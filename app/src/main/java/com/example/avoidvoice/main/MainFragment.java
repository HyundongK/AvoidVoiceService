package com.example.avoidvoice.main;

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

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private View view;
    private ViewPager viewPager;
    ArrayList<Integer> arrayList = new ArrayList<>();
    private Button testNoti;
    private NotificationHelper mNotificationhelper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.main_fragment, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        testNoti = view.findViewById(R.id.NotiTest);

        mNotificationhelper = new NotificationHelper(getActivity().getApplicationContext());

        testNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "보이스피싱 알림";
                String message = "보이스피싱 위험이 있습니다.";
                sendOnChannel(title, message);
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

    private void sendOnChannel(String title, String message) {
        NotificationCompat.Builder nb = mNotificationhelper.getChannelNotification(title, message);
        mNotificationhelper.getManager().notify(1, nb.build());
    }
}
