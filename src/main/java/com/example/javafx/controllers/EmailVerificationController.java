package com.example.javafx.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EmailVerificationController extends SignUpController{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToSignUpButton;

    @FXML
    private TextField signUpNickname;

    @FXML
    private Button signUpSignUpButton;

    @FXML
    void initialize() {
        backToSignUpButton.setOnAction(action -> loadScene("signUpWindow.fxml", backToSignUpButton));
    }

}
