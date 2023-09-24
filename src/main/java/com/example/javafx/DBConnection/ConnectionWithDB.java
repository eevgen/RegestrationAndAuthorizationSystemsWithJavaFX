package com.example.javafx.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionWithDB extends DBConfigs{
    public Connection connectionSetter() throws SQLException {
        Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPasword);
        return connection;
    }
}
