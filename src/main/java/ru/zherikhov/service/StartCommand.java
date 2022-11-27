package ru.zherikhov.service;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.zherikhov.buttonObject.buttonsCreate.KeyboardButton;
import ru.zherikhov.buttonObject.names.KeyboardsButtonNames;

public class StartCommand {
    KeyboardButton keyboardButtons = new KeyboardButton();
    SendMessageController messageController = new SendMessageController();

    private final static String helloMessage = "<code>Привет!\n" +
            "\n" +
            "Данный бот предназначен для конвертации валют,\n" +
            "а так же, ты можешь задать интересующий\n" +
            "тебя курс, и как только он сравняется или\n" +
            "станет меньше твоего параметра, то бот\n" +
            "тут же сообщит тебе об этом.\n" +
            "\n" +
            "С наилучшими пожеланиями от разработчика!</code>";

    public SendMessage start(Update update) {
        SendMessage sendMessage = messageController.createMessage(update, helloMessage);
        sendMessage.setParseMode(ParseMode.HTML);

        ReplyKeyboardMarkup keyboardMarkup =
                keyboardButtons.setButtons(keyboardButtons.createKeyboardButtons(new KeyboardsButtonNames().keyboardRows));

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }
}
