package ru.zherikhov.buttonObject.names;

import java.util.ArrayList;
import java.util.List;

public class KeyboardsButtonNames {

    private final List<String> keyboardRowFirst = new ArrayList<>();

    {
        keyboardRowFirst.add("Узнать курс");
        keyboardRowFirst.add("Назначить расписание");
    }

    private final List<String> keyboardRowSecond = new ArrayList<>();

    {
        keyboardRowSecond.add("Обратная связь");
    }

    public List<List<String>> keyboardRows = new ArrayList<>();

    {
        keyboardRows.add(keyboardRowFirst);
        keyboardRows.add(keyboardRowSecond);
    }
}
