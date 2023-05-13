package com.example.avoidvoice.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.ListFragment;

import com.example.avoidvoice.R;

import java.io.FileInputStream;

public class NumberFragmentDialog extends DialogFragment /*, ListFragment*/ {

    private View view;
    private TextView testV;

//    private ListView phoneNumberListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.phone_number_view, container, false);
        testV = view.findViewById(R.id.testSet);
        testV.setText(FileLoad());

//        phoneNumberListView = view.findViewById(R.id.numberList);

        return view;
    }

    public String FileLoad(){
        String strTmp;
        try{
            FileInputStream fis = getActivity().openFileInput("phoneNumber.txt");
            StringBuffer sb = new StringBuffer();
            byte dataBuffer[] = new byte[1024];
            int n = 0;

            while((n = fis.read(dataBuffer)) != -1){
                sb.append(new String(dataBuffer));
            }

            strTmp = sb.toString();
            fis.close();
        }
        catch (Exception e){
            return "";
        }
        return strTmp;
    }
}
