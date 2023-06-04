package com.example.avoidvoice.chatapi;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.avoidvoice.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
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
    private String gptKey = BuildConfig.GPT_KEY;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    /*
    실제 api를 호출하는 메서드
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void callAPI(String question, APICallback apiCallback, GptMessage gptMessage, int numberOfMessage){

        Log.d("callAPI 시작 : ", "qestion :"+ question);

        JSONObject userMessage = new JSONObject();
        JSONObject GPTMessage = new JSONObject();

        try {
            userMessage.put("role", "user");

            if(numberOfMessage == 0 ) {
                userMessage.put("content", gptMessage.statement);
            }
            else if (numberOfMessage == 1) {
                userMessage.put("content", question);
            }
            else if (numberOfMessage >=2){
                userMessage.put("content",gptMessage.appendStatement + question);
            }
            gptMessage.appendMessage(userMessage);

            System.out.println(gptMessage.gptQuery.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("json body 에러 : ", question);
        }

        RequestBody body = RequestBody.create(gptMessage.gptQuery.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer " + gptKey)
                .header("Content-Type", "application/json")
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

                        GPTMessage.put("role", "assistant");
                        GPTMessage.put("content", responseMessage);
                        gptMessage.appendMessage(GPTMessage);

                        Log.d("callApi 반환 메세지 : ", responseMessage);
                        apiCallback.onSuccess(responseMessage);
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        Log.d("call - response json exception 에러", question);
                    }
                }else{
                    responseMessage = "Failed to load response due to "+response.body().toString();
                    Log.d("call - response 안떨어짐", question);
                    response.close();
                    try {
                        apiCallback.onSuccess(responseMessage);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }
}
