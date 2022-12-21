package ru.zherikhov.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.zherikhov.buttonObject.buttonsCreate.InlineKeyButton;

import java.util.List;

public class InlineKeyButtonService { //TODO - нужно объеденить setInlineButtonDouble и setInlineButtonInLine в один
    InlineKeyButton inlineKeyButtons = new InlineKeyButton();
    SendMessageController sendMessageController = new SendMessageController();
    InlineKeyboardMarkup keyboardMarkup;

    public SendMessage setInlineButtonDouble(Update update, String text, List<String> names) {

        SendMessage sendMessage = sendMessageController.createMessage(update, text);
        keyboardMarkup =
                inlineKeyButtons.setKeyboardMarkup(inlineKeyButtons.createInlineButtonDouble(names));

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage setInlineButtonInLine(Update update, String text, List<List<String>> names) {

        SendMessage sendMessage = sendMessageController.createMessage(update, text);
        keyboardMarkup =
                inlineKeyButtons.setKeyboardMarkup(inlineKeyButtons.createInlineButton(names));

        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

}
