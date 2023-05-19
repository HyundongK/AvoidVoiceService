package com.example.avoidvoice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.avoidvoice.chatapi.APIHandler;
import com.example.avoidvoice.chatapi.ChatGptApi;
import com.example.avoidvoice.chatapi.TranslateText;
import com.example.avoidvoice.service.VoiceAvoidService;

import org.json.JSONException;

/*
테스트를 위한 activity class 후에 삭제 예정
 */
public class TestActivity extends AppCompatActivity {

    TextView textView;
    APIHandler apiHandler = new APIHandler(this);

    private Intent intent;
    private static boolean serviceStarted = false;

    public TestActivity() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        Button startbtn = findViewById(R.id.startbtn);
        Button stopbtn = findViewById(R.id.stopbtn);
        textView = findViewById(R.id.textView2);


        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {
                apiHandler.run(editText.getText().toString().trim());
            }
        });

        intent = new Intent(this, VoiceAvoidService.class);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!serviceStarted) {
                    serviceStarted = true;
                    startService(intent);
                }
            }
        });
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceStarted) {
                    serviceStarted = false;
                    stopService(intent);
                }
            }
        });

    }

    public void setTextView(String newText){
        textView.setText(newText);
    }
}