package com.example.mattilsynet;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    private View view;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeView();

        return view;
    }

    private void initializeView() {
        setClickListeners();
    }

    private void setClickListeners() {
        Button btn = view.findViewById(R.id.searchbtn);
        btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_search_result));
    }

}
