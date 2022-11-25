package ru.zherikhov;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.zherikhov.service.StartCommand;
import ru.zherikhov.utils.Check;

/**
 * Hello world!
 */
public class App extends TelegramLongPollingBot {
    private final Check check = new Check();
    private final StartCommand startCommand = new StartCommand();


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
                    break;
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
