package ru.zherikhov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.zherikhov.buttonObject.names.InlineButtonsNames;
import ru.zherikhov.connector.DatabaseHandler;
import ru.zherikhov.data.CurrencyValue;
import ru.zherikhov.data.MyUser;
import ru.zherikhov.schedule.ParsingExchange;
import ru.zherikhov.schedule.ScheduleCurrency;
import ru.zherikhov.service.ApiLayerService;
import ru.zherikhov.service.InlineKeyButtonService;
import ru.zherikhov.service.SendMessageController;
import ru.zherikhov.service.StartCommand;
import ru.zherikhov.utils.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App extends TelegramLongPollingBot {
    private final StartCommand startCommand = new StartCommand();
    private final DatabaseHandler db = new DatabaseHandler();
    public static final List<MyUser> myUsers = new ArrayList<>();
    private final InlineKeyButtonService inlineKeyButtonService = new InlineKeyButtonService();
    private MyUser myCurrentUser;
    private final SendMessageController sendMessageController = new SendMessageController();
    private final ApiLayerService apiLayerService = new ApiLayerService();
    long currentUserId = 0;
    public static final Logger LOGGER = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) {
        App bot = new App();
        TelegramBotsApi telegramBotsApi;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new ParsingExchange(), 1, 3, TimeUnit.HOURS);

        ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();
        scheduler2.scheduleAtFixedRate(new ScheduleCurrency(bot), 61, 61, TimeUnit.MINUTES);

        System.out.println("Загрузка выполнена в " + Date.getSourceDate());
    }

    @Override
    public void onUpdateReceived(Update update) {
        String command = Check.checkCommand(update.getMessage());
        if (command != null) {
            switch (command) {
                case "/start":
                    try {
                        execute(startCommand.start(update));
                    } catch (TelegramApiException e) {
                        LOGGER.error("TelegramApiException", e);
                        e.printStackTrace();
                    }
                    User user = update.getMessage().getFrom();
                    ResultSet resultSet = db.findUser(user.getId());

                    int count = 0;
                    while (true) {
                        try {
                            if (!resultSet.next()) break;
                        } catch (SQLException e) {
                            LOGGER.error("SQLException", e);
                            e.printStackTrace();
                        }
                        count++;
                    }

                    if (count == 0) {
                        db.newUser(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName());
                        db.setDefaultRate("", 0, user.getId());
                        LOGGER.info("User added in BD => " + Logs.sendConsoleLog(update));
                    }
                    break;
            }
        }

        //обязательная проверка пользователя на наличие в листе myUsers
        if (update.hasMessage()) {
            currentUserId = update.getMessage().getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            currentUserId = update.getCallbackQuery().getFrom().getId();
        }

        if (Check.checkUser(myUsers, currentUserId)) {
            App.addUser(update);
        }
        myCurrentUser = myUsers.get(UserUtil.findUserId(myUsers, currentUserId));

        //блок обработки - Обратная связь
        if (update.hasMessage() && update.getMessage().hasText() && myCurrentUser.isWaitFeedback() && !myCurrentUser.isWaitRate()) {
            try {
                execute(sendMessageController.createMessageFromVlad(update.getMessage().getText() + "\n\nСообщение от - " +
                        myCurrentUser.getUserId()));
                LOGGER.info("Message -> {} FROM {}", update.getMessage().getText(), myCurrentUser.getUserId());
                execute(sendMessageController.createMessage(update, "Сообщение доставлено, спасибо за обратную связь"));
                myCurrentUser.setWaitFeedback(false);
            } catch (TelegramApiException e) {
                LOGGER.error("TelegramApiException", e);
                e.printStackTrace();
            }
        }

        //блок
        if (update.hasMessage() && update.getMessage().hasText() && myCurrentUser.getRate() == 0.0 && myCurrentUser.isWaitRate()) {
            String temp = update.getMessage().getText().replaceAll("[^\\d.,]", "");
            temp = temp.replaceAll(",", ".");
            myCurrentUser.setRate(Float.parseFloat(temp));
        }
        if (update.hasMessage() && update.getMessage().hasText() && myCurrentUser.getRate() != 0 && myCurrentUser.isWaitRate()) {
            db.setRate(myCurrentUser.getCurrencyFrom() + myCurrentUser.getCurrencyTo(),
                    myCurrentUser.getRate(), myCurrentUser.getUserId());
            try {
                execute(sendMessageController.createMessage(update, "Отслеживание курса настроено!"));
            } catch (TelegramApiException e) {
                LOGGER.error("TelegramApiException", e);
                e.printStackTrace();
            }

            setNullFromCurrency(myCurrentUser);
            myCurrentUser.setWaitSumForSchedule(false);
            myCurrentUser.setWaitRate(false);
            myCurrentUser.setRate(0);
        }

        //блок обработки - Узнать курс и Отслеживать курс
        if (update.hasCallbackQuery() && (myCurrentUser.isWaitCouple() || myCurrentUser.isWaitSumForSchedule())) {
            String[] userSelectedCurrencies = update.getCallbackQuery().getData().split(":");

            if (userSelectedCurrencies[0].equals("original")) {
                myCurrentUser.setCurrencyFrom(userSelectedCurrencies[1]);
            } else if (userSelectedCurrencies[0].equals("target")) {
                myCurrentUser.setCurrencyTo(userSelectedCurrencies[1]);
            }

            if (update.getCallbackQuery().getData().equals("Биржевой (live)") && myCurrentUser.isWaitCouple()) {
                LOGGER.info("Используется 'Биржевой (live)' -> " + Logs.sendConsoleLog(update));

                User user = update.getCallbackQuery().getFrom();
                ResultSet resultSet = db.findUser(user.getId());
                int id = -1;
                try {
                    while (resultSet.next()) {
                        id = resultSet.getInt(6);
                    }

                    if (id == 1) {
                        execute(sendMessageController.editInlineMessage(update, "Информация на (время Московское): " + Date.getSourceDate() + "\n" +
                                apiLayerService.getLive(myCurrentUser.getCurrencyFrom(), myCurrentUser.getCurrencyTo())));
                    } else {
                        execute(sendMessageController.editInlineMessage(update, "У вас нет подписки, что бы воспользоваться функцией"));
                    }
                } catch (TelegramApiException | SQLException e) {
                    LOGGER.error("TelegramApiException/SQLException", e);
                    e.printStackTrace();
                }

                myCurrentUser.setWaitCouple(false);
                setNullFromCurrency(myCurrentUser);
            } else if (update.getCallbackQuery().getData().equals("Биржевой") && myCurrentUser.isWaitCouple()) {
                LOGGER.info("Используется 'Биржевой' " + Logs.sendConsoleLog(update));

                try {
                    switch (myCurrentUser.getCurrencyFrom()) {
                        case "USD":
                            execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): " +
                                    Date.getSourceDate() + "\n" + CurrencyValue.UsdValues.get(myCurrentUser.getCurrencyFrom() +
                                    myCurrentUser.getCurrencyTo())));
                            break;
                        case "EUR":
                            execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                                    + Date.getSourceDate() + "\n" + CurrencyValue.EurValues.get(myCurrentUser.getCurrencyFrom() +
                                    myCurrentUser.getCurrencyTo())));
                            break;
                        case "RUB":
                            execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                                    + Date.getSourceDate() + "\n" + CurrencyValue.RubValues.get(myCurrentUser.getCurrencyFrom() +
                                    myCurrentUser.getCurrencyTo())));
                            break;
                        case "GEL":
                            execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                                    + Date.getSourceDate() + "\n" + CurrencyValue.GelValues.get(myCurrentUser.getCurrencyFrom() +
                                    myCurrentUser.getCurrencyTo())));
                            break;
                        case "ARS":
                            execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                                    + Date.getSourceDate() + "\n" + CurrencyValue.ArsValues.get(myCurrentUser.getCurrencyFrom() +
                                    myCurrentUser.getCurrencyTo())));
                            break;
                    }
                } catch (TelegramApiException e) {
                    LOGGER.error("TelegramApiException", e);
                    e.printStackTrace();
                }
                setNullFromCurrency(myCurrentUser);
                myCurrentUser.setWaitCouple(false);
            } else if (update.getCallbackQuery().getData().equals("Биржевой") && myCurrentUser.isWaitSumForSchedule()) {
                LOGGER.info("Используется 'Биржевой (live)' -> " + Logs.sendConsoleLog(update));

                if (myCurrentUser.isWaitRate()) {

                } else {
                    try {
                        execute(sendMessageController.editInlineMessage(update, "Укажите желаемый курс"));
                    } catch (TelegramApiException e) {
                        LOGGER.error("TelegramApiException", e);
                        e.printStackTrace();
                    }
                    myCurrentUser.setWaitRate(true);
                }
            }

            if (myCurrentUser.getCurrencyFrom() != null && myCurrentUser.getCurrencyTo() != null && !myCurrentUser.isWaitRate()) {
                LOGGER.info("Используется 'Биржевой' -> " + Logs.sendConsoleLog(update));

                try {
                    execute(sendMessageController.editInlineMessage(update, myCurrentUser.getCurrencyFrom() +
                            "\n             ->\n                    " + myCurrentUser.getCurrencyTo()));
                    if (myCurrentUser.isWaitSumForSchedule()) {
                        execute(inlineKeyButtonService.setInlineButtonInLine(update, "Выберите курс", new InlineButtonsNames().InlineRowsOfSchedule));
                    } else if (myCurrentUser.isWaitCouple()) {
                        execute(inlineKeyButtonService.setInlineButtonInLine(update, "Выберите курс", new InlineButtonsNames().InlineRows));
                    }
                } catch (TelegramApiException e) {
                    LOGGER.error("TelegramApiException", e);
                    e.printStackTrace();
                }
            }
        }

        if (update.hasCallbackQuery()) {
            try {
                if (update.getCallbackQuery().getData().equals("За каким курсом я слежу?")) {
                    String couple = null;
                    float rate = 0;
                    ResultSet resultSet = db.getRate(currentUserId);
                    while (resultSet.next()) {
                        couple = resultSet.getString(1);
                        rate = resultSet.getFloat(2);
                    }
                    execute(sendMessageController.editInlineMessage(update, "Вы указали " + rate + " для пары " + couple));
                } else if (update.getCallbackQuery().getData().equals("О программе")) {
                    execute(sendMessageController.editInlineMessage(update, new StartCommand().helloMessage));
                }
            } catch (TelegramApiException | SQLException e) {
                LOGGER.error("Информация" + e);
                e.printStackTrace();
            }
        }

        //блок if для запуска логики с KeyboardButton и установки тригера "wait"
        if (update.hasMessage() && update.getMessage().hasText()) {

            try {
                switch (update.getMessage().getText()) {
                    case "Узнать курс":
                        LOGGER.info("Узнать курс -> " + Logs.sendConsoleLog(update));
                        execute(inlineKeyButtonService.setInlineButtonDouble(
                                update, "Выберите пару:", new InlineButtonsNames().currency));
                        myCurrentUser.setWaitCouple(true);
                        break;
                    case "Отслеживать курс":
                        LOGGER.info("Отслеживать курс -> " + Logs.sendConsoleLog(update));
                        execute(inlineKeyButtonService.setInlineButtonDouble(
                                update, "Выберите пару:", new InlineButtonsNames().currency));
                        myCurrentUser.setWaitSumForSchedule(true);
                        break;
                    case "Обратная связь":
                        LOGGER.info("Обратная связь -> " + Logs.sendConsoleLog(update));
                        execute(sendMessageController.createMessage(update, "Следующее сообщение будет отправлено автору бота,\n" +
                                "Но помни - <i>художника обидеть может каждый, не каждый может выйти из бана :)</i>"));
                        myCurrentUser.setWaitFeedback(true);
                        break;
                    case "Информация":
                        LOGGER.info("Инфорамция -> " + Logs.sendConsoleLog(update));
                        execute(inlineKeyButtonService.setInlineButton(
                                update, "Выберите интересующую информацию:", new InlineButtonsNames().settings));
                        break;
                }
            } catch (TelegramApiException e) {
                LOGGER.error("TelegramApiException", e);
                e.printStackTrace();
            }
        }
    }

    private static void setNullFromCurrency(MyUser myUser) {
        myUser.setCurrencyTo(null);
        myUser.setCurrencyFrom(null);
    }

    private static void addUser(Update update) {
        User user = update.getMessage().getFrom();
        MyUser myUser = new MyUser(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName());
        myUsers.add(myUser);
        LOGGER.info("Пользователь добавлен в лист myUsers -> " + Logs.sendConsoleLog(update));
    }

    @Override
    public String getBotUsername() {
        return PropertyUtil.getProperties("D:\\Projects\\myCurrency\\src\\main\\resources\\config.properties", "telegram.userName");
    }

    @Override
    public String getBotToken() {
        return PropertyUtil.getProperties("D:\\Projects\\myCurrency\\src\\main\\resources\\config.properties", "telegram.token");
    }
}
