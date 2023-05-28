package com.example.avoidvoice.chatapi;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.*;

public class ML extends AsyncTask<String, Void, Boolean> {

    private OkHttpClient client;
    private String url;

    public ML() {
        client = new OkHttpClient();
        url = "http://192.168.0.104:8080/predict";
    }

    @Override
    protected Boolean doInBackground(String... texts) {
        String text = texts[0];

        // Request Body에 텍스트 데이터를 담습니다
        RequestBody requestBody = new FormBody.Builder()
                .add("text", text)
                .build();

        // POST 요청 생성
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 요청 보내고 응답 처리
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 응답 데이터 받기
                String responseData = response.body().string();

                // 응답 데이터 파싱
                // 예측 결과는 JSON 형식으로 {"prediction": true} 또는 {"prediction": false}로 전달됩니다.
                return Boolean.parseBoolean(responseData);

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
