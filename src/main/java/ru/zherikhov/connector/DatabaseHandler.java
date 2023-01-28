package ru.zherikhov.connector;

import lombok.SneakyThrows;
import ru.zherikhov.App;
import ru.zherikhov.utils.Date;
import ru.zherikhov.utils.PropertyUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import static ru.zherikhov.connector.Const.*;

public class DatabaseHandler {
    Connection dbConnection;

    @SneakyThrows
    public Connection getDbConnection() {
        String dbName = PropertyUtil.getProperties("D:\\Projects\\myCurrency\\src\\main\\resources\\config.properties", "db.name");
        String dbUser = PropertyUtil.getProperties("D:\\Projects\\myCurrency\\src\\main\\resources\\config.properties", "db.user");
        String dbPass = PropertyUtil.getProperties("D:\\Projects\\myCurrency\\src\\main\\resources\\config.properties", "db.password");

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
        App.LOGGER.info("ADDED new user " + idTelegram);
    }

    @SneakyThrows
    public ResultSet findUser(long id) {
        ResultSet resultSet;
        String insert = "SELECT * FROM users WHERE id_telegram = " + id;
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        resultSet = preparedStatement.executeQuery();
        return resultSet;

    }

    @SneakyThrows
    public ResultSet getAllSchedulers() {
        ResultSet resultSet;
        String insert = "SELECT * FROM " + CURRENCY_FROM_USER;
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }


    @SneakyThrows
    public void setCurrenciesInDataBaseForUsd(HashMap<String, String> hashMap) {
        String insert = "INSERT INTO " + CURRENCY_USD_TABLE + "(" + USD_EUR + "," + USD_RUB + "," +
                USD_GEL + "," + USD_ARS + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, hashMap.get(USD_EUR.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(2, hashMap.get(USD_RUB.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(3, hashMap.get(USD_GEL.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(4, hashMap.get(USD_ARS.toUpperCase().replaceAll("_", "")));

        preparedStatement.executeUpdate();
        App.LOGGER.info("Added a value in DB for USD");
    }

    @SneakyThrows
    public void setCurrenciesInDataBaseForEur(HashMap<String, String> hashMap) {
        String insert = "INSERT INTO " + CURRENCY_EUR_TABLE + "(" + EUR_USD + "," + EUR_RUB + "," +
                EUR_GEL + "," + EUR_ARS + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, hashMap.get(EUR_USD.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(2, hashMap.get(EUR_RUB.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(3, hashMap.get(EUR_GEL.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(4, hashMap.get(EUR_ARS.toUpperCase().replaceAll("_", "")));

        preparedStatement.executeUpdate();
        App.LOGGER.info("Added a value in DB for EUR");
    }

    @SneakyThrows
    public void setCurrenciesInDataBaseForRub(HashMap<String, String> hashMap) {
        String insert = "INSERT INTO " + CURRENCY_RUB_TABLE + "(" + RUB_EUR + "," + RUB_USD + "," +
                RUB_GEL + "," + RUB_ARS + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, hashMap.get(RUB_EUR.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(2, hashMap.get(RUB_USD.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(3, hashMap.get(RUB_GEL.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(4, hashMap.get(RUB_ARS.toUpperCase().replaceAll("_", "")));

        preparedStatement.executeUpdate();
        App.LOGGER.info("Added a value in DB for RUB");
    }

    @SneakyThrows
    public void setCurrenciesInDataBaseForGel(HashMap<String, String> hashMap) {
        String insert = "INSERT INTO " + CURRENCY_GEL_TABLE + "(" + GEL_EUR + "," + GEL_RUB + "," +
                GEL_USD + "," + GEL_ARS + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, hashMap.get(GEL_EUR.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(2, hashMap.get(GEL_RUB.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(3, hashMap.get(GEL_USD.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(4, hashMap.get(GEL_ARS.toUpperCase().replaceAll("_", "")));

        preparedStatement.executeUpdate();
        App.LOGGER.info("Added a value in DB for GEL");
    }

    @SneakyThrows
    public void setCurrenciesInDataBaseForArs(HashMap<String, String> hashMap) {
        String insert = "INSERT INTO " + CURRENCY_ARS_TABLE + "(" + ARS_EUR + "," + ARS_RUB + "," +
                ARS_GEL + "," + ARS_USD + ")" + "VALUES(?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, hashMap.get(ARS_EUR.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(2, hashMap.get(ARS_RUB.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(3, hashMap.get(ARS_GEL.toUpperCase().replaceAll("_", "")));
        preparedStatement.setString(4, hashMap.get(ARS_USD.toUpperCase().replaceAll("_", "")));

        preparedStatement.executeUpdate();
        App.LOGGER.info("Added a value in DB for ARS");
    }

    @SneakyThrows
    public ResultSet getLastCurrency(String currencyTable) {
        ResultSet resultSet;
        String insert = "SELECT * FROM " + currencyTable + " ORDER BY id DESC LIMIT 1";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);

        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    @SneakyThrows
    public void setDefaultRate(String couple, int rate, long idTelegram) {
        String insert = "INSERT INTO " + CURRENCY_FROM_USER + " (" + ID_TELEGRAM + "," + COUPLE + "," + RATE + ") VALUES ("
                + idTelegram + ",'" + couple + "'," + rate + ")";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.executeUpdate();
        App.LOGGER.info("Set default rate for " + idTelegram);
    }

    @SneakyThrows
    public void setRate(String couple, float rate, long idTelegram) {
        String insert = "UPDATE currency_from_user SET (couple, rate) = (?,?) WHERE id_telegram = ?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, couple);
        preparedStatement.setFloat(2, rate);
        preparedStatement.setLong(3, idTelegram);
        preparedStatement.executeUpdate();
        App.LOGGER.info("Set rate for " + idTelegram);
    }

    @SneakyThrows
    public ResultSet getRate(long idTelegram) {
        String insert = "SELECT couple, rate FROM currency_from_user WHERE id_telegram = (?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setLong(1, idTelegram);
        return preparedStatement.executeQuery();
    }
}
