package com.example.avoidvoice.chatapi;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.avoidvoice.TestActivity;
import org.json.JSONException;


/*
1. ko -> en
2. en -> ko
--? > 표준 단어로 바꾸기 위함
3. ML
4. check and notification
 */

public class MLHandler {
    TestActivity targetActivity;
    ML ml;
    int warningCount;


    public MLHandler(TestActivity targetActivity) throws JSONException {
        this.targetActivity = targetActivity;
        this.ml = new ML();
        warningCount = 0;

    }

    //1
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void run(String inputText) {

        //warningcount 가 3 이상이면 바로 마지막 api로 보내도 되고
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
        public void onSuccess(String resultText) {
            if(ml.predict(resultText)) warningCount ++;

            if(warningCount==3){
                //알림주기
                //문자보내기
                //chatgpt run - 여기서 하는것보단 뭔가 신호를 주는게 맞을듯
            }
            else if(warningCount>3){
                //gpt만 run
            }
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }

}