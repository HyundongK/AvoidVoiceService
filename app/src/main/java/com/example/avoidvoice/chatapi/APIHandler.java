package com.example.avoidvoice.chatapi;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.avoidvoice.TestActivity;

import org.json.JSONException;

/*
api 들의 실행순서를 핸들링하는 클래스
해당 클래스를 인스턴스 생성해서 run 으로 호출 시작
순서 : 입력된 텍스트를 영문으로 변환 -> 영문 텍스트로 chatGPt api 호출 -> 반환 결과를 한글로 변환
 */

public class APIHandler {
    TestActivity targetActivity;
    GptMessage gptMessage;
    int numberOfMessage;

    public APIHandler(TestActivity targetActivity) throws JSONException {
        this.targetActivity = targetActivity;
        this.gptMessage = new GptMessage();
        this.numberOfMessage = 0;
    }

    /*
    api 호출을 시작하는 메서드 : 입력된 텍스트를 영문으로 변환
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void run(String inputText) {
        TranslateText translateText = new TranslateText();
        translateText.execute(inputText, "ko", "en",new TranslateTextCallback());
    }

    /*
    chatGpt api를 호출하는 메서드
     */
    private class TranslateTextCallback implements APICallback {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onSuccess(String resultText) {
            ChatGptApi chatGptApi = new ChatGptApi();

            chatGptApi.callAPI("녹취는 약 5분에서 7분정도 소요될 건데 본인 혹시 잠깐 통화 가능하신 건가요? 수신자 : 예 발신자 : 지금 고속도로세요? 몇분 정도나 도착하세요? 수신자 : 9시반이요 발신자 : 이제 다른 피해자분들도 있기 때문에 저희가 뭐 본인시간을 이렇게 맞춰 드리기가 힘듭니다. 그래서 제가 다시 연락 드리겠습니다. 수신자 : 알겠습니다.", new ChatGptAPICallback(),gptMessage,numberOfMessage);
            numberOfMessage++;
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }

    /*
    chatGpt api에서 반환된 값을 한글로 변환하는 메서드
     */
    private class ChatGptAPICallback implements APICallback {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onSuccess(String resultText) {
            TranslateText translateText = new TranslateText();
            translateText.execute(resultText, "en", "ko", new FinalAPICallback());
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }

    /*
   한글로 변환된 텍스트를 activity에 뿌려주는 메서드
    */
    private class FinalAPICallback implements APICallback {
        @Override
        public void onSuccess(String resultText) {

            targetActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    targetActivity.setTextView(resultText != null ? resultText : "null");
                }
            });
        }

        @Override
        public void onFailure(Exception e) {
            //TODO : 실패시 어떻게 처리할 것인지
        }
    }
}
