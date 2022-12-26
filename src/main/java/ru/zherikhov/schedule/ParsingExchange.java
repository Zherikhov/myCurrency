package ru.zherikhov.schedule;

import lombok.SneakyThrows;
import ru.zherikhov.buttonObject.names.InlineButtonsNames;
import ru.zherikhov.connector.Const;
import ru.zherikhov.connector.DatabaseHandler;
import ru.zherikhov.service.ApiLayerService;
import ru.zherikhov.utils.Date;

import java.sql.ResultSet;

import static ru.zherikhov.data.CurrencyValue.*;

public class ParsingExchange implements Runnable {

    DatabaseHandler databaseHandler = new DatabaseHandler();
    ApiLayerService apiLayerProcessing = new ApiLayerService();
    ResultSet resultSet;

    @SneakyThrows
    @Override
    public void run() {
        if (Date.compareTime("22:00:00") > 0 && Date.compareTime("07:00:00") < 0) {
            databaseHandler.setCurrenciesInDataBaseForUsd(apiLayerProcessing.getLiveAll(new InlineButtonsNames().currency, "USD"));

            resultSet = databaseHandler.getLastCurrency(Const.CURRENCY_USD_TABLE);
            while (resultSet.next()) {
                UsdValues.put("USDEUR", resultSet.getString(2));
                UsdValues.put("USDRUB", resultSet.getString(3));
                UsdValues.put("USDGEL", resultSet.getString(4));
                UsdValues.put("USDARS", resultSet.getString(5));
            }

            databaseHandler.setCurrenciesInDataBaseForEur(apiLayerProcessing.getLiveAll(new InlineButtonsNames().currency, "EUR"));

            resultSet = databaseHandler.getLastCurrency(Const.CURRENCY_EUR_TABLE);
            while (resultSet.next()) {
                EurValues.put("EURUSD", resultSet.getString(2));
                EurValues.put("EURRUB", resultSet.getString(3));
                EurValues.put("EURGEL", resultSet.getString(4));
                EurValues.put("EURARS", resultSet.getString(5));
            }

            databaseHandler.setCurrenciesInDataBaseForRub(apiLayerProcessing.getLiveAll(new InlineButtonsNames().currency, "RUB"));

            resultSet = databaseHandler.getLastCurrency(Const.CURRENCY_RUB_TABLE);
            while (resultSet.next()) {
                RubValues.put("RUBEUR", resultSet.getString(2));
                RubValues.put("RUBUSD", resultSet.getString(3));
                RubValues.put("RUBGEL", resultSet.getString(4));
                RubValues.put("RUBARS", resultSet.getString(5));
            }

            databaseHandler.setCurrenciesInDataBaseForGel(apiLayerProcessing.getLiveAll(new InlineButtonsNames().currency, "GEL"));

            resultSet = databaseHandler.getLastCurrency(Const.CURRENCY_GEL_TABLE);
            while (resultSet.next()) {
                GelValues.put("GELEUR", resultSet.getString(2));
                GelValues.put("GELRUB", resultSet.getString(3));
                GelValues.put("GELUSD", resultSet.getString(4));
                GelValues.put("GELARS", resultSet.getString(5));
            }

            databaseHandler.setCurrenciesInDataBaseForArs(apiLayerProcessing.getLiveAll(new InlineButtonsNames().currency, "ARS"));

            resultSet = databaseHandler.getLastCurrency(Const.CURRENCY_ARS_TABLE);
            while (resultSet.next()) {
                ArsValues.put("ARSEUR", resultSet.getString(2));
                ArsValues.put("ARSRUB", resultSet.getString(3));
                ArsValues.put("ARSGEL", resultSet.getString(4));
                ArsValues.put("ARSUSD", resultSet.getString(5));
            }
        }
        System.out.println("ParsingExchange.run() " + Date.getSourceDate());
    }
}
