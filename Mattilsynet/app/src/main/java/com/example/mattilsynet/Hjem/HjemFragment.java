package com.example.mattilsynet.Hjem;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.mattilsynet.R;

public class HjemFragment extends Fragment {

    private View view;
    public HjemFragment() {}

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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(getContext(), v);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_search_result);
            }
        });
    }

    //Legger ned virtuelt tastatur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
