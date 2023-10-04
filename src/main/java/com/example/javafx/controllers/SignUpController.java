package com.example.javafx.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.IntStream;

import com.example.javafx.CorrectStructure;
import com.example.javafx.DBConnection.ConnectionWithDB;
import com.example.javafx.DBConnection.DBConsts;
import com.example.javafx.SignUpInformation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class SignUpController implements SignUpInformation {

    @FXML
    private Text nameErrorMessage;

    @FXML
    private Text nicknameErrorMessage;

    @FXML
    private Text passwordErrorMessage;

    @FXML
    private Button signUpLogInButton;

    @FXML
    private TextField signUpName;

    @FXML
    private TextField signUpNickname;

    @FXML
    private PasswordField signUpPassword;

    @FXML
    private Button signUpSignUpButton;

    @FXML
    private TextField signUpSurname;

    @FXML
    private Text surnameErrorMessage;
    @FXML
    private Text globalErrorsText;
    private final LinkedList<String> listOfEmptyFields = new LinkedList<>();

    private static final String insertQuery = String.format("insert into registration (%s, %s, %s, %s) values (?, ?, ?, ?)",
            DBConsts.NAME_TABLE, DBConsts.SURNAME_TABLE, DBConsts.NICKNAME_TABLE, DBConsts.PASSWORD_TABLE);

    @FXML
    void initialize() {
        signUpLogInButton.setOnAction(event -> {
            signUpLogInButton.getScene().getWindow().hide();
            loadHelloViewScene();
        });

        signUpSignUpButton.setOnAction(eventForSignUpButtonInSignUpWindow -> {
            setToDefaultFields(signUpName, signUpNickname, signUpSurname, signUpPassword);
            setToDefaultTextErrorsMessages(nameErrorMessage, surnameErrorMessage, nicknameErrorMessage, passwordErrorMessage, globalErrorsText);
            String name = signUpName.getText();
            String surname = signUpSurname.getText();
            String nickname = signUpNickname.getText();
            String password = signUpPassword.getText();
            if (!theSameSurnameAndName(name, surname) && checkAllParameters(name, surname, nickname, password)) {
                try (Connection connection = (new ConnectionWithDB()).connectionSetter();
                     PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, surname);
                    preparedStatement.setString(3, nickname);
                    preparedStatement.setString(4, password);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("All information has been transferred into the DB");
                        signUpSignUpButton.getScene().getWindow().hide();
                        loadHelloViewScene();
                    } else {
                        System.err.println("No rows were inserted into the DB");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private void loadHelloViewScene() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/javafx/hello-view.fxml"));
        try {
            loader.load();
            Stage stage = new Stage();
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean checkAllParameters(String name, String surname, String nickname, String password) {
        CorrectStructure correctStructure = new CorrectStructure();
        boolean nameBoolean = fieldsTest(name, minSymbolsNameField, maxSymbolsNameField, correctStructure, nameErrorMessage, FieldName.NAME, signUpName);
        boolean surnameBoolean = fieldsTest(surname, minSymbolsSurnameField, maxSymbolsSurnameField, correctStructure, surnameErrorMessage, FieldName.SURNAME, signUpSurname);
        boolean nicknameBoolean = fieldsTest(nickname, minSymbolsNicknameField, maxSymbolsNicknameField, correctStructure, nicknameErrorMessage, FieldName.NICKNAME, signUpNickname);
        boolean passwordBoolean = fieldsTest(password, minSymbolsPasswordField, maxSymbolsPasswordField, correctStructure, passwordErrorMessage, FieldName.PASSWORD, signUpPassword);
        boolean errorMessages = testingListWithEmptyErrorMessages();
        if (!nameBoolean || !nicknameBoolean || !surnameBoolean || !passwordBoolean || !errorMessages) {
            return false;
        }
        return true;
    }

    private boolean fieldsTest(String word, int minAmountOfSymbols, int maxAmountOFSymbols, CorrectStructure correctStructure, Text textTable, FieldName fieldName,
                               TextField field) {
        boolean isOK = true;
        if(word.isEmpty()) {
            listOfEmptyFields.add(fieldName.getName());
            isOK = false;
        } else if (correctStructure.getMaxSortedSymbols(maxAmountOFSymbols, word)) {
            textTable.setText("You must have " + maxAmountOFSymbols + " symbols at max");
            isOK = false;
        } else if (correctStructure.getMinSortedSymbols(minAmountOfSymbols, word)) {
            textTable.setText("You must have " + minAmountOfSymbols + " symbols at least");
            isOK = false;
        }
        if (fieldName == FieldName.NAME || fieldName == FieldName.SURNAME) {
            if (correctStructure.containsSpace(word) && correctStructure.containsSpecialSymbols(word)) {
                textTable.setText("You have an extra space and special symbols");
                isOK = false;
            } else if (correctStructure.containsSpace(word)) {
                textTable.setText("You have an extra space");
                isOK = false;
            } else if (correctStructure.containsSpecialSymbols(word)) {
                textTable.setText("You have special symbols");
                isOK = false;
            }
        }
        if (fieldName == FieldName.NICKNAME || fieldName == FieldName.PASSWORD) {
            if (correctStructure.containsSpace(word)) {
                textTable.setText("You have an extra space");
                isOK = false;
            }
        }
        if (!isOK) {
            field.setStyle("-fx-border-color: #ff0000");
            return false;
        }
        return true;
    }

    private boolean testingListWithEmptyErrorMessages() {
        try {
            if (listOfEmptyFields.isEmpty()) {
                return true;
            } else if (listOfEmptyFields.size() == 1) {
                globalErrorsText.setText("The Field '" + listOfEmptyFields.getFirst() + "' is empty!!!");
            } else if (listOfEmptyFields.size() >= 4) {
                globalErrorsText.setText("Every field is empty!!!");
            } else {
                StringJoiner joiner = new StringJoiner(", ");
                listOfEmptyFields.forEach(joiner::add);
                globalErrorsText.setText("The Fields '" + joiner.toString() + "' are empty!!!");
            }
            return false;
        } finally {
            listOfEmptyFields.clear();
        }
    }

    private boolean theSameSurnameAndName(String name, String surname) {
        if(name.equals(surname) && !name.isEmpty() && !surname.isEmpty()) {
            globalErrorsText.setText("The fields Name and Surname are the same!");
            signUpName.setStyle("-fx-border-color: #ff0000");
            signUpSurname.setStyle("-fx-border-color: #ff0000");
            return true;
        }
        return false;
    }

    private void setToDefaultFields(TextField... fields) {
        Arrays.stream(fields).forEach(field -> field.setStyle(""));
    }

    private void setToDefaultTextErrorsMessages(Text... texts) {
        Arrays.stream(texts).forEach(text -> text.setText(""));
    }

    public enum FieldName {
        NAME{
            public String getName() {
                return "Name";
            }
        },
        SURNAME {
            public String getName() {
                return "Surname";
            }
        },
        NICKNAME {
            public String getName() {
                return "Nickname";
            }
        },
        PASSWORD {
            public String getName() {
                return "Password";
            }
        };
        public abstract String getName();
    }
}
