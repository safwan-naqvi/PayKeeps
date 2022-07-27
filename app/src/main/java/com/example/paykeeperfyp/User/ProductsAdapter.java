package com.example.paykeeperfyp.User;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.Admin.AdminDashboardActivity;
import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<ProductsHelperClass> model;
    private String business;

    public ProductsAdapter(Context context, Activity activity,List<ProductsHelperClass> model, String business) {
        this.context = context;
        this.model = model;
        this.activity = activity;
        this.business = business;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtProductName.setText(model.get(position).getPname());
        holder.txtProductDescription.setText(model.get(position).getDescription());
        holder.txtProductCategory.setText(model.get(position).getCategory());
        holder.txtProductPrice.setText("Rs " + model.get(position).getPrice() + " /-");
        Picasso.get().load(model.get(position).getImage()).into(holder.imageViewProducts);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog alertDialog = holder.AskOption(model.get(position).getPid());
                alertDialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        public AlertDialog AskOption(String pid) {
            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                    // set message, title, and icon
                    .setTitle("Delete")
                    .setMessage("Do you want to Delete")
                    .setIcon(R.drawable.ic_delete)

                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //your deleting code
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Company").child(business).child("Products").child(pid);
                            db.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, pid + " Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                    activity.startActivity(new Intent(context,AdminDashboardActivity.class));
                                    activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                    activity.finish();
                                }
                            });
                            dialog.dismiss();
                        }

                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    })
                    .create();

            return myQuittingDialogBox;
        }
    }
}
