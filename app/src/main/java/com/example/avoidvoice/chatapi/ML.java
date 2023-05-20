package com.example.avoidvoice.chatapi;
import android.util.Log;

import okhttp3.*;

public class ML {

   private OkHttpClient client;
   private String url;

    public void ML(){
        client = new OkHttpClient();
        url = "http://127.0.0.1/predict";
    }

    public void predict(String text){

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
                boolean prediction = Boolean.parseBoolean(responseData);

                // 예측 결과 처리
                if (prediction) {
                    // 예측 결과가 true인 경우 = 보이스피싱O
                    Log.d("predict","예측 결과: True");
                } else {
                    // 예측 결과가 false인 경우 = 보이스피싱X
                    Log.d("predict","예측 결과: False");
                }
            } else {
                // 응답이 실패한 경우
                Log.d("predict response error","응답 실패: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}