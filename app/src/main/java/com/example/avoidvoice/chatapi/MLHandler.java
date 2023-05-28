package com.example.avoidvoice.chatapi;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.avoidvoice.NotificationHelper;
import com.example.avoidvoice.TestActivity;
import com.example.avoidvoice.warning.WarningMessage;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;
/*
1. ko -> en
2. en -> ko
--? > 표준 단어로 바꾸기 위함
3. ML
4. check and notification
 */

public class MLHandler {
    private int warningCount;
    private NotificationHelper notificationHelper;
    //private Context context;
    private Boolean checkSendMessage;
    private String mInputText;
    private GPTHandler gptHandler;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public MLHandler() throws JSONException {
        this.warningCount = 0;
        //this.context = context;
        //notificationHelper = new NotificationHelper(context);
        checkSendMessage = false;
        mInputText="";
        gptHandler = new GPTHandler();
        gptHandler.run("first");
    }

    //1
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void run(String inputText) {
        mInputText=inputText;
        TranslateText translateText = new TranslateText();
        translateText.execute(inputText, "ko", "en",new MLHandler.TranslateTextCallback());
    }

    //2
    private class TranslateTextCallback implements APICallback {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onSuccess(String resultText) {
            TranslateText translateText = new TranslateText();
            translateText.execute(resultText, "en", "ko", new MLHandler.MLCallback());
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }

    //3
    private class MLCallback implements APICallback {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onSuccess(String resultText) throws ExecutionException, InterruptedException {
            ML ml =new ML();
            boolean mlResult = ml.execute(resultText).get();
            if(mlResult) {
                warningCount ++;
                Log.d("ML result", String.valueOf(mlResult));
            }

            if(warningCount==1 && !checkSendMessage){
                //알림주기
                NotificationCompat.Builder nb = notificationHelper.getChannelNotification("알림", "보이스 피싱의 위험이 감지되었습니다.");
                notificationHelper.getManager().notify(1, nb.build());

                //문자보내기

                checkSendMessage = true;

                //chatgpt run - 원래 input을 사용할꺼면 mInputTexxt , 번역된 input을 사용할꺼면 resultText
                gptHandler.run(mInputText);
            }
            else if(warningCount>=1){
                //gpt만 run
                gptHandler.run(mInputText);
            }
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }

    public int getWarningCount(){
        return this.warningCount;
    }

}