package ru.zherikhov.service;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendMessageController {
    public SendMessage createMessage(Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasCallbackQuery()) {
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        } else {
            sendMessage.setChatId(update.getMessage().getChatId());
        }

        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    public EditMessageText editInlineMessage(Update update, String text) {
        EditMessageText editMessageText = new EditMessageText();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        editMessageText.setParseMode(ParseMode.HTML);
        return editMessageText;
    }

    public SendMessage createMessageFromVlad(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(691103949L);
        sendMessage.setText("<b>" + text + "</b>");
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    public SendMessage createMessageFromUser(long telegramId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramId);
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }
}
