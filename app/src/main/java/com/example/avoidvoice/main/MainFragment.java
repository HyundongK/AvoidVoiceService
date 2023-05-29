package com.example.avoidvoice.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.avoidvoice.NotificationHelper;
import com.example.avoidvoice.R;
import com.example.avoidvoice.TestActivity;
import com.example.avoidvoice.service.VoiceAvoidService;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private View view;
    private ViewPager viewPager;
    ArrayList<Integer> arrayList = new ArrayList<>();
    private Button startBTN;
    private Button stopBTN;
    private NotificationHelper mNotificationhelper;
    private static boolean serviceStarted = false;
    private Intent intent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.main_fragment, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        startBTN = view.findViewById(R.id.startBTN);
        stopBTN = view.findViewById(R.id.stopBTN);

        //Intent intent = new Intent(getActivity(), TestActivity.class);
        mNotificationhelper = new NotificationHelper(getActivity().getApplicationContext());

        intent = new Intent(getContext(), VoiceAvoidService.class);
        intent.putExtra("test", true);
        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!serviceStarted) {
                    serviceStarted = true;
                    getContext().startService(intent);
                }
            }
        });

        stopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceStarted) {
                    serviceStarted = false;
                    getContext().stopService(intent);
                }

//                String title = "보이스피싱 알림";
//                String message = "보이스피싱 위험이 있습니다.";
//                sendOnChannel(title, message);
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
}
