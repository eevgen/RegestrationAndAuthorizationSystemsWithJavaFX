package com.example.javafx.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.javafx.DBConnection.ConnectionWithDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
                String select = "select * from registration;";

                Connection connection = (new ConnectionWithDB()).connectionSetter();
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                ResultSet resultSet = preparedStatement.executeQuery();
                String nickname = logInNicknameField.getText().trim();
                String password = PasswordField.getText().trim();

                boolean userFound = false;

                while (resultSet.next()) {
                    if(resultSet.getString(4).equals(nickname) && resultSet.getString(5).equals(password)) {
                        userFound = true;
                        break;
                    }
                }
                if(userFound) {
                    logInButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/javafx/logInButtonResult.fxml"));
                    loader.load();
                    Stage stage = new Stage();
                    Parent root = loader.getRoot();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    System.out.println("User not found or incorrect credentials.");
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
