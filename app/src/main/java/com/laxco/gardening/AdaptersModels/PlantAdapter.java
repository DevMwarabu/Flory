package com.laxco.gardening.AdaptersModels;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.laxco.gardening.Description;
import com.laxco.gardening.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This project file is owned by DevMwarabu, johnmwarabuchone@gmail.com.
 * Created on 7/1/21. Copyright (c) 2021 DevMwarabu
 */
public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> implements Filterable {
    private List<Plant> plants;
    private List<Plant> plantsFiltered;
    private Context context;

    public PlantAdapter(List<Plant> plants, Context context) {
        this.plants = plants;
        this.plantsFiltered = plants;
        this.context = context;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    plantsFiltered = plants;
                } else {
                    List<Plant> filteredList = new ArrayList<>();
                    for (Plant row : plants) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getStrName().toLowerCase().contains(charString.toLowerCase()) || row.getStrName().contains(charSequence)
                                || row.getStrDesc().contains(charSequence) || row.getStrCatId().contains(charSequence)
                                || row.getStrHeight().contains(charSequence) || row.getStrOrigin().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    plantsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = plantsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                plantsFiltered = (ArrayList<Plant>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.settingData(plantsFiltered.get(position).getStrName(),plantsFiltered.get(position).getStrOrigin(),plantsFiltered.get(position).getStrImageUrl());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Description.class);
                intent.putExtra("strName",plantsFiltered.get(position).getStrName());
                intent.putExtra("strDesc",plantsFiltered.get(position).getStrDesc());
                intent.putExtra("strOrigin",plantsFiltered.get(position).getStrOrigin());
                intent.putExtra("strHeight",plantsFiltered.get(position).getStrHeight());
                intent.putExtra("strImageUrl",plantsFiltered.get(position).getStrImageUrl());
                intent.putExtra("strCatId",plantsFiltered.get(position).getStrCatId());
                intent.putExtra("strId",plantsFiltered.get(position).getStrId());


                Pair<View, String> p1 = Pair.create((View) holder.mImageView, "image");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, p1);
                v.getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantsFiltered.size();
    }

    public void clear() {
        plantsFiltered.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Plant> plants) {
        plants.addAll(plants);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName,mHeight;
        private CardView mCardView;
        private ImageView mImageView;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.tv_name);
            mHeight = itemView.findViewById(R.id.tv_height);
            mCardView = itemView.findViewById(R.id.card_main);
            mImageView = itemView.findViewById(R.id.image_main);
        }

        private void settingData(String strName,String strHeight,String strImageUrl){
            mName.setText(strName);
            mHeight.setText(strHeight);
            //setting image
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(strImageUrl)
                    .placeholder(R.drawable.ic_leaf)
                    .error(R.drawable.ic_leaf)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mImageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        }
    }
}
