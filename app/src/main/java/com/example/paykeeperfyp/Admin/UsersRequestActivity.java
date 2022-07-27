package com.example.paykeeperfyp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.CommonCredential.SettingActivity;
import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.User.FeaturedAdapter;
import com.example.paykeeperfyp.User.ProductAdapterUser;
import com.example.paykeeperfyp.User.RequestAdminActivity;
import com.example.paykeeperfyp.User.UserDashboardActivity;
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

public class UsersRequestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    RecyclerView recyclerView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    List<RequestModel> modelList;
    RequestAdapter requestAdapter;
    DatabaseReference RequestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_users_request);
        initWidget();
        //region Navigation Bar Setting
        /*ToolBar*/
        setSupportActionBar(toolbar);
        /*Navigation Drawer*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*Navigation View*/
        navigationView.setNavigationItemSelectedListener(this);
        //To Make highlight the current activity
        navigationView.setCheckedItem(R.id.nav_rate);
        //Hide or Show Item

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_add_product).setVisible(false);
        menu.findItem(R.id.nav_share).setVisible(false);
        //-----------
        //--Filling Navigation Drawer Data --//
        View headerView = navigationView.getHeaderView(0);
        TextView nameText = headerView.findViewById(R.id.user_profile_name);
        TextView userNameText = headerView.findViewById(R.id.user_profile_username);
        CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);

        //Error in prevalent so will be continue from here

        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.dummy_user).into(profileImageView);
        nameText.setText(Prevalent.currentOnlineUser.getFullName());
        userNameText.setText(Prevalent.currentOnlineUser.getEmail());

        modelList = new ArrayList<>();
        setupRecycler();
        Query query = RequestRef.orderByChild("req_id");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    RequestModel model = data.getValue(RequestModel.class);
                    modelList.add(model);
                }
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void setupRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        requestAdapter = new RequestAdapter(modelList);
        recyclerView.setAdapter(requestAdapter);
        requestAdapter.notifyDataSetChanged();
    }

    private void initWidget() {
        String business;
        if(Paper.book().exist("UserBusiness")){
            business = Paper.book().read("UserBusiness");
        }else{
            business = Prevalent.UserChosenBusiness;
        }
        RequestRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child("Requests");
        drawerLayout = findViewById(R.id.drawer_layout_user_requests);
        navigationView = findViewById(R.id.nav_view_user_requests);
        toolbar = findViewById(R.id.toolbar_user_dash_requests);
        recyclerView = findViewById(R.id.users_request_rv);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(UsersRequestActivity.this, UserDashboardActivity.class));
                        finish();
                        break;

                    case R.id.nav_profile:
                        Intent intentx = new Intent(UsersRequestActivity.this, SettingActivity.class);
                        startActivity(intentx);
                        finish();
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();
                        Intent intent = new Intent(UsersRequestActivity.this, introductoryActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_share:
                        Toast.makeText(getApplicationContext(), "Sharing is caring", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_rate:
                        Toast.makeText(getApplicationContext(), "Rate Us", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }, 250);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

}