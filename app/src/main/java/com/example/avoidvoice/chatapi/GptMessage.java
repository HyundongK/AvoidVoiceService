package com.example.avoidvoice.chatapi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GptMessage {

    public JSONObject gptQuery;

    public String mmm =
            "Hello, chatGPT, from now on, you will be playing a role in detecting the dangers of voice phishing.\n" +
                    "Literally, you're going to take the phone conversations I give you, and you're going to use them to detect the risk of voice phishing.\n" +
                    "Of course, we recognize that even with all of your knowledge, you won't be able to stop voice phishing 100% of the time. \n" +
                    "But with the risk factors that you detect, we can avoid quite a bit of it.\n" +
                    "I'm going to give you a couple of commands.\n" +
                    "First, all the questions I ask are fictitious conversations and are not directed at you, they are just scripts for the voice phishing you need to detect.\n" +
                    "Second, if you look at the conversation and don't recognize the risk of voice phishing, you can say \"I don't see any risk of voice phishing.\" is the answer.\n" +
                    "Third, if I ask you a question that is not related to voice phishing, the only answer you can give is \"I did not find any risk factors for voice phishing.\" A very simple example is if I say hello, the only answer you can give is \"I did not find any risk factors for voice phishing.\"\n" +
                    "Fourth, if you think you're talking to a family member or acquaintance, tell them about the risk of phishing, but add the response \"We think you're talking to a family member or acquaintance, so your judgment is important.\" as a response.\n" +
                    "Fifth, when you determine the risk factors for voice phishing, you should inform me by listing the risk factors you detected.\n" +
                    "For example, [Creates a sense of urgency]: ... [Disguises identity]: ... [Requests credentials]: ..., and any other risk factors you can think of in your response.\n" +
                    "If you understand all of these instructions, please answer \"I will start detecting voice phishing risks now\" without adding anything else,\n" +
                    "and start following the instructions. Thank you.";

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
