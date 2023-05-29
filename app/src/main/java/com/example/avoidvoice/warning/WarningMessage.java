package com.example.avoidvoice.warning;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.avoidvoice.MainActivity;
import com.example.avoidvoice.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarningMessage extends AppCompatActivity {

    private static WarningMessage instance;
    private static List<Message> messageList;
    private static MessageAdapter messageAdapter;

    public static synchronized WarningMessage getInstance() {
        if (instance == null) {
            instance = new WarningMessage();
        }
        return instance;
    }

    public WarningMessage() {
        if (instance == null) {
            instance = this;
            instance.messageList = new ArrayList<>();
            instance.messageAdapter = new MessageAdapter(messageList);
        }
        else{
            messageList = instance.messageList;
            messageAdapter = instance.messageAdapter;
        }
    }

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    //List<Message> messageList = MessageList.getInstance().getList();
    //MessageAdapter messageAdapter = new MessageAdapter(messageList);


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warningmessage);

        instance = this;

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);


        //setup recycler view
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

//        Intent MainIntent = new Intent(this, MainActivity.class);
//        MainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(MainIntent);

//        sendButton.setOnClickListener((v)->{
//            String question = messageEditText.getText().toString().trim();
//            addToChat(question,Message.SENT_BY_ME);
//            messageEditText.setText("");
//            //callAPI(question);
//            welcomeTextView.setVisibility(View.GONE);
//        });
    }
    //사용자 채팅 올리는 부분 마지막에는 삭제
    public void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                //recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    //callapi로 결과값을 가져온걸로 채팅창 추가
    public void addResponse(String response){
        //messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    //GPT : 대화 분석 중... 메세지를 추가 답변이 response되면 삭제
//    public void addWait(){
//        messageList.add(new Message("GPT : 대화 분석 중...", Message.SENT_BY_BOT));
//    }
}

