package ru.zherikhov.data;

import java.util.HashMap;
import java.util.Map;

public class MyUser {
    private long userId;

    private String userName;
    private String firstName;
    private String lastName;

    private String currencyFrom;
    private String currencyTo;

    private boolean waitMessage;

    private Map<String, String> tempForCurrencies = new HashMap<>();

    public MyUser(long idUser, String userName, String firstName, String lastName) {
        this.userId = idUser;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Map<String, String> getTempForCurrencies() {
        return tempForCurrencies;
    }

    public void setTempForCurrencies(String type, String value) {
        this.tempForCurrencies.put(type, value);
    }

    public void resetTempFoeCurrencies() {
        this.tempForCurrencies.clear();
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public boolean isWaitMessage() {
        return waitMessage;
    }

    public void setWaitMessage(boolean waitMessage) {
        this.waitMessage = waitMessage;
    }
}
