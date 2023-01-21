package ru.zherikhov;

import lombok.SneakyThrows;
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
import ru.zherikhov.utils.Check;
import ru.zherikhov.utils.Date;
import ru.zherikhov.utils.Logs;
import ru.zherikhov.utils.UserUtil;

import java.sql.ResultSet;
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
    private static final Logger logger = LoggerFactory.getLogger(App.class);

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
        scheduler.scheduleAtFixedRate(new ParsingExchange(), 0, 3, TimeUnit.HOURS);

        ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();
        scheduler2.scheduleAtFixedRate(new ScheduleCurrency(bot), 1, 61, TimeUnit.MINUTES);

        System.out.println("Загрузка выполнена в " + Date.getSourceDate());
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String command = Check.checkCommand(update.getMessage());
        if (command != null) {
            switch (command) {
                case "/start":
                    execute(startCommand.start(update));
                    User user = update.getMessage().getFrom();
                    ResultSet resultSet = db.findUser(user.getId());

                    int count = 0;
                    while (resultSet.next()) {
                        count++;
                    }

                    if (count == 0) {
                        db.newUser(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName());
                        db.setDefaultRate("", 0, user.getId());
                        System.out.println("Пользователь добавлен в БД -> " + Logs.sendConsoleLog(update));
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
            execute(sendMessageController.createMessageFromVlad(update.getMessage().getText() + "\n\nСообщение от - " +
                    myCurrentUser.getUserId()));
            execute(sendMessageController.createMessage(update, "Сообщение доставлено, спасибо за обратную связь"));
            myCurrentUser.setWaitFeedback(false);
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
            //System.out.println("Создано новое расписание -> " + Logs.sendConsoleLog(update));
            execute(sendMessageController.createMessage(update, "Отслеживание курса настроено!"));

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
                System.out.println("Выбран Биржевой (live) курс -> " + Logs.sendConsoleLog(update));
                logger.info("logger");

                User user = update.getCallbackQuery().getFrom();
                ResultSet resultSet = db.findUser(user.getId());
                int id = -1;
                while (resultSet.next()) {
                    id = resultSet.getInt(6);
                }
                if (id == 1) {
                    execute(sendMessageController.editInlineMessage(update, "Информация на (время Московское): " + Date.getSourceDate() + "\n" +
                            apiLayerService.getLive(myCurrentUser.getCurrencyFrom(), myCurrentUser.getCurrencyTo())));
                } else {
                    execute(sendMessageController.editInlineMessage(update, "У вас нет подписки, что бы воспользоваться функцией"));
                }

                myCurrentUser.setWaitCouple(false);
                setNullFromCurrency(myCurrentUser);
            } else if (update.getCallbackQuery().getData().equals("Биржевой") && myCurrentUser.isWaitCouple()) {
                System.out.println("Выбран Биржевой курс -> " + Logs.sendConsoleLog(update));

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
                setNullFromCurrency(myCurrentUser);
                myCurrentUser.setWaitCouple(false);
            } else if (update.getCallbackQuery().getData().equals("Биржевой") && myCurrentUser.isWaitSumForSchedule()) {
                System.out.println("Выбран Биржевой курс -> " + Logs.sendConsoleLog(update));

                if (myCurrentUser.isWaitRate()) {
                    //myCurrentUser.setRate(Integer.parseInt(update.getMessage().getText()));
                    //execute(sendMessageController.createMessage(update, "Курс установлен"));
                } else {
                    execute(sendMessageController.editInlineMessage(update, "Укажите желаемый курс"));
                    myCurrentUser.setWaitRate(true);
                }
            }

            if (myCurrentUser.getCurrencyFrom() != null && myCurrentUser.getCurrencyTo() != null && !myCurrentUser.isWaitRate()) {
                System.out.println("Выбрана пара валют -> " + Logs.sendConsoleLog(update));

                execute(sendMessageController.editInlineMessage(update, myCurrentUser.getCurrencyFrom() +
                        "\n            ->\n                    " + myCurrentUser.getCurrencyTo()));
                if (myCurrentUser.isWaitSumForSchedule()) {
                    execute(inlineKeyButtonService.setInlineButtonInLine(update, "Выберите курс", new InlineButtonsNames().InlineRowsOfSchedule));
                } else if (myCurrentUser.isWaitCouple()) {
                    execute(inlineKeyButtonService.setInlineButtonInLine(update, "Выберите курс", new InlineButtonsNames().InlineRows));
                }
            }
        }

        //блок if для запуска логики с KeyboardButton и установки тригера "wait"
        if (update.hasMessage() && update.getMessage().hasText()) {

            switch (update.getMessage().getText()) {
                case "Узнать курс":
                    System.out.println("Узнать курс -> " + Logs.sendConsoleLog(update));
                    execute(inlineKeyButtonService.setInlineButtonDouble(
                            update, "Выберите пару:", new InlineButtonsNames().currency));
                    myCurrentUser.setWaitCouple(true);
                    break;
                case "Отслеживать курс":
                    System.out.println("Отслеживать курс -> " + Logs.sendConsoleLog(update));
                    execute(inlineKeyButtonService.setInlineButtonDouble(
                            update, "Выберите пару:", new InlineButtonsNames().currency));
                    myCurrentUser.setWaitSumForSchedule(true);
                    break;
                case "Обратная связь":
                    System.out.println("Хотят оставить обратную связь -> " + Logs.sendConsoleLog(update));
                    execute(sendMessageController.createMessage(update, "Следующее сообщение будет отправлено автору бота,\n" +
                            "Но помни - <i>художника обидеть может каждый, не каждый может выйти из бана!</i>"));
                    myCurrentUser.setWaitFeedback(true);
                    break;
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
        System.out.println("Пользователь добавлен в лист myUsers -> " + Logs.sendConsoleLog(update));
    }

    @Override
    public String getBotUsername() {
        return "@zherikhov_currency_bot";
    }

    @Override
    public String getBotToken() {
        return "5648874987:AAEeXQfDrN2SHH_Eirmh2tBo2Qs_q26Rsuc";
    }
}
