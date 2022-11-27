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
import ru.zherikhov.data.MyUser;
import ru.zherikhov.service.ApiLayerProcessing;
import ru.zherikhov.service.CurrentCurrencyCommand;
import ru.zherikhov.service.SendMessageController;
import ru.zherikhov.service.StartCommand;
import ru.zherikhov.utils.Check;
import ru.zherikhov.utils.Logs;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App extends TelegramLongPollingBot {
    private final Check check = new Check();
    private final StartCommand startCommand = new StartCommand();
    private final DatabaseHandler db = new DatabaseHandler();
    private static final List<MyUser> myUsers = new ArrayList<>();
    private final CurrentCurrencyCommand currentCurrencyCommand = new CurrentCurrencyCommand();
    private MyUser myCurrentUser;
    private final SendMessageController sendMessageController = new SendMessageController();
    private final ApiLayerProcessing apiLayerProcessing = new ApiLayerProcessing();


    public static void main(String[] args) {
        App bot = new App();
        TelegramBotsApi telegramBotsApi;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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

                    execute(currentCurrencyCommand.setInlineButtonDouble(
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

                execute(sendMessageController.sendInlineMessage(update, myCurrentUser.getCurrencyTo() +
                        "\n         ->\n                  " + myCurrentUser.getCurrencyFrom()));
                execute(currentCurrencyCommand.setInlineButtonInLine(update, "Выберите курс", new InlineButtonsNames().parsers));
                myCurrentUser.resetTempFoeCurrencies();
            }

            if (update.getCallbackQuery().getData().equals("Биржевой")) {
                System.out.println("Выбран биржевой курс -> " + Logs.sendConsoleLog(update));
                execute(sendMessageController.sendInlineMessage(update,
                        apiLayerProcessing.getLive(myCurrentUser.getCurrencyFrom(), myCurrentUser.getCurrencyTo())));

                myCurrentUser.setCurrencyTo(null);
                myCurrentUser.setCurrencyFrom(null);

            }
        }
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
