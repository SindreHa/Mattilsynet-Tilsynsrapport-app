package com.example.mattilsynet.SearchResult;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mattilsynet.R;

import java.util.ArrayList;

public class InfoListAdapter extends RecyclerView.Adapter<com.example.mattilsynet.SearchResult.InfoListAdapter.InfoCardHolder>{

    public interface OnItemClickListener {
        void onItemClick(InfoCard card);
    }

    private final ArrayList<InfoCard> infoList;
    private final OnItemClickListener listener;
    private LayoutInflater inflater;
    private int lastPosition = -1;
    private Context context;
    private final String LOG_TAG = InfoListAdapter.class.getSimpleName();

    public InfoListAdapter(Context context, ArrayList<InfoCard> infoList, OnItemClickListener listener){
        inflater = LayoutInflater.from(context);
        this.infoList = infoList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InfoCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_card_layout, parent, false);
        return new InfoListAdapter.InfoCardHolder(itemView,this);
    }

    class InfoCardHolder extends RecyclerView.ViewHolder {
        //public final ImageView sportImg;
        CardView container;
        private final TextView placeTitle;
        private final TextView placeOrgNum;
        private final TextView placeAddress;
        private final TextView placeZipCode;
        private final TextView placeZipName;
        //private final ImageView placeGrade;
        final InfoListAdapter adapter;

        private InfoCardHolder(View itemView, InfoListAdapter adapter){
            super(itemView);
            //this.placeTitle = itemView.findViewById(R.id.card_post_title);
            container = itemView.findViewById(R.id.info_card);
            this.placeTitle = itemView.findViewById(R.id.info_card_title);
            this.placeOrgNum = itemView.findViewById(R.id.info_card_orgnr);
            this.placeAddress = itemView.findViewById(R.id.info_card_adr);
            this.placeZipCode = itemView.findViewById(R.id.info_card_zip);
            this.placeZipName = itemView.findViewById(R.id.info_card_zipname);
            //this.placeGrade = itemView.findViewById(R.id.info_card_grade);
            this.adapter = adapter;
            //itemView.setOnClickListener(Snackbar.make(itemView, "Dette skal føre til kommentarer", Snackbar.LENGTH_LONG).show());
        }

        public void bind(final InfoCard card, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(card);
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(@NonNull InfoListAdapter.InfoCardHolder holder, int position) {
        holder.placeTitle.setText(infoList.get(position).getPlaceTitle());
        holder.placeOrgNum.setText(infoList.get(position).getPlaceOrgNum());
        holder.placeAddress.setText(infoList.get(position).getPlaceAddress());
        holder.placeZipCode.setText(infoList.get(position).getPlaceZipCode());
        holder.placeZipName.setText(infoList.get(position).getPlaceZipName());
        holder.bind(infoList.get(position), listener);
        //holder.placeGrade.setImageResource(infoList.get(position).getPlaceGrade());
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

}