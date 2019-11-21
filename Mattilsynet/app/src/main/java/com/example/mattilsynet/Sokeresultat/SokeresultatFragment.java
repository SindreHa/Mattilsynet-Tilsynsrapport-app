package com.example.mattilsynet.Sokeresultat;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mattilsynet.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class SokeresultatFragment extends Fragment implements InfoListeAdapter.OnItemClickListener,
        Response.Listener<String>, Response.ErrorListener  {

    private View view;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout oppdater;
    ImageView fjernFilter;
    CardView fjernFilterKort;
    private InfoListeAdapter infoAdapter;
    String filterArstall = "";
    private ArrayList<InfoKort> infoListe = new ArrayList<>();
    private final String LOG_TAG = SokeresultatFragment.class.getSimpleName();
    public final static String ENDPOINT = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_sokeresultat, container, false);

        setHasOptionsMenu(false);
        initialiserView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (infoListe.isEmpty()) { initialiserData(); }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Holder aktivt filter knapp synlig når man returnerer til fragment fra detaljert visning fragment
        if (!filterArstall.matches("")) {
            TextView klarerFilterVerdi = view.findViewById(R.id.klarer_filter_verdi);
            klarerFilterVerdi.setText(filterArstall);
            fjernFilterKort.setVisibility(View.VISIBLE);
            fjernFilterKort.setScaleX(1);
            fjernFilterKort.setScaleY(1);
        }
    }

    private void initialiserView() {
        recyclerViewInit();
        settKlikkLyttere();
    }

    private void settKlikkLyttere() {
        CardView knapp = view.findViewById(R.id.filter_sok);
        knapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog();
            }
        });

        fjernFilter = view.findViewById(R.id.klarer_filter);
        fjernFilterKort = view.findViewById(R.id.klarer_filter_kort);
        fjernFilterKort.setVisibility(View.INVISIBLE);

        fjernFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fjernFilterKort.animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setDuration(250)
                        .start();

                filterArstall = "";
                initialiserData();
            }
        });
    }

    private void recyclerViewInit() {
        mRecyclerView = view.findViewById(R.id.recyclerView_sokeresultat);
        oppdaterRecyclerView();
        recyclerViewMetoder();
    }

    private void oppdaterRecyclerView() {
        infoAdapter = new InfoListeAdapter(getContext(), infoListe, new InfoListeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfoKort infoKort) {
                Bundle b = new Bundle();
                b.putString("tilsynid", infoKort.getTilsynId());
                b.putString("stedNavn", infoKort.getStedNavn());
                b.putString("stedOrgNr", infoKort.getStedOrgNr());
                b.putString("rapportDato", infoKort.getStedDato());
                b.putString("stedKarakter", infoKort.getStedKarakter());
                b.putString("stedAdresse", infoKort.getStedAdresse());
                b.putString("stedPostKode", infoKort.getStedPostkode());
                b.putString("stedPostSted", infoKort.getStedPoststed());
                Navigation.findNavController(view).navigate(R.id.action_nav_search_result_to_nav_detailed_view, b);
            }
        });
        mRecyclerView.setAdapter(infoAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void recyclerViewMetoder() {

        //Refresh listener
        oppdater = view.findViewById(R.id.refresh_sokreresultat);
        oppdater.setColorSchemeColors(getResources().getColor(R.color.colorAccent), Color.RED, Color.GREEN);
        oppdater.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorPrimary));
        oppdater.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initialiserData();
                    }
                }
        );

        //Swipe listener
        ItemTouchHelper.SimpleCallback helper = new ItemTouchHelper.SimpleCallback(
                0,ItemTouchHelper.LEFT)
        {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.LEFT) {
                    final InfoKort kort = infoAdapter.getInfoKort().get(viewHolder.getAdapterPosition());
                    final int position = viewHolder.getAdapterPosition();

                    final Snackbar snackBar = Snackbar.make(getView(), "Sted fjernet fra søk", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Angre", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Her kan man legge inn angrefunksjon på sletting av innlegg
                            snackBar.dismiss();
                            infoAdapter.restoreInfoKort(kort, position);
                        }
                    });

                    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom))
                            .setTitle("Fjerne sted fra søk?")
                            .setPositiveButton("Fjern", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Hvis bruker trykker Fjern
                                    infoListe.remove(position);
                                    infoAdapter.notifyItemRemoved(position);
                                    snackBar.show();
                                }
                            })
                            .setNegativeButton("Avbryt", null) //Dismiss gjør samme som Avbryt knappen
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    //Hvis bruker trykker utenfor dialogvindu for å fjerne det eller avbryt knapp
                                    infoAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .create();
                    dialog.show();
                }
            }

            /*
             * Bruker her et bibliotek fra github som sparer mye koding for å sette opp tekst og ikon bak
             * kort på swipe, som da her "Fjern fra søk" med et søppelbøtte ikon
             * https://github.com/xabaras/RecyclerViewSwipeDecorator
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftActionIconTint(Color.RED)
                        .setSwipeLeftLabelTextSize(2, 20)
                        .addSwipeLeftLabel("Fjern fra søk")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) { return false; }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(helper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initialiserData() {
        hentData();
    }

    private void hentData() {
        oppdater.setRefreshing(true);
        infoListe.clear();
        final Bundle sokeDataBundle = getArguments();
        String infoliste_URL = ENDPOINT + sokeDataBundle.getString("sokeKriterier") + "&dato=*" + filterArstall;
        //Log.d(LOG_TAG, infoliste_URL);
        if (isOnline()){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, infoliste_URL, this, this);
            queue.add(stringRequest);
        }else{
            lagSnackbar("Ingen nettverkstilgang. Kan ikke laste varer.");
        }

        /*infoListe.add(new InfoKort(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 1", "6242", "Stedsnavn", R.drawable.smilefjes));
        infoListe.add(new InfoKort(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 2", "6242", "Stedsnavn", R.drawable.noytralfjes));
        infoListe.add(new InfoKort(getString(R.string.placeholder_title), "Org nr: 5231761235", "Gatenavn 3", "6242", "Stedsnavn", R.drawable.noytralfjes));*/
    }

    // Sjekker om nettverkstilgang
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onResponse(String response) {

        oppdater.setRefreshing(false);
        LinearLayout ingenTreff = view.findViewById(R.id.no_search_result);
        String sResponse = response;
        String sok  = ":[]"; //Hvis det ikke er noen treff viser entries tabellen :[]

        if (sResponse.toLowerCase().contains(sok.toLowerCase())) {
            ingenTreff.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            ingenTreff.setVisibility(View.GONE);
        }
        try{
            //Log.d(LOG_TAG, response);
            infoListe = InfoKort.createInfoCard(response);
            oppdaterRecyclerView();
        }catch (Exception e){
            Log.d(LOG_TAG, "Error: " + e);
        }
        infoAdapter.notifyDataSetChanged();
        recyclerAnimasjon();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private void recyclerAnimasjon() {
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
    public void onItemClick(InfoKort card) { }

    public void filterDialog() {
        final TextView klarerFilterVerdi = view.findViewById(R.id.klarer_filter_verdi);
        //https://stackoverflow.com/questions/2795300/how-to-implement-a-custom-alertdialog-view
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.filter_dialog, null);
        final EditText text = dialoglayout.findViewById(R.id.filter);
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom))
                .setTitle("Skriv inn ønsket dato")
                .setMessage("Skriv inn dato uten mellomtegn, f.eks 012018 gir deg alt fra januar 2018")
                .setView(dialoglayout)
                .setPositiveButton("Søk", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filterArstall = text.getText().toString();

                        if (!filterArstall.matches("")) {
                            klarerFilterVerdi.setText(filterArstall);
                            fjernFilterKort.setScaleX(0);
                            fjernFilterKort.setScaleY(0);
                            fjernFilterKort.setVisibility(View.VISIBLE);
                            fjernFilterKort.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(250)
                                    .start();

                            initialiserData();
                        }
                    }
                })
                .setNegativeButton("Avbryt", null)
                .create();
        dialog.show();
        text.requestFocus();
    }

    private void lagSnackbar(String melding) {
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
