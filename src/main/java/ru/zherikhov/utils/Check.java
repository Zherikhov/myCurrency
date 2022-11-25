package ru.zherikhov.utils;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.Optional;

public class Check {
    //Проверка ввел ли пользователь bot команду и вернуть ее при наличии
    public String checkCommand(Message message) {
        String command = null;
        if (message != null && message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
            }
        }
        return command;
    }
}
