package com.example.mattilsynet.Sokeresultat;


import android.app.Activity;
import android.content.Context;
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
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private ImageView fjernFilter;
    private CardView fjernFilterKort;
    private InfoListeAdapter infoAdapter;
    private String filterArstall = "";
    private ArrayList<InfoKort> infoListe = new ArrayList<>();
    private final String LOG_TAG = SokeresultatFragment.class.getSimpleName();
    private final static String TILSYN_URL = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_sokeresultat, container, false);

        initialiserView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        /*
        * Hent data kun hvis det ikke er data i ArrayList, trengs
        * for når bruker går tilbake fra detaljert visning
         */
        if (infoListe.isEmpty()) {
            initialiserData();
        }

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

    /*
    * Metoder for grafiske elementer
    */

    private void initialiserView() {
        recyclerViewInit();
        settKlikkLyttere();
    }

    //Setter opp recyclerview som viser søkeresultater
    private void recyclerViewInit() {
        mRecyclerView = view.findViewById(R.id.recyclerView_sokeresultat);
        oppdaterRecyclerView();
        recyclerViewFunksjoner();
    }

    //Setter adapter på recyclerview som holder all data
    private void oppdaterRecyclerView() {
        infoAdapter = new InfoListeAdapter(getContext(), infoListe, new InfoListeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfoKort infoKort) {
                //Lag bundle med all data fra kort og send til detaljert visning
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

    //Setter opp diverse klikk lyttere i recyclerview som klikk på kort
    private void settKlikkLyttere() {
        //Knapp for filtrering på år
        CardView knapp = view.findViewById(R.id.filter_sok);
        knapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Åpne dialog for å skrive inn datofiltrering
                filterDialog();
            }
        });

        //Rød knapp med aktiv datofiltrering, kan krysses ut for å fjerne filter
        fjernFilter = view.findViewById(R.id.klarer_filter);
        fjernFilterKort = view.findViewById(R.id.klarer_filter_kort);
        fjernFilterKort.setVisibility(View.INVISIBLE);
        fjernFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Animasjon for å skjule knappen
                fjernFilterKort.animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setDuration(250)
                        .start();

                //Setter filter til null for neste volleyinnhenting som ikke skal filtrere
                filterArstall = "";
                oppdater.setRefreshing(true);
                //Kjører animasjon ut, som avslutter med å kjøre initialiserData()
                recyclerAnimasjonUt();
            }
        });
    }

    //Setter opp funksjoner i recyclerview som swipe av kort og dra ned for å oppdatere data
    private void recyclerViewFunksjoner() {

        //Lytter for oppdatering, bruker kan dra ned for å oppdatere
        oppdater = view.findViewById(R.id.refresh_sokreresultat);
        oppdater.setColorSchemeColors(Color.RED, Color.GREEN, Color.CYAN);
        oppdater.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorPrimary));
        oppdater.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Kjører animasjon ut, som avslutter med å kjøre initialiserData()
                        recyclerAnimasjonUt();
                    }
                }
        );

        //Lytter for swipe av kort
        ItemTouchHelper.SimpleCallback helper = new ItemTouchHelper.SimpleCallback(
                0,ItemTouchHelper.LEFT)
        {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                //Kort kan kun swipes til venstre
                if (direction == ItemTouchHelper.LEFT) {
                    //Lagrer instans av gjeldende kort som brukes til å gjenoprette kortet
                    final InfoKort kort = infoAdapter.getInfoKort().get(viewHolder.getAdapterPosition());
                    //Lagring av posisjon i ArrayListen
                    final int position = viewHolder.getAdapterPosition();
                    //Snackbar som vises etter sletting som gir mulighet til å angre sletting
                    final Snackbar snackBar = Snackbar.make(getView(), "Sted fjernet fra søk", Snackbar.LENGTH_LONG);
                    snackBar.setAction("Angre", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Kjører gjenoppretting av kort med instans lagret tidligere
                            infoAdapter.restoreInfoKort(kort, position);
                            snackBar.dismiss();
                        }
                    });

                    //Dialogvindu som vises etter swipe med som spør om bruker vil fjerne kortet
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

    //Funksjon som viser en popup dialog for filtrering
    private void filterDialog() {
        final TextView klarerFilterVerdi = view.findViewById(R.id.klarer_filter_verdi);
        /*
        * Dialogvindu ved trykk på "Filter" knapp, har her brukt egendefinertlayout med hjelp av StackOverflow
        * https://stackoverflow.com/questions/2795300/how-to-implement-a-custom-alertdialog-view
        */
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.filter_dialog_edittext, null); //Edittext layout i dialog
        final EditText text = dialoglayout.findViewById(R.id.filter); //tekstfelt bruker skriver inn i
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom))
                .setTitle("Skriv inn ønsket dato")
                .setMessage("Skriv inn dato uten mellomtegn, f.eks 012018 gir deg alt fra januar 2018")
                .setView(dialoglayout)
                .setPositiveButton("Søk", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Setter filterArstall til brukes sin input
                        filterArstall = text.getText().toString();

                        //Sjekker om bruker har skrivd inn data, hvis ikke skjer ingenting
                        if (!filterArstall.matches("")) {
                            klarerFilterVerdi.setText(filterArstall);
                            //Animasjon som gjør at aktivt filter "popper ut"
                            fjernFilterKort.setScaleX(0);
                            fjernFilterKort.setScaleY(0);
                            fjernFilterKort.setVisibility(View.VISIBLE);
                            fjernFilterKort.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(250)
                                    .start();

                            oppdater.setRefreshing(true);
                            //Kjører animasjon ut, som avslutter med å kjøre initialiserData()
                            recyclerAnimasjonUt();
                        }
                    }
                })
                .setNegativeButton("Avbryt", null)
                .create();
        dialog.show();
        text.requestFocus();
    }

    //Enkel funksjon som viser en snackbar med melding som parameter
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

    //Animasjon som får kortene til å skli inn fra bunn
    private void recyclerAnimasjonInn() {
        //https://stackoverflow.com/questions/38909542/how-to-animate-recyclerview-items-when-adapter-is-initialized-in-order
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            /*
                            * Animasjon som animerer hvert kort synlig i view.
                            * pr loop økes delay for å gi en mykere animasjon
                             */
                            View v = mRecyclerView.getChildAt(i);
                            View reCycler = mRecyclerView;
                            //Starter med å sette posisjon til bunn av skjermen
                            v.setTranslationY(reCycler.getHeight());
                            v.animate()
                                    .translationY(0) //Sender kort til toppen av recyclerView eller under neste kort
                                    .setInterpolator(new DecelerateInterpolator(2)) //Animasjon timing
                                    .setDuration(800) //Lengde i ms på animasjon pr kort
                                    .setStartDelay(i * 130) //Tid før neste kort skal animeres
                                    .start();
                        }
                        return true;
                    }
                });

    }

    //Animasjon som får kortene til å skli ut til siden
    private void recyclerAnimasjonUt(){
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            /*
                             * Animasjon som fader ut hvert kort synlig i view.
                             * pr loop økes delay for å gi en mykere animasjon
                             */
                            View v = mRecyclerView.getChildAt(i);
                            View pRecycler = mRecyclerView;
                            v.animate()
                                    .translationX(pRecycler.getWidth()) //Setter posisjon helt til høyre i view
                                    .setInterpolator(new AccelerateDecelerateInterpolator()) //Animasjon timing
                                    .alpha(-1f) //Kort blir totalt gjennomsiktig halvveis inn i animasjonen
                                    .setDuration(600) //Lengde i ms på animasjon pr kort
                                    .setStartDelay(i * 80) //Tid før neste kort skal animeres
                                    .start();
                        }
                        return true;
                    }
                });

        //Vent på at animasjon fullfører før innhenting av ny data
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                initialiserData();
            }
        }, 700);
    }

    /*
     * Metoder for innhenting av data
     */

    private void initialiserData() {
        hentData();
    }

    //Funksjon som henter inn data med volley
    private void hentData() {
        //Setter oppdaterings sirkel til å vises så bruker ser at innhenting pågår
        oppdater.setRefreshing(true);
        //Tømmer gammel data fra ArrayListe
        infoListe.clear();
        //Henter ut bundle hvor søkekriteriene fra hjemskjerm ligger
        final Bundle sokeDataBundle = getArguments();
        String infoliste_URL = TILSYN_URL + sokeDataBundle.getString("sokeKriterier") + "&dato=*" + filterArstall;
        if (isOnline()){
            //Lager ny volleyrequest med opprettet url
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, infoliste_URL, this, this);
            queue.add(stringRequest);
        }else {
            oppdater.setRefreshing(false);
            lagSnackbar("Ingen nettverkstilgang");
        }
     }

    //Sjekker nettverkstilgang
    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //Når volley suksessfult får response fra url
    @Override
    public void onResponse(String response) {

        oppdater.setRefreshing(false);
        LinearLayout ingenTreff = view.findViewById(R.id.no_search_result);
        String sok  = ":[]"; //Hvis det ikke er noen treff viser entries tabellen :[]

        if (response.toLowerCase().contains(sok.toLowerCase())) {
            ingenTreff.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            ingenTreff.setVisibility(View.GONE);
        }
        try{
            //Log.d(LOG_TAG, response);
            infoListe = InfoKort.lagInfoKort(response);
            oppdaterRecyclerView();
        }catch (Exception e){
            Log.d(LOG_TAG, "Error: " + e);
        }
        infoAdapter.notifyDataSetChanged();
        recyclerAnimasjonInn();
    }

    //Når volley får en error på response
    @Override
    public void onErrorResponse(VolleyError error) {
        lagSnackbar("Error med innlasting av data");
        Log.d(LOG_TAG, "onErrorResponse: " + error.getMessage());
    }

    @Override
    public void onItemClick(InfoKort card) { }

    //Legger ned virtuelt tastatur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
