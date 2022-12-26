package ru.zherikhov.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.zherikhov.data.ApiLayerJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiLayerService {
    Gson gson = new Gson();
    private List<String> apiKeys = new ArrayList<>();

    {
        apiKeys.add("HIsyujDCxps0P1k3phH9iehEIB9yy3XS"); //temp53453@gmail.com
        apiKeys.add("A2IT8o9iwMEJggR2LB6R0Kt8aE6UyuQT"); //vladzherikhov@gmail.com
        apiKeys.add("68Od7l7hEfhm549Cduo5dInvlG1y79l3"); //main mail
        apiKeys.add("mv0Ycs5It2V2duyoQJCL3e8sQAJn61iU"); //temp53454@gmail.com
        apiKeys.add("EB5Gf5brNYkdYGipBINifLFE7tWpEiAf"); //temp53455@gmail.com
    }

    public String getLive(String source, String currencies) {
        String response = null;
        try {
            response = getResponse(source, currencies).body().string();
        } catch (IOException e) {
            System.out.println(("Хз]1"));
            e.printStackTrace();
        }

        while (response.split(":")[0].equals("{\"message\"")) {
            apiKeys.add(apiKeys.get(0));
            apiKeys.remove(0);
            System.out.println("apiKey заменен с " + apiKeys.get(apiKeys.size() - 1) + " на " + apiKeys.get(0));
            try {
                response = getResponse(source, currencies).body().string();
            } catch (IOException e) {
                System.out.println(("Хз]2"));
                e.printStackTrace();
            }
        }

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
    public HashMap<String, String> getLiveAll(List<String> currencies, String source) {
        HashMap<String, String> currenciesCouple = new HashMap<>();

        //формируем String currencies для запроса всех курсов для одной валюты
        StringBuilder allCurrencies = new StringBuilder("");
        for (String str : currencies) {
            allCurrencies.append(str);
            allCurrencies.append(",");
        }
        allCurrencies.deleteCharAt(allCurrencies.length() - 1);

        String response = null;
        try {
            response = getResponse(source, allCurrencies.toString()).body().string();
        } catch (IOException e) {
            System.out.println(("Хз]3"));
            e.printStackTrace();
        }

        while (response.split(":")[0].equals("{\"message\"")) {
            apiKeys.add(apiKeys.get(0));
            apiKeys.remove(0);
            try {
                response = getResponse(source, allCurrencies.toString()).body().string();
            } catch (IOException e) {
                System.out.println(("Хз]4"));
                e.printStackTrace();
            }
        }
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
                .addHeader("apikey", apiKeys.get(0))
                .method("GET", null)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println("нет ответа от api.apilayer.com");
            e.printStackTrace();
        }

        return response;
    }
}
