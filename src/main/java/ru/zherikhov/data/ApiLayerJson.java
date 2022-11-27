package ru.zherikhov.data;

import com.google.gson.JsonObject;

public class ApiLayerJson {
    boolean success;
    String source;
    long timestamp;
    JsonObject quotes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public JsonObject getQuotes() {
        return quotes;
    }

    public void setQuotes(JsonObject quotes) {
        this.quotes = quotes;
    }
}
