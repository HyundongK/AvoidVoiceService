package com.example.avoidvoice.chatapi;

import android.os.AsyncTask;
import android.util.Log;

import com.example.avoidvoice.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.*;

public class ML extends AsyncTask<String, Void, Boolean> {

    private OkHttpClient client;
    private String url;
    private Gson gson;

    public ML() {
        client = new OkHttpClient();
        url = BuildConfig.ML_URL;
        gson = new Gson();
    }

    @Override
    protected Boolean doInBackground(String... texts) {
        String text = texts[0];

        // Request Body에 텍스트 데이터를 담습니다
        RequestBody requestBody = new FormBody.Builder()
                .build();


        //
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("text", text);
        String requestUrl = urlBuilder.build().toString();

        // POST 요청 생성
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();

        // 요청 보내고 응답 처리
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 응답 데이터 받기
                String responseData = response.body().string();

                JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                boolean prediction = jsonObject.get("prediction").getAsBoolean();
                return prediction;

            } else {
                Log.d("predict response error", "응답 실패: " + response.code());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // 예측 결과 처리
        if (result) {
            // 예측 성공 처리
        } else {
            // 예측 실패 처리
        }
    }
}