package com.example.paykeeperfyp.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {

    Context context;
    Boolean flagAdmin;
    List<ProductsHelperClass> model;

    public FeaturedAdapter(Context context, List<ProductsHelperClass> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public FeaturedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_design_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedAdapter.ViewHolder holder, int position) {
        holder.txtProductName.setText(model.get(position).getPname());
        holder.txtProductDescription.setText(model.get(position).getDescription());
        Picasso.get().load(model.get(position).getImage()).into(holder.imageViewProducts);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductDescription;
        ImageView imageViewProducts;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProducts = itemView.findViewById(R.id.featured_image);
            txtProductName = itemView.findViewById(R.id.feature_title);
            txtProductDescription = itemView.findViewById(R.id.featured_description);

        }
    }
}
