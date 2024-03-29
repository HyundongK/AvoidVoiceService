package com.example.avoidvoice.setting;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.avoidvoice.R;
import com.example.avoidvoice.service.VoiceAvoidService;

public class SetFragment extends Fragment {

//    private BottomNavigationView bottomNavigation;

    private View view;
    private Button numBtn;
    private Switch smsSwitch;
    private Button startBTN;
    private Button stopBTN;
    private LinearLayout numLayout;
    private NumberFragmentDialog fragmentDialog;

    private SharedPreferences appData;
    private boolean saveSwitchData = false;

    private Intent intent;

    private static boolean serviceStarted = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.set_fragment, container, false);

        numBtn = view.findViewById(R.id.numBtn);
        smsSwitch = view.findViewById(R.id.smsSwitch);
        numLayout = view.findViewById(R.id.numLayout);
        startBTN = view.findViewById(R.id.startBTN);
        stopBTN = view.findViewById(R.id.stopBTN);

        appData = getContext().getSharedPreferences("appData", MODE_PRIVATE);
        load();

        numBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentDialog = new NumberFragmentDialog();
                fragmentDialog.show(getActivity().getSupportFragmentManager(), "dialog");

            }
        });
        numLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentDialog = new NumberFragmentDialog();
                fragmentDialog.show(getActivity().getSupportFragmentManager(), "dialog");

            }
        });


        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                save();
            }
        });

        intent = new Intent(getContext(), VoiceAvoidService.class);

        intent.putExtra("test", 2);
        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!serviceStarted) {
                    serviceStarted = true;
                    getContext().startService(intent);
                }
            }
        });

        stopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceStarted) {
                    serviceStarted = false;
                    getContext().stopService(intent);
                }

//                String title = "보이스피싱 알림";
//                String message = "보이스피싱 위험이 있습니다.";
//                sendOnChannel(title, message);
            }
        });

        return view;
    }

    private void save() {
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("SMS_SWITCH", smsSwitch.isChecked());
        editor.apply();
    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveSwitchData = appData.getBoolean("SMS_SWITCH", false);
        smsSwitch.setChecked(saveSwitchData);
    }
}
