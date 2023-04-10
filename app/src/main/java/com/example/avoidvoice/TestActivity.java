package com.example.avoidvoice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.avoidvoice.chatapi.ChatGptApi;

/*
테스트를 위한 activity class 후에 삭제 예정
 */
public class TestActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView2);
        ChatGptApi api = new ChatGptApi(this);

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {
                api.callAPI(editText.getText().toString().trim());
            }
        });

    }

    public void setTextView(String newText){
        textView.setText(newText);
    }
}