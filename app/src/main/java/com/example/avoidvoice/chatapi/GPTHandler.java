package com.example.avoidvoice.chatapi;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.avoidvoice.warning.WarningMessage;

import org.json.JSONException;

public class GPTHandler {
    GptMessage gptMessage;
    int numberOfMessage;
    private WarningMessage warningMessageActivity = WarningMessage.getInstance();

    public GPTHandler() throws JSONException {
        this.gptMessage = new GptMessage();
        this.numberOfMessage = 0;
    }

    /*
    api 호출을 시작하는 메서드 : 입력된 텍스트를 영문으로 변환
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void run(String inputText) {
        ChatGptApi chatGptApi = new ChatGptApi();
        chatGptApi.callAPI(inputText, new GPTHandler.ChangeActivity(),gptMessage,numberOfMessage);

        numberOfMessage++;
    }

    /*
   gpt 결과를 activity에 뿌려주는 메서드
    */
    private class ChangeActivity implements APICallback {
        @Override
        public void onSuccess(String resultText) {

            warningMessageActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    warningMessageActivity.addResponse(resultText);
                }
            });
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }
}