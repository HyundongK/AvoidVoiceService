package com.example.avoidvoice.chatapi;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TranslateText extends AsyncTask<String, Void, String> {

    private APICallback apiCallback;

    @Override
    protected String doInBackground(String... params) {
        String sourceText = params[0];
        String sourceLang = params[1];
        String targetLang = params[2];

        // Perform the network operation in the background thread
        String translatedText = null;
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("source", sourceLang)
                    .add("target", targetLang)
                    .add("text", sourceText)
                    .build();

            Request request = new Request.Builder()
                    .url("https://openapi.naver.com/v1/papago/n2mt")
                    .addHeader("X-Naver-Client-Id", "29AAbkBCZHL_s2w265uA")
                    .addHeader("X-Naver-Client-Secret", "kbpC8jzqpx")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            response.close();
            JSONObject responseJson = new JSONObject(responseBody);
            JSONObject message = responseJson.getJSONObject("message");
            JSONObject result = message.getJSONObject("result");
            translatedText = result.getString("translatedText");

            Log.d("번역 결과 : ",translatedText);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return translatedText;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void execute(String sourceText, String sourceLang, String targetLang, APICallback apiCallback) {
        super.execute(sourceText, sourceLang, targetLang);
        this.apiCallback = apiCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onPostExecute(String translatedText) {
        Log.d("번역 완료 : ",translatedText);

        apiCallback.onSuccess(translatedText);
    }
}


