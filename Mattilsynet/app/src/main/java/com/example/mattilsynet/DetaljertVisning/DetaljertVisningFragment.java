package com.example.mattilsynet.DetaljertVisning;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mattilsynet.R;

public class DetaljertVisningFragment extends Fragment {

    public DetaljertVisningFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_place_view, container, false);

        TextView text = view.findViewById(R.id.detailed_view);

        final Bundle arguments = getArguments();
        if (arguments != null) {
            text.setText(arguments.getString("title"));
        }

        return view;
    }
}
