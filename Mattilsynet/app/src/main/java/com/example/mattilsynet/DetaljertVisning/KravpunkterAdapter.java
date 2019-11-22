package com.example.mattilsynet.DetaljertVisning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mattilsynet.R;

import java.util.ArrayList;

public class KravpunkterAdapter extends RecyclerView.Adapter<KravpunkterAdapter.kravpunktHolder>{

    private final ArrayList<DetaljerKravpunkterKort> kravpunkterListe;
    private LayoutInflater inflater;
    private final String LOG_TAG = KravpunkterAdapter.class.getSimpleName();

    public KravpunkterAdapter(Context context, ArrayList<DetaljerKravpunkterKort> kravpunkterListe){
        inflater = LayoutInflater.from(context);
        this.kravpunkterListe = kravpunkterListe;
    }

    @NonNull
    @Override
    public kravpunktHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.kravpunkter_kort_layout, parent, false);
        return new kravpunktHolder(itemView,this);
    }

    class kravpunktHolder extends RecyclerView.ViewHolder {
        //public final ImageView sportImg;
        LinearLayout kontainer;
        private final TextView kravpunktTittel;
        private final TextView kravpunktKarakter;
        final KravpunkterAdapter adapter;

        private kravpunktHolder(View itemView, KravpunkterAdapter adapter){
            super(itemView);
            //this.kravpunktKarakter = itemView.findViewById(R.id.card_post_title);
            kontainer = itemView.findViewById(R.id.kravpunkt_kort);
            this.kravpunktTittel = itemView.findViewById(R.id.detaljer_kravpunkt_tittel);
            this.kravpunktKarakter = itemView.findViewById(R.id.detaljer_kravpunkt_karakter);
            this.adapter = adapter;
            //itemView.setOnClickListener(Snackbar.make(itemView, "Dette skal føre til kommentarer", Snackbar.LENGTH_LONG).show());
        }

    }

    @Override
    public void onBindViewHolder(@NonNull kravpunktHolder holder, int position) {
        holder.kravpunktTittel.setText(kravpunkterListe.get(position).getKravpunktTittel());
        holder.kravpunktKarakter.setText(kravpunkterListe.get(position).getKravpunktKarakter());
        //listeScrollAnimasjon(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return kravpunkterListe.size();
    }

}