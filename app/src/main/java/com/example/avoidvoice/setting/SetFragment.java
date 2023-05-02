package com.example.avoidvoice.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.avoidvoice.R;

public class SetFragment extends Fragment {

//    private BottomNavigationView bottomNavigation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.set_fragment, container, false);
    }
}
