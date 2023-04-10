package com.example.avoidvoice.chatapi;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.avoidvoice.TestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
chatGpt turbo 3.5 model api
 */
public class ChatGptApi {

    /*
    api response 을 출력할 activity 를 받는 변수
    생성자함수 에서 초기화 합니다.
     */
    private TestActivity testActivity;

    /*
    api response 값 저장 변수
     */
    String result;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /*
    api 호출을 위해 OkHttpClient를 사용합니다.
    TODO : 속도가 좀 느린감이 있어 후에 좀더 빠르게 api를 호출하고 응답받는 방법을 적용할 예정
    타임아웃 값은 30초로 설정
    */
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public ChatGptApi(TestActivity testActivity){
        this.testActivity = testActivity;
    }

    /*
    실제 api를 호출하는 메서드
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void callAPI(String question){

        JSONObject jsonBody = new JSONObject();
        JSONObject message = new JSONObject();

        try {
            jsonBody.put("model","gpt-3.5-turbo");
            message.put("role", "user");
            message.put("content", question);
            jsonBody.append("messages", message);
            jsonBody.put("max_tokens",4000);
            jsonBody.put("temperature",0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result = "Failed to load response due to "+ e.getMessage();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content").trim();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    result = "Failed to load response due to "+response.body().toString();
                }
            }
        });

        /*
        response 값을 받아온 엑티비티 텍스트뷰에 세팅
        후에는 리사이클뷰에 세팅되도록 수정
         */
        testActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testActivity.setTextView(result != null ? result : "null");
            }
        });
    }
}
