package com.example.avoidvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.avoidvoice.chatapi.ChatGptApi;
import com.example.avoidvoice.family.FamilyFragment;
import com.example.avoidvoice.main.MainFragment;
import com.example.avoidvoice.setting.SetFragment;
import com.example.avoidvoice.warning.WarningMessage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static BottomNavigationView bottomNavigationView;
    private int fragmentNum = 0;
    private Switch mainSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavi);

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new MainFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.bottom_home:
                        fragmentNum = 0;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MainFragment()).commit();
                        break;
                    case R.id.bottom_family:
                        fragmentNum = 1;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new FamilyFragment()).commit();
                        break;
                    case R.id.bottom_set:
                        fragmentNum = 2;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new SetFragment()).commit();
                        break;
                    case R.id.test_set:
                        Intent intent = new Intent(MainActivity.this, WarningMessage.class);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new SetFragment()).commit();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //TODO : 추후 수정
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
//        Button testButton = findViewById(R.id.testButton);
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent testintent = new Intent(MainActivity.this, TestActivity.class);
//                startActivity(testintent);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (fragmentNum) {
            case 0:
                View view = bottomNavigationView.findViewById(R.id.bottom_home);
                view.performClick();
                break;
            case 1:
                view = bottomNavigationView.findViewById(R.id.bottom_family);
                view.performClick();
                break;
            case 2:
                view = bottomNavigationView.findViewById(R.id.bottom_set);
                view.performClick();
                break;
        }
    }
}