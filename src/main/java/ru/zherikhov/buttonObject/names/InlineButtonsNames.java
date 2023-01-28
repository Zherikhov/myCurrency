package ru.zherikhov.buttonObject.names;

import java.util.ArrayList;
import java.util.List;

public class InlineButtonsNames {

    public final List<String> parsers = new ArrayList<>();

    {
        parsers.add("Биржевой");
        parsers.add("В разработке");
    }

    public final List<String> parsersLive = new ArrayList<>();

    {
        parsersLive.add("Биржевой (live)");
        parsersLive.add("В разработке (live)");
    }

    public List<List<String>> InlineRows = new ArrayList<>();

    {
        InlineRows.add(parsers);
        InlineRows.add(parsersLive);
    }

    public final List<String> currency = new ArrayList<>();

    {
        currency.add("USD");
        currency.add("EUR");
        currency.add("RUB");
        currency.add("GEL");
        currency.add("ARS");
    }

    public List<List<String>> InlineRowsOfSchedule = new ArrayList<>();

    {
        InlineRowsOfSchedule.add(parsers);
    }

    public final List<String> firstForSetting = new ArrayList<>();

    {
        firstForSetting.add("За каким курсом я слежу?");
    }

    public final List<String> secondForSetting = new ArrayList<>();

    {
        secondForSetting.add("Посмотреть график");
    }

    public final List<String> thirdForSetting = new ArrayList<>();

    {
        thirdForSetting.add("Поблагодарить разработчика");
    }

    public final List<String> fourthForSetting = new ArrayList<>();

    {
        fourthForSetting.add("О программе");
    }

    public List<List<String>> settings = new ArrayList<>();

    {
        settings.add(firstForSetting);
        settings.add(secondForSetting);
        settings.add(thirdForSetting);
        settings.add(fourthForSetting);
    }
}
