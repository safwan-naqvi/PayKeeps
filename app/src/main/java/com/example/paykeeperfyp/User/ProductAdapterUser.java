package com.example.paykeeperfyp.User;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.R;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapterUser extends RecyclerView.Adapter<ProductAdapterUser.ViewHolder> {
    private Context context;
    private List<ProductsHelperClass> model;

    public ProductAdapterUser(Context context, List<ProductsHelperClass> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
        return new ProductAdapterUser.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtProductName.setText(model.get(position).getPname());
        holder.txtProductDescription.setText(model.get(position).getDescription());
        holder.txtProductCategory.setText(model.get(position).getCategory());
        holder.txtProductPrice.setText("Rs " + model.get(position).getPrice() + " /-");
        Picasso.get().load(model.get(position).getImage()).into(holder.imageViewProducts);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductCategory;
        public ImageView imageViewProducts;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.product_item_name);
            txtProductDescription = itemView.findViewById(R.id.product_item_desc);
            txtProductPrice = itemView.findViewById(R.id.product_item_price);
            txtProductCategory = itemView.findViewById(R.id.product_item_category);
            imageViewProducts = itemView.findViewById(R.id.product_item_image);
        }
    }
}
