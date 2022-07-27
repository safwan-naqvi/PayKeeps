package com.example.paykeeperfyp.Admin.Management.Database;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.paykeeperfyp.R;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    Context context;
    List<String> friend_id, transaction_name, transaction_phone_number, image_2;
    List<Integer> transaction_amount;
    List<Bitmap> transaction_image;
    int lastPosition = -1;

    public ContactAdapter(Context context, List<String> friend_id, List<String> transaction_name, List<String> transaction_phone_number, List<Integer> transaction_amount, List<Bitmap> transaction_image, List<String> image_2) {
        this.context = context;
        this.friend_id = friend_id;
        this.transaction_name = transaction_name;
        this.transaction_phone_number = transaction_phone_number;
        this.image_2 = image_2;
        this.transaction_amount = transaction_amount;
        this.transaction_image = transaction_image;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.friend_id_text.setTag(String.valueOf(friend_id.get(position)));
        holder.transaction_name_text.setText(String.valueOf(transaction_name.get(position)));
        Log.i("answer","size at adapter "+image_2.size());
        if (image_2.size()>=1) {
            if(!String.valueOf(image_2.get(position)).equals("none")){
                Glide
                        .with(context)
                        .load(String.valueOf(image_2.get(position)))
                        .centerCrop()
                        .placeholder(R.drawable.user)
                        .into(holder.customer_image);
            }
        }else{
            holder.customer_image.setImageBitmap((Bitmap) transaction_image.get(position));
        }


        holder.transaction_phone_number_text.setText("+92-" + String.valueOf(transaction_phone_number.get(position)).substring(2));
        if ((Integer) transaction_amount.get(position) >= 0) {
            holder.transaction_amount_text.setText("+ " + String.valueOf(transaction_amount.get(position)));
            holder.transaction_amount_text.setTextColor(context.getResources().getColor(R.color.sucess));
            holder.transactionamountsymbol.setText("Rs");
        } else {
            holder.transaction_amount_text.setText("- " + String.valueOf(Math.abs((Integer) transaction_amount.get(position))));
            holder.transaction_amount_text.setTextColor(context.getResources().getColor(R.color.warning));
            holder.transactionamountsymbol.setText("Rs");
        }
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return transaction_name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView transaction_name_text, transaction_phone_number_text, transaction_amount_text, transactionamountsymbol;
        MaterialCardView friend_id_text;
        ImageView customer_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            friend_id_text = itemView.findViewById(R.id.transction);
            transaction_name_text = itemView.findViewById(R.id.customername);
            transaction_phone_number_text = itemView.findViewById(R.id.customerphonenumber);
            transaction_amount_text = itemView.findViewById(R.id.transactionamount);
            transactionamountsymbol = itemView.findViewById(R.id.transactionamountsymbol);
            customer_image = itemView.findViewById(R.id.customer_image);
        }

    }

}
