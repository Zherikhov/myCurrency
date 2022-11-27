package ru.zherikhov.utils;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.zherikhov.data.MyUser;

import java.util.List;
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

    public boolean checkUser(List<MyUser> myUserList, long userId) {
        boolean check = false;
        for (MyUser user : myUserList) {
            if (user.getUserId() == userId) {
                check = true;
                break;
            }
        }
        return check;
    }

    public static int findUserId(List<MyUser> myUsers, long myUserId) {
        int userId = 0;
        for (int i = 0; i < myUsers.size(); i++) {
            if (myUsers.get(i).getUserId() == myUserId) {
                userId = i;
            }
        }
        return userId;
    }
}
