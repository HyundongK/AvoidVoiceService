package com.example.avoidvoice.chatapi;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.avoidvoice.NotificationHelper;
import com.example.avoidvoice.TestActivity;
import com.example.avoidvoice.warning.WarningMessage;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private Boolean checkSendMessage;
    private String mInputText;
    private GPTHandler gptHandler;
    private Context context;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public MLHandler(Context context) throws JSONException {
        this.warningCount = 0;
        this.context = context;
        checkSendMessage = false;
        mInputText="";
        gptHandler = new GPTHandler();
        //gptHandler.run("first");
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
                notificationHelper = new NotificationHelper(context);
                NotificationCompat.Builder nb = notificationHelper.getChannelNotification("알림", "보이스 피싱의 위험이 감지되었습니다.");
                notificationHelper.getManager().notify(1, nb.build());

                //문자보내기
                sendMessage();
                checkSendMessage = true;

                //chatgpt run - 원래 input을 사용할꺼면 mInputTexxt , 번역된 input을 사용할꺼면 resultText
                gptHandler.run(resultText);
            }
            else if(warningCount>=1){
                gptHandler.run(resultText);
            }
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
            Log.d("ML result", "load fail");
        }
    }

    public int getWarningCount(){
        return this.warningCount;
    }

    public void sendMessage(){
        String line = null; // 한줄씩 읽기
        String phoneNum;
        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        try {
            BufferedReader buf = new BufferedReader(new FileReader(saveFile+"/item.list"));
            while((line=buf.readLine())!=null){
                phoneNum = line;
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNum, null, "보이스피싱 위험 감지", null, null);
                }catch (Exception e){
                    //
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void run2(String inputText) throws ExecutionException, InterruptedException {
        ML ml =new ML();
        boolean mlResult = ml.execute(inputText).get();
        Log.d("ML result", String.valueOf(mlResult));
        if(mlResult) {
            warningCount ++;
            Log.d("ML result", String.valueOf(mlResult));
        }

        if(warningCount==1 && !checkSendMessage){
            //알림주기
                notificationHelper = new NotificationHelper(context);
                NotificationCompat.Builder nb = notificationHelper.getChannelNotification("알림", "보이스 피싱의 위험이 감지되었습니다.");
                notificationHelper.getManager().notify(1, nb.build());

            //문자보내기
            sendMessage();
            checkSendMessage = true;

            //chatgpt run - 원래 input을 사용할꺼면 mInputTexxt , 번역된 input을 사용할꺼면 resultText
            gptHandler.run(inputText);
        }
        else if(warningCount>=1){
            gptHandler.run(inputText);
        }
    }
}

