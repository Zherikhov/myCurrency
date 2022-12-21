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
    private String currencyCouple;
    private int currencyCoupleValue;

    private boolean waitFeedback;
    private boolean waitCouple;
    private boolean waitSumForSchedule;

    private final Map<String, String> tempForCurrencies = new HashMap<>();
    //private final List<String> saveCurrency = new ArrayList<>();
    private final Map<String, Integer> saveCurrency = new HashMap<>();

    public MyUser(long idUser, String userName, String firstName, String lastName) {
        this.userId = idUser;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public MyUser(long userId, String currencyCouple, int currencyCoupleValue) {
        this.userId = userId;
        this.currencyCouple = currencyCouple;
        this.currencyCoupleValue = currencyCoupleValue;
    }

//    public List<String> getSaveCurrency() {
//        return saveCurrency;
//    }
//
//    public void setSaveCurrency(String value) {
//        this.saveCurrency.add(value);
//    }

    public void setSaveCurrency(String key, Integer value) {
        this.saveCurrency.put(key, value);
    }

    public Map<String, Integer> getSaveCurrency() {
        return saveCurrency;
    }

    public void removeSaveCurrency(String type) {
        this.saveCurrency.remove(type);
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

    public boolean isWaitFeedback() {
        return waitFeedback;
    }

    public void setWaitFeedback(boolean waitFeedback) {
        this.waitFeedback = waitFeedback;
    }

    public boolean isWaitSumForSchedule() {
        return waitSumForSchedule;
    }

    public void setWaitSumForSchedule(boolean waitSumForSchedule) {
        this.waitSumForSchedule = waitSumForSchedule;
    }

    public boolean isWaitCouple() {
        return waitCouple;
    }

    public void setWaitCouple(boolean waitCouple) {
        this.waitCouple = waitCouple;
    }

    public String getCurrencyCouple() {
        return currencyCouple;
    }

    public void setCurrencyCouple(String currencyCouple) {
        this.currencyCouple = currencyCouple;
    }

    public int getCurrencyCoupleValue() {
        return currencyCoupleValue;
    }

    public void setCurrencyCoupleValue(int currencyCoupleValue) {
        this.currencyCoupleValue = currencyCoupleValue;
    }
}
