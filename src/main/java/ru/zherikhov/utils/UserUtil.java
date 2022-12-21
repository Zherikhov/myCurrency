package ru.zherikhov.utils;

import ru.zherikhov.data.MyUser;

import java.util.List;

public class UserUtil {
    public static int findUserId(List<MyUser> myUsers, long myUserId) {
        int userIdFromList = 0;
        for (int i = 0; i < myUsers.size(); i++) {
            if (myUsers.get(i).getUserId() == myUserId) {
                userIdFromList = i;
                break;
            }
        }
        return userIdFromList;
    }
}
