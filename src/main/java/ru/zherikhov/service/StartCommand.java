package ru.zherikhov.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.zherikhov.buttonObject.buttonsCreate.KeyboardButton;
import ru.zherikhov.buttonObject.names.KeyboardsButtonNames;

public class StartCommand {
    KeyboardButton keyboardButtons = new KeyboardButton();
    SendMessageController messageController = new SendMessageController();

    private final static String helloMessage = "Красивое\n" + //TODO переделать на чтение файла
            "   приветственное\n" +
            "       окно";

    public SendMessage start(Update update) {
        SendMessage sendMessage = messageController.createMessage(update, helloMessage);

        ReplyKeyboardMarkup keyboardMarkup =
                keyboardButtons.setButtons(keyboardButtons.createKeyboardButtons(new KeyboardsButtonNames().keyboardRows));

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }
}
