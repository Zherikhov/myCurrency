package ru.zherikhov.schedule;

import lombok.SneakyThrows;
import ru.zherikhov.App;
import ru.zherikhov.connector.DatabaseHandler;
import ru.zherikhov.data.MyUser;
import ru.zherikhov.service.SendMessageController;

import java.sql.ResultSet;

import static ru.zherikhov.App.myUsers;

public class ScheduleCurrency implements Runnable {
    private final App bot;
    private final SendMessageController sendMessageController = new SendMessageController();

    public ScheduleCurrency(App bot) {
        this.bot = bot;
    }

    //private List<MyUser> myUsers = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler();

    @SneakyThrows
    @Override
    public void run() {
        ResultSet resultSet = db.getAllUsers();
        while (resultSet.next()) {
            int id = resultSet.getInt(5);
            String couple = resultSet.getString(7);
            int coupleValue = resultSet.getInt(8);

            if (coupleValue >= 69) {
                bot.execute(sendMessageController.createMessageFromVlad("Test"));

            }



        }


    }
}


