package com.example.avoidvoice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.avoidvoice.chatapi.APIHandler;
import com.example.avoidvoice.chatapi.ChatGptApi;
import com.example.avoidvoice.chatapi.TranslateText;

/*
테스트를 위한 activity class 후에 삭제 예정
 */
public class TestActivity extends Activity {

    private View view;
    TextView textView;
    EditText editText;
    Button button;

    APIHandler apiHandler = new APIHandler(this);

    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        view = inflater.inflate(R.layout.activity_test, container, false);
//
//        editText = view.findViewById(R.id.editText);
//        button = view.findViewById(R.id.button);
//        textView = view.findViewById(R.id.textView2);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
//            @Override
//            public void onClick(View view) {
//                apiHandler.run(editText.getText().toString().trim());
//            }
//        });
//
//        return view;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView2);


        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {
                apiHandler.run(editText.getText().toString().trim());
            }
        });

    }

    public void setTextView(String newText){
        textView.setText(newText);
    }
}