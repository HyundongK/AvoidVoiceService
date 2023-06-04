package com.example.avoidvoice.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.avoidvoice.NotificationHelper;
import com.example.avoidvoice.R;
import com.example.avoidvoice.TestActivity;
import com.example.avoidvoice.service.PhoneStateReceiver;
import com.example.avoidvoice.service.VoiceAvoidService;
import com.example.avoidvoice.warning.WarningMessage;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private View view;
    private ViewPager viewPager;
    ArrayList<Integer> arrayList = new ArrayList<>();

    private Switch mainSwitch;
    private NotificationHelper mNotificationhelper;
    private SharedPreferences appData;
    private boolean saveSwitchData = false;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.main_fragment, container, false);
        viewPager = view.findViewById(R.id.viewPager);

        mainSwitch = view.findViewById(R.id.main_switch);

        appData = getContext().getSharedPreferences("appData", MODE_PRIVATE);
        load();

        //Intent intent = new Intent(getActivity(), TestActivity.class);
        mNotificationhelper = new NotificationHelper(getActivity().getApplicationContext());

        Intent warningIntent = new Intent(getActivity().getApplicationContext(), WarningMessage.class);


        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                save();
            }
        });

        arrayList.add(R.drawable.banknum5);
        arrayList.add(R.drawable.png1);
        arrayList.add(R.drawable.png2);
        arrayList.add(R.drawable.png3);
        arrayList.add(R.drawable.png4);
        arrayList.add(R.drawable.png5);
        arrayList.add(R.drawable.png6);
        arrayList.add(R.drawable.png7);
        arrayList.add(R.drawable.png8);
        arrayList.add(R.drawable.png9);
        arrayList.add(R.drawable.png10);



        MainAdapter mainAdapter = new MainAdapter(getActivity().getApplicationContext(), arrayList);
        viewPager.setAdapter(mainAdapter);

        return view;
    }

    public void sendOnChannel(String title, String message) {
        NotificationCompat.Builder nb = mNotificationhelper.getChannelNotification(title, message);
        mNotificationhelper.getManager().notify(1, nb.build());
    }

    private void save() {
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("MAIN_SWITCH", mainSwitch.isChecked());
        editor.apply();
    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveSwitchData = appData.getBoolean("MAIN_SWITCH", false);
        mainSwitch.setChecked(saveSwitchData);
    }
}
