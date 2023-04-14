package com.example.avoidvoice.chatapi;

public interface APICallback {
    void onSuccess(String resultText);
    void onFailure(Exception e);
}
