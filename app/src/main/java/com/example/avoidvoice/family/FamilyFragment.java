package com.example.avoidvoice.family;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.avoidvoice.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FamilyFragment extends Fragment {

    private View view;
    private Button btn;
    private Button testBtn;
    EditText et;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.family_fragment, container, false);
        et = view.findViewById(R.id.numEdit);
        btn = view.findViewById(R.id.saveBtn);
        testBtn = view.findViewById(R.id.removeBtn);

        // 등록하기 버튼 클릭 시 내부 저장소에 번호 저장
        // Device File Explorer -> data -> data -> avoidvoice 패키지 -> file -> phoneNumber.txt
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이대로 하면 Bundle{number=010-xxxx-xxxx} 로 저장
//                Bundle bundle = new Bundle();
//                bundle.putString("number", et.getText().toString());

                // 이렇게 하면 010-xxxx-xxxx로 저장
                String number = et.getText().toString();

                try {
                    FileOutputStream fos = getActivity().openFileOutput("phoneNumber.txt", Context.MODE_APPEND);
                    PrintWriter writer = new PrintWriter(fos);

                    writer.println(number);
                    writer.flush();
                    writer.close();

                    et.setText("");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        // 테스트를 위한 삭제 버튼 추후 삭제 예정
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "phoneNumber.txt";
                FileDelete(str);
            }
        });

        // --------------------------------------------------------------
        // 문자 보내는 기능인데,,, 테스트를 어떻게 해야할지 모르겠습니다.
//        String strTmp;
//        String str = "phoneNumber.txt";
//
//        try {
//            FileInputStream fis = getActivity().openFileInput(str);
//            StringBuffer sb = new StringBuffer();
//            byte dataBuffer[] = new byte[128];
//
//            int n = 0;
//
//            while((n = fis.read(dataBuffer)) != -1){
//                sb.append(new String(dataBuffer));
//            }
//
//            strTmp = sb.toString();
//
//
////            try {
////                SmsManager smsManager = SmsManager.getDefault();
////                smsManager.sendTextMessage(strTmp, null, "보이스피싱 위험 감지", null, null);
////            }catch (Exception e){
////                //
////            }
//
//            fis.close();
//        } catch (Exception e){
//            //empty
//        }


        // --------------------------------------------------------------

        return view;
    }

    //테스트를 위한 내부 저장소 삭제 파일
    private void FileDelete(String str) {
        getActivity().deleteFile(str);
    }
}


