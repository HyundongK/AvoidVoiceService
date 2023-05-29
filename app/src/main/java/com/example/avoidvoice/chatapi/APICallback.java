package com.example.avoidvoice.chatapi;

import java.util.concurrent.ExecutionException;

public interface APICallback {
    void onSuccess(String resultText) throws ExecutionException, InterruptedException;
    void onFailure(Exception e);
}
