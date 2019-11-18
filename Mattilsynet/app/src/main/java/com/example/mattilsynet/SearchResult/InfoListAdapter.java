package com.example.mattilsynet.SearchResult;

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

public class InfoListAdapter extends RecyclerView.Adapter<com.example.mattilsynet.SearchResult.InfoListAdapter.InfoCardHolder>{

    private final ArrayList<InfoCard> infoList;
    private LayoutInflater inflater;
    private int lastPosition = -1;
    private Context context;

    class InfoCardHolder extends RecyclerView.ViewHolder {
        //public final ImageView sportImg;
        CardView container;
        public final TextView placeTitle;
        public final TextView placeOrgNum;
        public final TextView placeAddress;
        public final TextView placeZipCode;
        public final TextView placeZipName;
        //public final ImageView placeGrade;
        final com.example.mattilsynet.SearchResult.InfoListAdapter adapter;

        public InfoCardHolder(View itemView, com.example.mattilsynet.SearchResult.InfoListAdapter adapter){
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

    }
    public InfoListAdapter(Context context, ArrayList<InfoCard> infoList){
        inflater = LayoutInflater.from(context);
        this.infoList = infoList;
        this.context = context;
    }

    @NonNull
    @Override
    public com.example.mattilsynet.SearchResult.InfoListAdapter.InfoCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.info_card_layout, parent, false);
        return new com.example.mattilsynet.SearchResult.InfoListAdapter.InfoCardHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.mattilsynet.SearchResult.InfoListAdapter.InfoCardHolder holder, int position) {
        //holder.sportImg.setImageResource(postCardList.get(position).getImagePath());
        holder.placeTitle.setText(infoList.get(position).getPlaceTitle());
        holder.placeOrgNum.setText(infoList.get(position).getPlaceOrgNum());
        holder.placeAddress.setText(infoList.get(position).getPlaceAddress());
        holder.placeZipCode.setText(infoList.get(position).getPlaceZipCode());
        holder.placeZipName.setText(infoList.get(position).getPlaceZipName());
        //holder.placeGrade.setImageResource(infoList.get(position).getPlaceGrade());
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.nav_default_enter_anim);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

}