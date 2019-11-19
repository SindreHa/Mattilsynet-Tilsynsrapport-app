package com.example.mattilsynet.Sokeresultat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mattilsynet.R;

import java.util.ArrayList;

public class InfoListeAdapter extends RecyclerView.Adapter<InfoListeAdapter.InfoCardHolder>{

    public interface OnItemClickListener {
        void onItemClick(InfoKort card);
    }

    private final ArrayList<InfoKort> infoListe;
    private final OnItemClickListener lytter;
    private LayoutInflater inflater;
    private int forrigePosisjon = -1;
    private Context context;
    private final String LOG_TAG = InfoListeAdapter.class.getSimpleName();

    public InfoListeAdapter(Context context, ArrayList<InfoKort> infoListe, OnItemClickListener lytter){
        inflater = LayoutInflater.from(context);
        this.infoListe = infoListe;
        this.context = context;
        this.lytter = lytter;
    }

    @NonNull
    @Override
    public InfoCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_kort_layout, parent, false);
        return new InfoListeAdapter.InfoCardHolder(itemView,this);
    }

    class InfoCardHolder extends RecyclerView.ViewHolder {
        //public final ImageView sportImg;
        CardView kontainer;
        private final TextView stedNavn;
        private final TextView stedorgNr;
        private final TextView stedDato;
        private final TextView stedAdresse;
        private final TextView stedPostkode;
        private final TextView stedPoststed;
        private final ImageView stedKarakter;
        final InfoListeAdapter adapter;

        private InfoCardHolder(View itemView, InfoListeAdapter adapter){
            super(itemView);
            //this.stedNavn = itemView.findViewById(R.id.card_post_title);
            kontainer = itemView.findViewById(R.id.info_kort);
            this.stedNavn = itemView.findViewById(R.id.info_kort_tittel);
            this.stedorgNr = itemView.findViewById(R.id.info_kort_orgnr);
            this.stedDato = itemView.findViewById(R.id.info_kort_dato);
            this.stedAdresse = itemView.findViewById(R.id.info_kort_adr);
            this.stedPostkode = itemView.findViewById(R.id.info_kort_postkode);
            this.stedPoststed = itemView.findViewById(R.id.info_kort_poststed);
            this.stedKarakter = itemView.findViewById(R.id.info_kort_karakter);
            this.adapter = adapter;
            //itemView.setOnClickListener(Snackbar.make(itemView, "Dette skal føre til kommentarer", Snackbar.LENGTH_LONG).show());
        }

        public void bind(final InfoKort card, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(card);
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(@NonNull InfoListeAdapter.InfoCardHolder holder, int position) {
        holder.stedNavn.setText(infoListe.get(position).getStedNavn());
        holder.stedorgNr.setText(infoListe.get(position).getStedOrgNr());
        holder.stedDato.setText(infoListe.get(position).getStedDato());
        holder.stedAdresse.setText(infoListe.get(position).getStedAdresse());
        holder.stedPostkode.setText(infoListe.get(position).getStedPostkode());
        holder.stedPoststed.setText(infoListe.get(position).getStedPoststed());
        holder.stedKarakter.setImageResource(infoListe.get(position).getStedKarakter());
        holder.bind(infoListe.get(position), lytter);
        setAnimation(holder.itemView, position);
        //holder.stedKarakter.setImageResource(infoListe.get(position).getStedKarakter());
    }

    //Animasjon som fader inn infokort når man scroller
    private void setAnimation(View viewAAnimere, int posisjon)
    {
        if (posisjon > forrigePosisjon || posisjon < forrigePosisjon)
        {
            Animation animasjon = AnimationUtils.loadAnimation(context, R.anim.nav_default_enter_anim);
            viewAAnimere.startAnimation(animasjon);
            forrigePosisjon = posisjon;
        }
    }

    @Override
    public int getItemCount() {
        return infoListe.size();
    }

}