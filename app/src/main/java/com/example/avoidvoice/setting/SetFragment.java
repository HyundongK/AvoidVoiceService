package com.example.avoidvoice.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.avoidvoice.R;

public class SetFragment extends Fragment {

//    private BottomNavigationView bottomNavigation;

    private View view;
    private Button numBtn;
    private NumberFragmentDialog fragmentDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.set_fragment, container, false);


        numBtn = view.findViewById(R.id.numBtn);
        numBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentDialog = new NumberFragmentDialog();
                fragmentDialog.show(getActivity().getSupportFragmentManager(), "dialog");

            }
        });

        return view;
    }
}
