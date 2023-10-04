package com.example.javafx;

public interface SignUpInformation {

    //Name
    final int minSymbolsNameField = 2;
    final int maxSymbolsNameField = 30;

    //Surname
    final int minSymbolsSurnameField = 2;
    final int maxSymbolsSurnameField = 48;

    //Nickname
    final int minSymbolsNicknameField = 5;
    final int maxSymbolsNicknameField = 60;

    //Password
    final int minSymbolsPasswordField = 5;
    final int maxSymbolsPasswordField = 128;

}
