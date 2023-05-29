package com.example.avoidvoice.chatapi;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.avoidvoice.warning.WarningMessage;

import org.json.JSONException;

public class GPTHandler {
    WarningMessage warningMessageActivity;
    GptMessage gptMessage;
    int numberOfMessage;

    public GPTHandler(/*WarningMessage targetActivity*/) throws JSONException {
        //this.warningMessageActivity = targetActivity;
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

        warningMessageActivity.addWait();

           /* String aaa = "녹취는 약 5분에서 7분정도 소요될 건데 본인 혹시 잠깐 통화 가능하신 건가요? 수신자 : 예 발신자 : 지금 고속도로세요? 몇분 정도나 도착하세요? 수신자 : 9시반이요 발신자 : 이제 다른 피해자분들도 있기 때문에 저희가 뭐 본인시간을 이렇게 맞춰 드리기가 힘듭니다. 그래서 제가 다시 연락 드리겠습니다. 수신자 : 알겠습니다.";
            chatGptApi.callAPI(aaa, new GPTHandler.ChatGptAPICallback(),gptMessage,numberOfMessage);*/
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