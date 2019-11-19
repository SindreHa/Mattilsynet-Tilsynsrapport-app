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
import android.widget.EditText;

import com.example.mattilsynet.R;

public class HjemFragment extends Fragment {

    private View view;
    private final String LOG_TAG = HjemFragment.class.getSimpleName();

    public HjemFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_hjem, container, false);

        initialiserView();

        return view;
    }

    private void initialiserView() {
        settKlikkLyttere();
    }

    private void settKlikkLyttere() {
        Button knapp = view.findViewById(R.id.sokeknapp);

        knapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom(getContext(), v);

                Bundle b = new Bundle();
                b.putString("sokeKriterier", hentSokeKriterierURL());
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_search_result, b);
            }
        });
    }

    private String hentSokeKriterierURL() {

        EditText stedNavn = view.findViewById(R.id.sok_stednavn);
        String sStedNavn = stedNavn.getText().toString();

        EditText postSted = view.findViewById(R.id.sok_poststed);
        String sPostSted = postSted.getText().toString();

        return "navn=" + sStedNavn + "&poststed=" + sPostSted;
    }

    //Legger ned virtuelt tastatur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
