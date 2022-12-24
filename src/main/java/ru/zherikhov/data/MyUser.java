package ru.zherikhov.data;

public class MyUser {
    private long userId;
    private float rate;

    private String userName;
    private String firstName;
    private String lastName;

    private String currencyFrom;
    private String currencyTo;

    private boolean waitFeedback;
    private boolean waitCouple;
    private boolean waitSumForSchedule;
    private boolean waitRate;

    public MyUser(long idUser, String userName, String firstName, String lastName) {
        this.userId = idUser;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public boolean isWaitRate() {
        return waitRate;
    }

    public void setWaitRate(boolean waitRate) {
        this.waitRate = waitRate;
    }
}
