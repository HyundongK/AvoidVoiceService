package com.example.avoidvoice.chatapi;

import android.os.Build;
import android.util.Log;
import android.view.View;

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
    api response 값 저장 변수
     */
    private String responseMessage;

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


    /*
    실제 api를 호출하는 메서드
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void callAPI(String question, APICallback apiCallback){

        Log.d("callAPI 시작 : ", question);

        JSONObject jsonBody = new JSONObject();
        JSONObject message = new JSONObject();

        try {
            jsonBody.put("model","gpt-3.5-turbo");
            message.put("role", "user");
            message.put("content", question);
            jsonBody.append("messages", message);
            jsonBody.put("max_tokens",500);
            jsonBody.put("temperature",0);
            jsonBody.put("n",1);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("json body 에러 : ", question);
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
                responseMessage = "Failed to load response due to "+ e.getMessage();
                Log.d("call - onFailure 에러", responseMessage);
                apiCallback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        response.close();
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        responseMessage = jsonArray.getJSONObject(0).getJSONObject("message").getString("content").trim();
                        Log.d("callApi 반환 메세지 : ", responseMessage);
                        apiCallback.onSuccess(responseMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("call - response json exception 에러", question);
                    }
                }else{
                    responseMessage = "Failed to load response due to "+response.body().toString();
                    Log.d("call - response 안떨어짐 에러", question);
                    response.close();
                    apiCallback.onSuccess(responseMessage);

                }
            }
        });
    }
}
