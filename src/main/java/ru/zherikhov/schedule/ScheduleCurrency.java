package ru.zherikhov.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.zherikhov.App;
import ru.zherikhov.connector.DatabaseHandler;
import ru.zherikhov.data.CurrencyValue;
import ru.zherikhov.service.SendMessageController;
import ru.zherikhov.utils.Date;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class ScheduleCurrency implements Runnable {
    private final App bot;
    private final SendMessageController sendMessageController = new SendMessageController();
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleCurrency.class);

    public ScheduleCurrency(App bot) {
        this.bot = bot;
    }

    DatabaseHandler db = new DatabaseHandler();


    @Override
    public void run() {
        try {
            if (Date.compareTime("22:00:00") > 0 && Date.compareTime("07:00:00") < 0) {
                LOGGER.info("Schedule have STARTED for send a message");
                ResultSet resultSet = db.getAllSchedulers();

                while (resultSet.next()) {
                    int idTelegram = resultSet.getInt(2);
                    float rate = resultSet.getFloat(4);
                    String couple = resultSet.getString(3);

                    if (rate != 0 && !couple.equals("")) {
                        String firstPart = couple.substring(0, 3);
                        String secondPart = couple.substring(3, 6);
                        String value;

                        switch (firstPart) {
                            case "USD":
                                value = CurrencyValue.UsdValues.get(couple);
                                if (Float.parseFloat(value) <= rate) {
                                    bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару USD -> " + secondPart +
                                            "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                                    App.LOGGER.info("Massage sent for " + idTelegram);
                                }
                                break;
                            case "EUR":
                                value = CurrencyValue.EurValues.get(couple);
                                if (Float.parseFloat(value) <= rate) {
                                    bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару EUR -> " + secondPart +
                                            "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                                    App.LOGGER.info("Massage sent for " + idTelegram);
                                }
                                break;
                            case "RUB":
                                value = CurrencyValue.RubValues.get(couple);
                                if (Float.parseFloat(value) <= rate) {
                                    bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару RUB -> " + secondPart +
                                            "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                                    App.LOGGER.info("Massage sent for " + idTelegram);
                                }
                                break;
                            case "GEL":
                                value = CurrencyValue.GelValues.get(couple);
                                if (Float.parseFloat(value) <= rate) {
                                    bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару GEL -> " + secondPart +
                                            "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                                    App.LOGGER.info("Massage sent for " + idTelegram);
                                }
                                break;
                            case "ARS":
                                value = CurrencyValue.ArsValues.get(couple);
                                if (Float.parseFloat(value) <= rate) {
                                    bot.execute(sendMessageController.createMessageFromUser(idTelegram, "Цена за пару ARS -> " + secondPart +
                                            "\nупала ниже установленной Вами суммы (<b>" + rate + "</b>), а именно - <b>" + value + "</b>"));
                                    App.LOGGER.info("Massage sent for " + idTelegram);
                                }
                                break;
                        }
                    }
                }
                LOGGER.info("Schedule finished for send message");
            } else {
                App.LOGGER.info("Sleeping...");
            }
        } catch (ParseException | SQLException | TelegramApiException e) {
            LOGGER.error("Schedule NOT finished correct for send a message", e);
            e.printStackTrace();
        }
    }
}


