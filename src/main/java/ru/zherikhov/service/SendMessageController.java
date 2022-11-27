package ru.zherikhov.service;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendMessageController {
    public SendMessage createMessage(Update update, String message) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasCallbackQuery()) {
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        } else {
            sendMessage.setChatId(update.getMessage().getChatId());
        }

        sendMessage.setText(message);
        return sendMessage;
    }

    public EditMessageText editInlineMessage(Update update, String text) {
        EditMessageText editMessageText = new EditMessageText();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("`" + text + "`");
        editMessageText.setParseMode(ParseMode.MARKDOWN);
        return editMessageText;
    }

    public SendMessage createMessageFromVlad(Update update, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(691103949L);
        sendMessage.setText("*" + message + "*");
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        return sendMessage;
    }
}
