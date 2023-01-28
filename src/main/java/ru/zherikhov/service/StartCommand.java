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

    public final static String helloMessage = "Приветствую!\n" +
            "\n" +
            "Данный бот предназначен для отслеживания курса валют. Нажимайте <b>Узнать курс</b>, указывайте пару валют, " +
            "выбирайте откуда будут загружаться данные и смотрите актуальный курс.\n" +
            "\n" +
            "Не удобно постоянно мониторить и общаться с ботом? Не беда!\n" +
            "Зайдите в меню <b>Отслеживать курс</b>, следуйте инструкциям, и бот сам напишет Вам, когда сработают заданные правила.\n" +
            "\n" +
            "В меню <b>Информация</b> вы так же сможете найти много интересного, а если Вам чего-то не хватает или Вы просто захотите " +
            "оставить обратную связь, тогда тут пригодится пункт меню <b>Обратная связь</b>. Удобного использования.\n" +
            "\n" +
            "С наилучшими пожеланиями от разработчика!";

    public SendMessage start(Update update) {
        SendMessage sendMessage = messageController.createMessage(update, helloMessage);
        sendMessage.setParseMode(ParseMode.HTML);

        ReplyKeyboardMarkup keyboardMarkup =
                keyboardButtons.setButtons(keyboardButtons.createKeyboardButtons(new KeyboardsButtonNames().keyboardRows));

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }
}
