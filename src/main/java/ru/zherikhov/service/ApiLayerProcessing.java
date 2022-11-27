package ru.zherikhov.service;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.zherikhov.data.ApiLayerJson;

import java.io.IOException;

public class ApiLayerProcessing {
    public String getLive(String currencies, String source) throws IOException {
        String response = getResponse(source, currencies).body().string();

        Gson gson = new Gson();
        ApiLayerJson apiLayerJson = gson.fromJson(response, ApiLayerJson.class);

        //TODO переделать на нормальный JSON
        String s = apiLayerJson.getQuotes().toString();
        s = s.replaceAll("[{}\"]", "");
        s = s.split(":")[1];

        return "Итог: " + s;
    }

    private Response getResponse(String source, String currencies) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Response response = null;
        Request request = new Request.Builder()
                .url("https://api.apilayer.com/currency_data/live?source=" + source + "&currencies=" + currencies)
                .addHeader("apikey", "68Od7l7hEfhm549Cduo5dInvlG1y79l3")
                //"A2IT8o9iwMEJggR2LB6R0Kt8aE6UyuQT"
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
