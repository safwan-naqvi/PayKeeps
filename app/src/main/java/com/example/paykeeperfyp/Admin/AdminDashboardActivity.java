package com.example.paykeeperfyp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.Management.managerDashboard;
import com.example.paykeeperfyp.CommonCredential.SettingActivity;
import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.Interface.ItemClickListener;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.User.FeaturedAdapter;
import com.example.paykeeperfyp.User.ProductsAdapter;
import com.example.paykeeperfyp.User.UserDashboardActivity;
import com.example.paykeeperfyp.ViewHolder.FeaturedViewHolder;
import com.example.paykeeperfyp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView featuredRecycler, products;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private DatabaseReference ProductRef;
    TextView noProductAvailable;
    ScrollView scrollView;
    List<ProductsHelperClass> featureList, productList;
    FeaturedAdapter adapter;
    ProductsAdapter productAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_dashboard);
        initWidget();
        /*ToolBar*/
        Paper.init(this);


        setSupportActionBar(toolbar);
        /*Navigation Drawer*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*Navigation View*/
        navigationView.setNavigationItemSelectedListener(this);
        //To Make highlight the current activity
        navigationView.setCheckedItem(R.id.nav_home);
        //Hide or Show Item

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_add_product).setVisible(true);
        menu.findItem(R.id.nav_share).setVisible(false);
        menu.findItem(R.id.nav_rate).setVisible(true);

        //--Filling Navigation Drawer Data --//
        View headerView = navigationView.getHeaderView(0);
        TextView nameText = headerView.findViewById(R.id.user_profile_name);
        TextView userNameText = headerView.findViewById(R.id.user_profile_username);
        CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);

        //Error in prevalent so will be continue from here
        if (Prevalent.currentOnlineUser.getImage() != null) {
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.dummy_user).into(profileImageView);
        } else {
            Picasso.get().load(R.drawable.dummy_user).into(profileImageView);
        }
        nameText.setText(Prevalent.currentOnlineUser.getFullName());
        userNameText.setText(Prevalent.currentOnlineUser.getEmail());

        //Calling Recyclers

    }

    String business;

    private void initWidget() {

        if (Paper.book().exist("UserBusiness")) {
            business = Paper.book().read("UserBusiness");
        } else {
            business = Prevalent.UserChosenBusiness;
        }

        ProductRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child("Products");
        //Hooks
        featuredRecycler = findViewById(R.id.featured_recycler_view);
        products = findViewById(R.id.mostViewed_recycler);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollView_admin);
        noProductAvailable = findViewById(R.id.no_product_tv2);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class));
                        finish();
                        break;
                    case R.id.nav_add_product:
                        startActivity(new Intent(AdminDashboardActivity.this, AddNewProductActivity.class));
                        finish();
                        break;
                    case R.id.nav_orders:
                        startActivity(new Intent(AdminDashboardActivity.this, managerDashboard.class));
                        // Toast.makeText(getApplicationContext(), "Pending Orders", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_profile:
                        Intent intentx = new Intent(AdminDashboardActivity.this, SettingActivity.class);
                        startActivity(intentx);
                        finish();
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();
                        Intent intent = new Intent(AdminDashboardActivity.this, introductoryActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_share:
                        Toast.makeText(getApplicationContext(), "Sharing is caring", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_rate:
                        Intent intenx = new Intent(AdminDashboardActivity.this, UsersRequestActivity.class);
                        startActivity(intenx);
                        finish();
                        break;

                }
            }
        }, 250);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void productsRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        //products.setHasFixedSize(true);
        products.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductsAdapter(this, this, productList, business);
        products.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    private void featuredRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        featuredRecycler.setLayoutManager(linearLayoutManager);
        featuredRecycler.setHasFixedSize(false);
        adapter = new FeaturedAdapter(this, featureList);
        featuredRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    public void retrieveDATABASE() {

        ProductRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    fetchProductFromDatabase();
                } else {
                    scrollView.setVisibility(View.GONE);
                    noProductAvailable.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed, how to handle?

            }

        });

    }

    private void fetchProductFromDatabase() {
        //region Featured List
        featureList = new ArrayList<>();
        featuredRecycler();
        Query query = ProductRef.orderByChild("featured").equalTo("true");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ProductsHelperClass model = data.getValue(ProductsHelperClass.class);
                    featureList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("TAG", "Failed to read value.", error.toException());
            }
        });
        //endregion

        //region productList
        productList = new ArrayList<>();
        productsRecycler();
        Query queryProducts = ProductRef.orderByChild("pid");
        queryProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ProductsHelperClass model = data.getValue(ProductsHelperClass.class);
                    productList.add(model);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("TAG", "Failed to read value.", error.toException());
            }
        });
        //endregion

    }


    @Override
    protected void onStart() {
        super.onStart();
        retrieveDATABASE();
    }


}