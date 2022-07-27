package com.example.paykeeperfyp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.Interface.ItemClickListener;
import com.example.paykeeperfyp.R;


public class ProductViewHolder extends RecyclerView.ViewHolder {

    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductCategory;
    public ImageView imageViewProducts;
    public View listener;

    public ProductViewHolder(View itemView) {
        super(itemView);
        //Hooking Design with their id's
        txtProductName = itemView.findViewById(R.id.product_item_name);
        txtProductDescription = itemView.findViewById(R.id.product_item_desc);
        txtProductPrice = itemView.findViewById(R.id.product_item_price);
        txtProductCategory = itemView.findViewById(R.id.product_item_category);
        imageViewProducts = itemView.findViewById(R.id.product_item_image);
        listener = itemView;

    }

//    @Override
//    public void onClick(View view) {
//        listener.onClick(view, getAdapterPosition(), false);
//    }
//
//    public void setItemClickListener(ItemClickListener listener) {
//        this.listener = listener;
//    }

}
