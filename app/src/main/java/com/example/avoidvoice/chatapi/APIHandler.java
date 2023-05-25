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

            chatGptApi.callAPI("발신자 : 그러면은 고객님, 이 부분은 정지 풀릴려면은 시간 소요가 조금 되시구요.\n" +
                    "수신자 : 아 삼년. 삼개월 이상 걸린다고 하던데요.\n" +
                    "발신자 : 네 6개월에서 일년 정도 그 사이에 풀릴 거예요.\n" +
                    "수신자 : 아 그래서 어떻게, 어떻게 해야 되는 거예요?\n" +
                    "발신자 : 제가 말씀 드렸다시피 해결 방법은 이 방법밖에 없으세요.\n" +
                    "수신자 : 아 그건 아닌 거 같은데요. 제가 생각하기에.\n" +
                    "발신자 : 그러면은 고객님 뭐 이거 고객님 이거 풀 풀으실려면은 기대 기다렸다가 벌금 무시고 하셔야 되는데\n" +
                    "수신자 : 아니 처음부터 그렇게 말씀 안하셨잖아요.\n" +
                    "발신자 : 제가 고객님 처음부터 담당자였나요?\n" +
                    "수신자 : 아니 저 그 처음 그 담당자하고 통화할 수 있나요 제가?\n" +
                    "발신자 : 지금은 힘들고요. 지금은 이제 서류가 저희 쪽으로 넘어 와서 지금 저희 팀으로 넘어 왔는데 팀장인 저한테 바로 넘어왔어요.", new ChatGptAPICallback(),gptMessage,numberOfMessage);
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
