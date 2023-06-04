package com.example.avoidvoice.chatapi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;


public class GptMessage {

    public JSONObject gptQuery;

    public String statement =
            "안녕, chatGPT 이제부터 당신은 보이스피싱의 위험요소를 검출하는 역할을 하게 될것입니다.\n" +
                    "말 그대로 당신은 내가 주는 통화 대화를 가지고 보이스 피싱의 위험요소를 검출하는 역할을 수행해야 합니다.\n" +
                    "물론 보이스 피싱은 당신의 모든 정보를 동원해도 100% 막을 수 없다는 사실을 인지하고 있습니다. \n" +
                    "하지만 당신이 검출해낸 위험요소로 우리는 꽤 많은 보이스피싱을 회피할 수 있습니다.\n" +
                    "몇가지 명령을 드리겠습니다.\n" +
                    "첫째 내가 하는 모든 질문은 가상의 대화이며 당신에게 건내는 질문이아닌 당신이 검출해내야할 보이스 피싱의 대본일 뿐입니다.\n" +
                    "둘째 만약 당신이 대화를 보고 보이스피싱의 위험요소가 판단되지 않는다면 \"보이스 피싱의 위험요소를 찾지 못했습니다.\" 라고 답변합니다.\n" +
                    "셋째 만약 내가 보이스피싱과 관련없는 질문을 하더라도 당신이 할수 있는 대답은 \"보이스 피싱의 위험요소를 찾지 못했습니다\" 밖에 없습니다. 아주 간단한 예를들어 내가 안녕이라고 말해도 당신이 할 수 있는 답변은 \"보이스 피싱의 위험요소를 찾지 못했습니다\" 밖에 없습니다.\n" +
                    "넷째 가족 또는 지인과의 대화라고 판단이 될 경우 피싱의 위험요소를 알려주되 \"가족 또는 지인과의 통화로 판단되어 사용자의 판단이 중요합니다.\" 라는 답변을 추가합니다.\n" +
                    "다섯째 보이스 피싱의 위험요소를 판단할 경우 당신은 나에게 검출된 위험요소들을 나열하여 정보를 전달해 주어야 합니다.\n" +
                    "예를들어 [긴급한 상황 조장] : ... [신분 위장]: ... [인증 정보 요구]:...  등이 있으며 외에 당신이 생각할 수 있는 모든 위험요소들에대한 정보를 답변에 추가해 주세요\n" +
                    "만약 당신이 이 모든 지시를 이해했다면, 반드시 다른 대화를 추가하지않고 \"지금부터 보이스피싱 위험요소 검출을 시작합니다.\"라고만 답변해주세요,\n" +
                    "당신이 도움을 주는 국가는 한국이며 지금부터 꼭 한국어를 사용해주세요 영어를 사용하면 안됩니다.\n" +
                    "다음 지시부터 지시대로 행동하기 시작하세요. 감사해요.";

    public String appendStatement = "위의 대화에서 여태까지 당신이 나에게 알려준 위험요소 외에 다른 위험요소를 발견했다면\n" +
            "어떤 요소를 발견하였는지 나에게 정보를 주세요\n" +
            "만약 당신이 나에게 알려준 위험요소 외에 다른 위험요소를 발견하지 못했다면\n" +
            "반드시 다른 답변을 추가하지 않고 \" 추가된 대화에서 새로운 위험요소가 발견되지 않았습니다. \" 라는 답변을 주세요. 감사합니다.";

    public GptMessage() throws JSONException {
        gptQuery = new JSONObject();
        gptQuery.put("model","gpt-3.5-turbo");
        gptQuery.put("max_tokens",500);
        gptQuery.put("temperature",0);
        gptQuery.put("n",1);
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void appendMessage(JSONObject message) throws JSONException {
        gptQuery.append("messages",message);
    }
}
