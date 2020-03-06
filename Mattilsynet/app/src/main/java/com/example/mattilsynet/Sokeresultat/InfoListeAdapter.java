package com.example.mattilsynet.Sokeresultat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mattilsynet.R;

import java.util.ArrayList;

public class InfoListeAdapter extends RecyclerView.Adapter<InfoListeAdapter.InfoKortHolder>{

    //ClickListener interface for infokort
    public interface OnItemClickListener {
        void onItemClick(InfoKort card);
    }

    private final ArrayList<InfoKort> infoListe;
    private final OnItemClickListener lytter;
    private LayoutInflater inflater;
    private int forrigePosisjon = -1;
    private Context context;

    public InfoListeAdapter(Context context, ArrayList<InfoKort> infoListe, OnItemClickListener lytter){
        inflater = LayoutInflater.from(context);
        this.infoListe = infoListe;
        this.context = context;
        this.lytter = lytter;
    }

    @NonNull
    @Override
    public InfoKortHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_kort_layout, parent, false);
        return new InfoKortHolder(itemView,this);
    }

    class InfoKortHolder extends RecyclerView.ViewHolder {
        //Klasse som setter data inn i recyclerview elementene, her da ett og ett infokort
        CardView kontainer;
        private final TextView tilsynId;
        private final TextView stedNavn;
        //private final TextView stedorgNr;
        private final TextView stedDato;
        private final TextView stedAdresse;
        private final TextView stedPostkode;
        private final TextView stedPoststed;
        private final RelativeLayout bakgrunn;
        private final ImageView stedKarakter;
        final InfoListeAdapter adapter;

        //Metode som henter id'er til elementer
        private InfoKortHolder(View itemView, InfoListeAdapter adapter){
            super(itemView);
            kontainer = itemView.findViewById(R.id.info_kort);
            this.tilsynId = itemView.findViewById(R.id.sted_tilsynsid);
            this.stedNavn = itemView.findViewById(R.id.info_kort_tittel);
            //this.stedorgNr = itemView.findViewById(R.id.info_kort_orgnr);
            this.stedDato = itemView.findViewById(R.id.info_kort_dato);
            this.stedAdresse = itemView.findViewById(R.id.info_kort_adr);
            this.stedPostkode = itemView.findViewById(R.id.info_kort_postkode);
            this.stedPoststed = itemView.findViewById(R.id.info_kort_poststed);
            this.bakgrunn = itemView.findViewById(R.id.info_kort_gradient);
            this.stedKarakter = itemView.findViewById(R.id.info_kort_karakter);
            this.adapter = adapter;
        }

        //Setter ClickListener pr kort
        private void bind(final InfoKort card, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(card);
                }
            });
        }

    }

    //Metode som setter data inn i elementer med get metoder satt i Infokort klassen
    @Override
    public void onBindViewHolder(@NonNull InfoKortHolder holder, int position) {
        holder.tilsynId.setText(infoListe.get(position).getTilsynId());
        holder.stedNavn.setText(infoListe.get(position).getStedNavn());
        //holder.stedorgNr.setText(infoListe.get(position).getStedOrgNr());
        holder.stedDato.setText(infoListe.get(position).getStedDato());
        holder.stedAdresse.setText(infoListe.get(position).getStedAdresse());
        holder.stedPostkode.setText(infoListe.get(position).getStedPostkode());
        holder.stedPoststed.setText(infoListe.get(position).getStedPoststed());
        //holder.bakgrunn.setBackgroundResource(infoListe.get(position).getGradient());
        holder.stedKarakter.setImageResource(infoListe.get(position).getStedKarakterBilde());
        //Setter ClickListener
        holder.bind(infoListe.get(position), lytter);
        //Setter animasjons egenskap
        listeScrollAnimasjon(holder.itemView, position);
    }

    //Animasjon som fader inn infokort når man scroller
    private void listeScrollAnimasjon(View infoKort, int posisjon)
    {
        if (posisjon > forrigePosisjon || posisjon < forrigePosisjon)
        {
            Animation animasjon = AnimationUtils.loadAnimation(context, R.anim.nav_default_enter_anim);
            infoKort.startAnimation(animasjon);
            forrigePosisjon = posisjon;
        }
    }

    @Override
    public int getItemCount() {
        return infoListe.size();
    }

    //Metode som gjenoppretter infokort når bruker angrer sletting
    void restoreInfoKort(InfoKort kort, int position) {
        infoListe.add(position, kort);
        notifyItemInserted(position);
    }

    //Metode som returnerer en instans av et infokort
    ArrayList<InfoKort> getInfoKort() {
        return infoListe;
    }

}