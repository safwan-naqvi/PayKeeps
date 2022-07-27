package com.example.paykeeperfyp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.Interface.ItemClickListener;
import com.example.paykeeperfyp.R;

public class RelatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductPrice;
    public ImageView imageViewProducts;
    public ItemClickListener listener;



    public RelatedViewHolder(@NonNull View itemView) {
        super(itemView);
        //Hooking Design with their id's
        txtProductName = itemView.findViewById(R.id.related_product_item_name);
        txtProductPrice = itemView.findViewById(R.id.related_product_item_price);
        imageViewProducts = itemView.findViewById(R.id.related_product_item_image);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }
}
