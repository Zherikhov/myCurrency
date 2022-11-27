package ru.zherikhov;

import lombok.SneakyThrows;
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
import ru.zherikhov.service.ApiLayerService;
import ru.zherikhov.service.InlineKeyButtonService;
import ru.zherikhov.service.SendMessageController;
import ru.zherikhov.service.StartCommand;
import ru.zherikhov.utils.Check;
import ru.zherikhov.utils.Date;
import ru.zherikhov.utils.Logs;

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
    private final Check check = new Check();
    private final StartCommand startCommand = new StartCommand();
    private final DatabaseHandler db = new DatabaseHandler();
    private static final List<MyUser> myUsers = new ArrayList<>();
    private final InlineKeyButtonService inlineKeyButtonService = new InlineKeyButtonService();
    private MyUser myCurrentUser;
    private final SendMessageController sendMessageController = new SendMessageController();
    private final ApiLayerService apiLayerService = new ApiLayerService();


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
        scheduler.scheduleAtFixedRate(new ParsingExchange(), 0, 7, TimeUnit.HOURS);
        System.out.println("Загрузка выполнена!");
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String command = check.checkCommand(update.getMessage());
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
                        System.out.println("Пользователь добавлен в БД -> " + Logs.sendConsoleLog(update));
                    }
                    break;
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            switch (update.getMessage().getText()) {
                case "Узнать курс":
                    System.out.println("Узнать курс -> " + Logs.sendConsoleLog(update));

                    execute(inlineKeyButtonService.setInlineButtonDouble(
                            update, "Выберите пару:", new InlineButtonsNames().currency));
                    break;
                case "Обратная связь":
                    System.out.println("Обратная связь");
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            long currentUserId = update.getCallbackQuery().getFrom().getId();

            if (!check.checkUser(myUsers, currentUserId)) {
                User user = update.getCallbackQuery().getFrom();
                MyUser myUser = new MyUser(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName());
                myUsers.add(myUser);
                System.out.println("Создан пользователь -> " + Logs.sendConsoleLog(update));
            }

            myCurrentUser = myUsers.get(Check.findUserId(myUsers, currentUserId));
            String[] userSelectedCurrencies = update.getCallbackQuery().getData().split(":");

            if (userSelectedCurrencies[0].equals("original")) {
                myCurrentUser.setCurrencyFrom(userSelectedCurrencies[1]);
                myCurrentUser.setTempForCurrencies(userSelectedCurrencies[0], userSelectedCurrencies[1]);
            } else if (userSelectedCurrencies[0].equals("target")) {
                myCurrentUser.setCurrencyTo(userSelectedCurrencies[1]);
                myCurrentUser.setTempForCurrencies(userSelectedCurrencies[0], userSelectedCurrencies[1]);
            }

            if (myCurrentUser.getCurrencyFrom() != null && myCurrentUser.getCurrencyTo() != null
                    && myCurrentUser.getTempForCurrencies().size() == 2) {
                System.out.println("Выбрана пара валют -> " + Logs.sendConsoleLog(update));

                execute(sendMessageController.editInlineMessage(update, myCurrentUser.getCurrencyFrom() +
                        "\n         ->\n                  " + myCurrentUser.getCurrencyTo()));
                execute(inlineKeyButtonService.setInlineButtonInLine(update, "Выберите курс", new InlineButtonsNames().parsers));
                myCurrentUser.resetTempFoeCurrencies();
            }

            if (update.getCallbackQuery().getData().equals("Биржевой (live)")) {
                System.out.println("Выбран биржевой курс -> " + Logs.sendConsoleLog(update));

                User user = update.getCallbackQuery().getFrom();
                ResultSet resultSet = db.findUser(user.getId());
                int id = -1;
                while (resultSet.next()) {
                    id = resultSet.getInt(6);
                }
                if (id == 1) {
                    execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): " + Date.getSourceDate() + "\n" +
                            apiLayerService.getLive(myCurrentUser.getCurrencyFrom(), myCurrentUser.getCurrencyTo())));
                } else {
                    execute(sendMessageController.editInlineMessage(update, "У вас нет подписки, что бы воспользоваться функцией"));
                }
                setNullFromCurrency(myCurrentUser);
            } else if (update.getCallbackQuery().getData().equals("Биржевой")) {
                if (myCurrentUser.getCurrencyFrom().equals("USD")) {
                    execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): " +
                            Date.getSourceDate() + "\n" + CurrencyValue.UsdValues.get(myCurrentUser.getCurrencyFrom() +
                            myCurrentUser.getCurrencyTo())));
                } else if (myCurrentUser.getCurrencyFrom().equals("EUR")) {
                    execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                            + Date.getSourceDate() + "\n" + CurrencyValue.EurValues.get(myCurrentUser.getCurrencyFrom() +
                            myCurrentUser.getCurrencyTo())));
                } else if (myCurrentUser.getCurrencyFrom().equals("RUB")) {
                    execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                            + Date.getSourceDate() + "\n" + CurrencyValue.RubValues.get(myCurrentUser.getCurrencyFrom() +
                            myCurrentUser.getCurrencyTo())));
                } else if (myCurrentUser.getCurrencyFrom().equals("GEL")) {
                    execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                            + Date.getSourceDate() + "\n" + CurrencyValue.GelValues.get(myCurrentUser.getCurrencyFrom() +
                            myCurrentUser.getCurrencyTo())));
                } else if (myCurrentUser.getCurrencyFrom().equals("ARS")) {
                    execute(sendMessageController.editInlineMessage(update, "Информация за (время Московское): "
                            + Date.getSourceDate() + "\n" + CurrencyValue.ArsValues.get(myCurrentUser.getCurrencyFrom() +
                            myCurrentUser.getCurrencyTo())));
                }
                setNullFromCurrency(myCurrentUser);
            }

        }
    }

    private static void setNullFromCurrency(MyUser myUser) {
        myUser.setCurrencyTo(null);
        myUser.setCurrencyFrom(null);
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
