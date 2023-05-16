package com.example.avoidvoice.family;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.avoidvoice.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FamilyFragment extends Fragment {
    private final String fileName = "items.list";

    private View view;
    private Button btn;
    private Button testBtn;
    private EditText et;
    private ArrayAdapter adapter;
    private ArrayList<String> items = new ArrayList<String>();
    private Button SMStest;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.family_fragment, container, false);
        btn = view.findViewById(R.id.saveBtn);
        et = view.findViewById(R.id.numEdit);
//        testBtn = view.findViewById(R.id.removeBtn);
        SMStest = view.findViewById(R.id.testSMS);

        adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_single_choice, items);

        // 등록하기 버튼 클릭 시 내부 저장소에 번호 저장
        // Device File Explorer -> data -> data -> avoidvoice 패키지 -> file -> phoneNumber.txt
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이대로 하면 Bundle{number=010-xxxx-xxxx} 로 저장
//                Bundle bundle = new Bundle();
//                bundle.putString("number", et.getText().toString());
                String strNew = (String) et.getText().toString();

                if(strNew.length() > 0){
                    items.add(strNew);

                    et.setText("");

                    adapter.notifyDataSetChanged();

                    saveItemsToFile();

                }
                // 이렇게 하면 010-xxxx-xxxx로 저장
//                String number;
//                number = et.getText().toString();
//
//                try {
//                    FileOutputStream fos = getActivity().openFileOutput("phoneNumber.txt", Context.MODE_APPEND);
//                    PrintWriter writer = new PrintWriter(fos);
//
//                    writer.println(number);
//                    writer.flush();
//                    writer.close();
//
//                    et.setText("");
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

            }
        });

        // 테스트를 위한 삭제 버튼 추후 삭제 예정
//        testBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                String str = "phoneNumber.txt";
//                FileDelete(fileName);
//            }
//        });

        // --------------------------------------------------------------
        // 문자 보내는 기능인데,,, 테스트를 어떻게 해야할지 모르겠습니다.
//        SMStest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String strTmp;
//                String str = "phoneNumber.txt";
//
//                try {
//                    FileInputStream fis = getActivity().openFileInput(str);
//                    StringBuffer sb = new StringBuffer();
//                    byte dataBuffer[] = new byte[128];
//
//                    int n = 0;
//
//                    while((n = fis.read(dataBuffer)) != -1){
//                        sb.append(new String(dataBuffer));
//                    }
//
//                    strTmp = sb.toString();
//
//
//                    try {
//                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage(strTmp, null, "보이스피싱 위험 감지", null, null);
//                    }catch (Exception e){
//                        //
//                    }
//
//                    fis.close();
//                } catch (Exception e){
//                    //empty
//                }
//            }
//        });

        // --------------------------------------------------------------

        return view;
    }

    private void saveItemsToFile() {
        File file = new File(getActivity().getFilesDir(), fileName);
        FileWriter fw = null;
        BufferedWriter bufwr = null;

        if(file.exists()) {
            try {
                fw = new FileWriter(file);
                bufwr = new BufferedWriter(fw);

                for (String str : items) {
                    bufwr.write(str);
                    bufwr.newLine();
                }

                bufwr.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (bufwr != null) {
                    bufwr.close();
                }

                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //테스트를 위한 내부 저장소 삭제 파일
//    private void FileDelete(String str) {
//        getActivity().deleteFile(str);
//    }
}


