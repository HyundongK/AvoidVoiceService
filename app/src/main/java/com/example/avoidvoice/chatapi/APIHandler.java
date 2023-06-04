package com.example.avoidvoice.chatapi;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*
api 들의 실행순서를 핸들링하는 클래스
해당 클래스를 인스턴스 생성해서 run 으로 호출 시작
순서 : 입력된 텍스트를 영문으로 변환 -> 영문 텍스트로 chatGPt api 호출 -> 반환 결과를 한글로 변환
 */

public class APIHandler {

    private final String fileName = "items.list";
    private int warningCount;
    private NotificationHelper notificationHelper;
    private Boolean checkSendMessage;
    private String mInputText;
    private GPTHandler gptHandler;
    private Context context;

    private SharedPreferences appData;
    private boolean saveSwitchData = false;

    GptMessage gptMessage;
    int numberOfMessage;
    private WarningMessage warningMessageActivity = WarningMessage.getInstance();

    public APIHandler(Context context) throws JSONException {
        this.warningCount = 0;
        this.context = context;
        checkSendMessage = false;
        mInputText = "";
        //gptHandler = new GPTHandler();
        //gptHandler.run("first");
        appData = context.getSharedPreferences("appData", MODE_PRIVATE);
        load();

        this.gptMessage = new GptMessage();
        this.numberOfMessage = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void run(String inputText) throws ExecutionException, InterruptedException {
        ML ml = new ML();
        boolean mlResult = ml.execute(inputText).get();
        if (mlResult) {
            warningCount++;
            Log.d("ML result", String.valueOf(mlResult));
        }

        if (warningCount == 1 && !checkSendMessage) {
            //알림주기
            notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification("알림", "보이스 피싱의 위험이 감지되었습니다.");
            notificationHelper.getManager().notify(1, nb.build());

            //문자보내기
            if (saveSwitchData) sendMessage();
            checkSendMessage = true;

            //gptHandler.run(inputText);
            new GptCallBack().onSuccess(inputText);
        } else if (warningCount >= 1) {
            //gptHandler.run(inputText);
            new GptCallBack().onSuccess(inputText);
        }
    }

    private class GptCallBack implements APICallback {

        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onSuccess(String resultText) throws ExecutionException, InterruptedException {
            ChatGptApi chatGptApi = new ChatGptApi();
            chatGptApi.callAPI(resultText, new APIHandler.ChangeActivityCallBack(), gptMessage, numberOfMessage);

            numberOfMessage++;
        }

        @Override
        public void onFailure(Exception e) {

        }
    }

    private class ChangeActivityCallBack implements APICallback {

        @Override
        public void onSuccess(String resultText) throws ExecutionException, InterruptedException {
            warningMessageActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    warningMessageActivity.addResponse(resultText);
                }
            });
        }

        @Override
        public void onFailure(Exception e) {

        }
    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveSwitchData = appData.getBoolean("SMS_SWITCH", false);
    }

    public void sendMessage() {
        String line = null; // 한줄씩 읽기

        File file = new File(context.getFilesDir(), fileName);
        FileReader fr = null;
        BufferedReader bufrd = null;
        String phoneNum;

        if (file.exists()) {
            try {
                fr = new FileReader(file);
                bufrd = new BufferedReader(fr);

                while ((phoneNum = bufrd.readLine()) != null) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNum, null, "보이스피싱 위험 감지", null, null);
                    } catch (Exception e) {
                        Log.d("sms", "sms error");
                    }
                }

                bufrd.close();
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//        try {
//            BufferedReader buf = new BufferedReader(new FileReader(saveFile+"/item.list"));
//            while((line=buf.readLine())!=null){
//                phoneNum = line;
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phoneNum, null, "보이스피싱 위험 감지", null, null);
//                }catch (Exception e){
//                    Log.d("sms","sms error");
//                }
//            }
//            buf.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
