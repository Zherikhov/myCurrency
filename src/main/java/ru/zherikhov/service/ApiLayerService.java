package ru.zherikhov.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.zherikhov.data.ApiLayerJson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ApiLayerService {
    Gson gson = new Gson();

    @SneakyThrows
    public String getLive(String source, String currencies) {
        String response = getResponse(source, currencies).body().string();
        HashMap<String, String> currenciesCouple = new HashMap<>();

        ApiLayerJson apiLayerJson = gson.fromJson(response, ApiLayerJson.class);
        String sourceCurrency = apiLayerJson.getSource();

        String temp = sourceCurrency + currencies;
        currenciesCouple.put(temp, apiLayerJson.getQuotes().get(temp).toString());

        return "Итог: " + currenciesCouple.get(temp);
    }

    /**
     * @param currencies валюты для конвертации
     * @param source     исходная валюта
     * @return возвращает HashMap где ключ - пара из валют для конвертации, а значение - курс этой пары
     */
    @SneakyThrows
    public HashMap<String, String> getLiveAll(List<String> currencies, String source) {
        HashMap<String, String> currenciesCouple = new HashMap<>();

        //формируем String currencies для запроса всех курсов для одной валюты
        StringBuilder allCurrencies = new StringBuilder("");
        for (String str : currencies) {
            allCurrencies.append(str);
            allCurrencies.append(",");
        }
        allCurrencies.deleteCharAt(allCurrencies.length() - 1);

        String response = getResponse(source, allCurrencies.toString()).body().string();
        ApiLayerJson apiLayerJson = gson.fromJson(response, ApiLayerJson.class);
        String sourceCurrency = apiLayerJson.getSource();

        for (String str : currencies) {
            if (!sourceCurrency.equals(str)) {
                String temp = sourceCurrency + str;
                currenciesCouple.put(temp, apiLayerJson.getQuotes().get(temp).toString());
            }
        }

        return currenciesCouple;
    }

    private Response getResponse(String source, String currencies) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Response response = null;
        Request request = new Request.Builder()
                .url("https://api.apilayer.com/currency_data/live?source=" + source + "&currencies=" + currencies)
                .addHeader("apikey", "HIsyujDCxps0P1k3phH9iehEIB9yy3XS")
                //A2IT8o9iwMEJggR2LB6R0Kt8aE6UyuQT
                //68Od7l7hEfhm549Cduo5dInvlG1y79l3
                //HIsyujDCxps0P1k3phH9iehEIB9yy3XS
                .method("GET", null)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println("нет ответа от api.apilayer.com, или что-то еще...");
            e.printStackTrace();
        }
        return response;
    }
}
