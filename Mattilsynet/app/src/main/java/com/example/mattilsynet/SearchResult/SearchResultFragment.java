package com.example.mattilsynet.SearchResult;


import android.app.Activity;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mattilsynet.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;


public class SearchResultFragment extends Fragment implements InfoListAdapter.OnItemClickListener,
        Response.Listener<String>, Response.ErrorListener  {

    private View view;
    private RecyclerView mRecyclerView;
    private InfoListAdapter infoAdapter;
    private ArrayList<InfoCard> infoList = new ArrayList<>();
    private final String LOG_TAG = SearchResultFragment.class.getSimpleName();
    public final static String ENDPOINT = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_search_result, container, false);

        initializeView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeData();
    }

    private void initializeView() {
        recyclerViewInit();
    }

    private void recyclerViewInit() {
        mRecyclerView = view.findViewById(R.id.recyclerView_search_result);
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        infoAdapter = new InfoListAdapter(getContext(), infoList, new InfoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfoCard card) {
                Bundle b = new Bundle();
                b.putString("title", card.getPlaceAddress());
                Navigation.findNavController(view).navigate(R.id.action_nav_search_result_to_nav_detailed_view, b);
            }
        });
        mRecyclerView.setAdapter(infoAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void initializeData() {
        getData();
        postAnimation();
    }

    private void getData() {
        infoList.clear();

        String vareliste_URL = ENDPOINT + "?dato=*2016";
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, vareliste_URL, this, this);
            queue.add(stringRequest);
        }else{
            makeSnackbar("Ingen nettverkstilgang. Kan ikke laste varer.");
        }

        /*infoList.add(new InfoCard(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 1", "6242", "Stedsnavn", R.drawable.smilefjes));
        infoList.add(new InfoCard(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 2", "6242", "Stedsnavn", R.drawable.noytralfjes));
        infoList.add(new InfoCard(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 3", "6242", "Stedsnavn", R.drawable.noytralfjes));*/
    }

    // Sjekker om nettverkstilgang
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onResponse(String response) {
        try{
            Log.d(LOG_TAG, response);
            infoList = InfoCard.createInfoCard(response);
            updateRecyclerView();
        }catch (Exception e){
           // Log.d(LOG_TAG, "feil");
        }
        //Aktiver scrolling for recyclerview etter animasjon
        infoAdapter.notifyDataSetChanged();
        postAnimation();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private void postAnimation() {
        //https://stackoverflow.com/questions/38909542/how-to-animate-recyclerview-items-when-adapter-is-initialized-in-order
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int tid = 450;
                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            tid = tid + 100;
                            View v = mRecyclerView.getChildAt(i);
                            View reCycler = mRecyclerView;
                            v.setAlpha(1.0f);
                            v.setTranslationY(reCycler.getHeight());
                            v.animate()
                                    .translationY(0)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .alpha(1.0f)
                                    .setDuration(tid)
                                    .setStartDelay(i * 50)
                                    .start();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onItemClick(InfoCard card) { }

    private void makeSnackbar(String melding) {
        final Snackbar snackBar = Snackbar.make(view, melding, Snackbar.LENGTH_LONG);
        snackBar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }
}
