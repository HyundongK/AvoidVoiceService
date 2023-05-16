package com.example.avoidvoice.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.ListFragment;

import com.example.avoidvoice.R;
import com.example.avoidvoice.family.FamilyFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class NumberFragmentDialog extends DialogFragment {
    private final String fileName = "items.list";

    private View view;
//    private TextView testV;

    private Button delBtn;
    private ListView phoneNumberListView;
    private ArrayAdapter adapter;
    private ArrayList<String> items = new ArrayList<String>();

    public NumberFragmentDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.phone_number_view, container, false);
        delBtn = view.findViewById(R.id.delBtn);
        phoneNumberListView = view.findViewById(R.id.numberList);
//        testV = view.findViewById(R.id.testSet);
//        testV.setText(FileLoad());
        adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_single_choice, items);
        phoneNumberListView.setAdapter(adapter);

        loadItemFromFile();
        adapter.notifyDataSetChanged();

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count;
                int checkedIndex;

                count = adapter.getCount();

                if(count > 0){
                    checkedIndex = phoneNumberListView.getCheckedItemPosition();
                    if(checkedIndex > -1 && checkedIndex < count){
                        items.remove(checkedIndex);

                        phoneNumberListView.clearChoices();

                        adapter.notifyDataSetChanged();

//                        FamilyFragment.saveItemsToFile();
                    }
                }
            }
        });

        return view;
    }

//    private void saveItemsToFile() {
//        File file = new File(getActivity().getFilesDir(), fileName);
//        FileWriter fw = null;
//        BufferedWriter bufwr = null;
//
//        try{
//            fw = new FileWriter(file);
//            bufwr = new BufferedWriter(fw);
//
//            for(String str : items){
//                bufwr.write(str);
//                bufwr.newLine();
//            }
//
//            bufwr.flush();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        try{
//            if(bufwr != null){
//                bufwr.close();
//            }
//
//            if(fw != null){
//                fw.close();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private void loadItemFromFile(){
        File file = new File(getActivity().getFilesDir(), fileName);
        FileReader fr = null;
        BufferedReader bufrd =null;
        String str;

        if(file.exists()){
            try{
                fr = new FileReader(file);
                bufrd = new BufferedReader(fr);

                while((str = bufrd.readLine()) != null){
                    items.add(str);
                }

                bufrd.close();
                fr.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    public String FileLoad(){
//        String strTmp;
//        try{
//            FileInputStream fis = getActivity().openFileInput("phoneNumber.txt");
//            StringBuffer sb = new StringBuffer();
//            byte dataBuffer[] = new byte[1024];
//            int n = 0;
//
//            while((n = fis.read(dataBuffer)) != -1){
//                sb.append(new String(dataBuffer));
//            }
//
//            strTmp = sb.toString();
//            fis.close();
//        }
//        catch (Exception e){
//            return "";
//        }
//        return strTmp;
//    }
}
