package ru.zherikhov.connector;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static ru.zherikhov.connector.Const.*;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    @SneakyThrows
    public Connection getDbConnection() {
        String connectionUrl = "jdbc:postgresql://localhost:5432/" + dbName;
        Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();

        dbConnection = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
        return dbConnection;
    }

    @SneakyThrows
    public void newUser(long idTelegram, String userName, String firstName, String lastName) {
        String insert = "INSERT INTO " + USER_TABLE + "(" + ID_TELEGRAM + "," + USER_NAME + "," +
                FIRST_NAME + "," + LAST_NAME + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setInt(1, (int) idTelegram);
        preparedStatement.setString(2, userName);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, lastName);

        preparedStatement.executeUpdate();
    }

    @SneakyThrows
    public ResultSet findUser(long id) {
        ResultSet resultSet;
        String insert = "SELECT * FROM users WHERE id_telegram = " + id;
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
}
