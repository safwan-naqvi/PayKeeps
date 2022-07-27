package com.example.paykeeperfyp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.Interface.ItemClickListener;
import com.example.paykeeperfyp.R;

public class FeaturedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public ItemClickListener listener;

    public FeaturedViewHolder(View itemView){
        super(itemView);

    }


    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }
}
