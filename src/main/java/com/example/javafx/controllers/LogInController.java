package com.example.javafx.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import com.example.javafx.DBConnection.ConnectionWithDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LogInController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private Button logInButton;

    @FXML
    private TextField logInNicknameField;

    @FXML
    private Button signUpButton;
    @FXML
    private Text nicknameErrorMessage;

    @FXML
    private Text passwordErrorMessage;

    private static boolean isBlocked = false;
    private static boolean userFound;
    private static int numberOfTries = 1;
    @FXML
    void initialize() {



        signUpButton.setOnAction(event -> {
            signUpButton.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/javafx/signUpWindow.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = new Stage();
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
            stage.show();
        });

        logInButton.setOnAction(event -> {
            try {
                passwordErrorMessage.setText("");
                nicknameErrorMessage.setText("");

                String select = "select * from registration;";

                Connection connection = (new ConnectionWithDB()).connectionSetter();
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                ResultSet resultSet = preparedStatement.executeQuery();
                String nickname = logInNicknameField.getText().trim();
                String password = PasswordField.getText().trim();
                if(!isBlocked) {
                    checkingAllFields(resultSet, nickname, password);
                }
                if(isBlocked) {
                    passwordErrorMessage.setText("A lot of tries, try again later.");
                }
                if(userFound && !isBlocked) {
                    logInButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/javafx/logInButtonResult.fxml"));
                    loader.load();
                    Stage stage = new Stage();
                    Parent root = loader.getRoot();
                    stage.setScene(new Scene(root));
                    stage.show();
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void checkingAllFields(ResultSet resultSet, String nickname, String password) throws SQLException{
        int randomNumberOfMaxTrials = (new Random()).nextInt(8, 10);
        boolean isCorrectNickname = false;
        while (resultSet.next()) {
            if(resultSet.getString(4).equals(nickname) && resultSet.getString(5).equals(password) &&
                    !resultSet.getString(4).equals("") && !resultSet.getString(5).equals("")) {
                userFound = true;
            }
            if (resultSet.getString(4).equals(nickname)){
                isCorrectNickname = true;
            }
            if (!resultSet.getString(5).equals(password) && resultSet.getString(4).equals(nickname)) {
                if (numberOfTries < randomNumberOfMaxTrials){
                    passwordErrorMessage.setText("The wrong password. Try again");
                    numberOfTries++;
                }
                else if (numberOfTries >= randomNumberOfMaxTrials) {
                    passwordErrorMessage.setText("A lot of tries, try again later.");
                    isBlocked = true;
                    numberOfTries++;
                }
            }
        }
        if(!isCorrectNickname) {
            nicknameErrorMessage.setText("There is not a user with that nickname");
        }
    }
}
