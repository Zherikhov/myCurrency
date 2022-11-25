package ru.zherikhov.buttonObject.buttonsCreate;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyButton {
    //Создание объектов InlineKeyboardButton с задублированием
    public List<List<InlineKeyboardButton>> createInlineButtonDouble(List<String> buttonsNames) {
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();

        for (String buttonName : buttonsNames) {
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                keyboardRow.add(new InlineKeyboardButton());
                keyboardRow.get(keyboardRow.size() - 1).setText(buttonName);
                //Установка target и original в CallbackData для дальнейшей работы с парами валют
                if (i == 0) {
                    keyboardRow.get(keyboardRow.size() - 1).setCallbackData("target:" + buttonName);
                } else {
                    keyboardRow.get(keyboardRow.size() - 1).setCallbackData("original:" + buttonName);
                }
            }
            keyboardList.add(keyboardRow);
        }
        return keyboardList;
    }

    //Создание объектов InlineKeyboardButton в одину строку
    public List<List<InlineKeyboardButton>> createInlineButtonInLine(List<String> buttonsNames) {
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();

        for (String buttonName : buttonsNames) {
            keyboardRow.add(new InlineKeyboardButton());
            keyboardRow.get(keyboardRow.size() - 1).setText(buttonName);
            keyboardRow.get(keyboardRow.size() - 1).setCallbackData(buttonName);
        }
        keyboardList.add(keyboardRow);
        return keyboardList;
    }

    //Добавление InlineKeyboardButtons на Markup панель
    public InlineKeyboardMarkup setKeyboardMarkup(List<List<InlineKeyboardButton>> keyboardList) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboardList);
        return inlineKeyboardMarkup;
    }
}
