package com.example.paykeeperfyp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.ViewHolder.ProductViewHolder;
import com.example.paykeeperfyp.ViewHolder.RelatedViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class ProductDetailsActivity extends AppCompatActivity {


    ImageView product_image;
    TextView product_name, product_price, product_qty, product_category, product_description;
    RecyclerView relatedRecycler;
    private DatabaseReference ProductRef;
    String product_id;
    String business;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product_details);

        product_id = getIntent().getStringExtra("productID");
        initWidget();

        getProductDetail(product_id);
        productsRecycler();
    }

    private void getProductDetail(String product_id) {
        ProductRef.child(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ProductsHelperClass productsHelperClass = snapshot.getValue(ProductsHelperClass.class);
                    product_name.setText(productsHelperClass.getPname());
                    product_price.setText("RS " + productsHelperClass.getPrice() + "/-");
                    product_qty.setText("Qty: " + productsHelperClass.getQty());
                    product_category.setText("Category: " + productsHelperClass.getCategory());
                    product_description.setText(productsHelperClass.getDescription());
                    Picasso.get().load(productsHelperClass.getImage()).into(product_image);

                    category = productsHelperClass.getCategory();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveDATABASE();
    }

    public void retrieveDATABASE() {

        ProductRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fetchProductFromDatabase();
                } else {
                    relatedRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed, how to handle?

            }

        });
    }

    private void fetchProductFromDatabase() {
        Query query = ProductRef.orderByChild("category").limitToLast(6).equalTo(category);
        FirebaseRecyclerOptions<ProductsHelperClass> options = new FirebaseRecyclerOptions.Builder<ProductsHelperClass>()
                .setQuery(query, ProductsHelperClass.class).build();
        //Products Display

        FirebaseRecyclerAdapter<ProductsHelperClass, RelatedViewHolder> adapterProduct = new FirebaseRecyclerAdapter<ProductsHelperClass, RelatedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RelatedViewHolder holder, int position, @NonNull ProductsHelperClass model) {

                holder.txtProductName.setText(model.getPname());
                holder.txtProductPrice.setText("Rs " + model.getPrice() + " /-");
                Picasso.get().load(model.getImage()).into(holder.imageViewProducts);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getApplicationContext(), model.getPid(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("productID", model.getPid());
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @NonNull
            @Override
            public RelatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_product_list, parent, false);
                RelatedViewHolder holder = new RelatedViewHolder(view);
                return holder;
            }
        };


        relatedRecycler.setAdapter(adapterProduct);
        adapterProduct.startListening();
    }

    private void productsRecycler() {
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //products.setHasFixedSize(true);
        relatedRecycler.setLayoutManager(gridLayoutManager);
    }

    private void initWidget() {
        if (Paper.book().exist("UserBusiness")) {
            business = Paper.book().read("UserBusiness");
        } else {
            business = Prevalent.UserChosenBusiness;
        }
        ProductRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child("Products");
        product_image = findViewById(R.id.product_detail_image);
        product_name = findViewById(R.id.product_detail_name);
        product_price = findViewById(R.id.product_detail_price);
        product_qty = findViewById(R.id.product_detail_qty);
        product_category = findViewById(R.id.product_detail_category);
        product_description = findViewById(R.id.product_detail_desc);
        relatedRecycler = findViewById(R.id.related_recycler);
    }

    public void onBackPressed(View view) {
        startActivity(new Intent(ProductDetailsActivity.this, UserDashboardActivity.class));
        finish();

    }
}