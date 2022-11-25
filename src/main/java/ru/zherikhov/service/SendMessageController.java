package ru.zherikhov.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
}
