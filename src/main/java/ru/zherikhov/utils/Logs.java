package ru.zherikhov.utils;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class Logs {
    public static String sendConsoleLog(Update update) {
        User user;
        if (update.hasCallbackQuery()) {
            user = update.getCallbackQuery().getFrom();
        } else {
            user = update.getMessage().getFrom();
        }
        return user.getId() + ", " + user.getUserName() + ", " + user.getFirstName() + ", " + user.getLastName()
                + ", " + Date.getSourceDate();
    }
}
