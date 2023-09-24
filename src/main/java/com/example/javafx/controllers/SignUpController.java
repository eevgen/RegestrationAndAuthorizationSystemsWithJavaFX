package com.example.javafx.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.javafx.DBConnection.ConnectionWithDB;
import com.example.javafx.DBConnection.DBConsts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class SignUpController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox signUpCheckBoxFemale;

    @FXML
    private CheckBox signUpCheckBoxMale;

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

    private static final String insertQuery = String.format("insert into registration (%s, %s, %s, %s) values (?, ?, ?, ?)",
            DBConsts.NAME_TABLE, DBConsts.SURNAME_TABLE, DBConsts.NICKNAME_TABLE, DBConsts.PASSWORD_TABLE);

    @FXML
    void initialize() {
        signUpLogInButton.setOnAction(event -> {
            signUpLogInButton.getScene().getWindow().hide();
            loadHelloViewScene();
        });

        signUpSignUpButton.setOnAction(eventForSignUpButtonInSignUpWindow -> {
            String name = signUpName.getText().trim();
            String surname = signUpSurname.getText().trim();
            String nickname = signUpNickname.getText().trim();
            String password = signUpPassword.getText().trim();
            if (name.isEmpty() || surname.isEmpty() || nickname.isEmpty() || password.isEmpty()) {
                System.err.println("You haven't entered all data!!!");
            }
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
}
