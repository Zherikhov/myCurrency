package ru.zherikhov.schedule;

import lombok.SneakyThrows;
import ru.zherikhov.App;
import ru.zherikhov.connector.DatabaseHandler;
import ru.zherikhov.data.CurrencyValue;
import ru.zherikhov.service.SendMessageController;
import ru.zherikhov.utils.Date;

import java.sql.ResultSet;

public class ScheduleCurrency implements Runnable {
    private final App bot;
    private final SendMessageController sendMessageController = new SendMessageController();

    public ScheduleCurrency(App bot) {
        this.bot = bot;
    }

    DatabaseHandler db = new DatabaseHandler();

    @SneakyThrows
    @Override
    public void run() {
        if (Date.compareTime("22:00:00") > 0 && Date.compareTime("07:00:00") < 0) {
            ResultSet resultSet = db.getAllSchedulers();

            while (resultSet.next()) {
                int idTelegram = resultSet.getInt(2);
                float rate = resultSet.getFloat(4);
                String couple = resultSet.getString(3);

                String firstPart = couple.substring(0, 3);
                String secondPart = couple.substring(3, 6);
                String value;

                switch (firstPart) {
                    case "USD":
                        value = CurrencyValue.UsdValues.get(couple);
                        if (Float.parseFloat(value) <= rate) {
                            bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару USD -> " + secondPart +
                                    "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                        }
                        break;
                    case "EUR":
                        value = CurrencyValue.EurValues.get(couple);
                        if (Float.parseFloat(value) <= rate) {
                            bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару EUR -> " + secondPart +
                                    "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                        }
                        break;
                    case "RUB":
                        value = CurrencyValue.RubValues.get(couple);
                        if (Float.parseFloat(value) <= rate) {
                            bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару RUB -> " + secondPart +
                                    "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                        }
                        break;
                    case "GEL":
                        value = CurrencyValue.GelValues.get(couple);
                        if (Float.parseFloat(value) <= rate) {
                            bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару GEL -> " + secondPart +
                                    "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                        }
                        break;
                    case "ARS":
                        value = CurrencyValue.ArsValues.get(couple);
                        if (Float.parseFloat(value) <= rate) {
                            bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару ARS -> " + secondPart +
                                    "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                        }
                        break;
                }
            }
            System.out.println("Расписание отработало в " + Date.getSourceDate());
        }
    }
}


