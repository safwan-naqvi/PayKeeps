package com.example.paykeeperfyp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.RequestAdapter;
import com.example.paykeeperfyp.Admin.RequestModel;
import com.example.paykeeperfyp.CommonCredential.SettingActivity;
import com.example.paykeeperfyp.HelperClass.ProductsHelperClass;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
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

public class UserKhataActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView transactionsDebit,transactionsCredit;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private DatabaseReference KhaataRefPaid,KhaataRefReceived;
    TextView noTransactionAvailable;
    List<KhaataModel> modelListPaid;
    List<KhaataModelReceived> modelListReceived;
    KhaataAdapterPaid khaataAdapterPaid;
    KhaataAdapterReceived khaataAdapterReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_khata);
        initWidget();
        //region Navigation Sidebar
        setSupportActionBar(toolbar);
        /*Navigation Drawer*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*Navigation View*/
        navigationView.setNavigationItemSelectedListener(UserKhataActivity.this);
        //To Make highlight the current activity
        navigationView.setCheckedItem(R.id.nav_orders);
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
        //endregion

        modelListPaid = new ArrayList<>();
        modelListReceived = new ArrayList<>();

        setupRecyclerDebit();
        setupRecyclerCredit();

        Query queryDebit = KhaataRefPaid.orderByChild("debit_id");
        queryDebit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    KhaataModel model = data.getValue(KhaataModel.class);
                    modelListPaid.add(model);
                }
                khaataAdapterPaid.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("TAG", "Failed to read value.", error.toException());
            }
        });

        Query queryCredit = KhaataRefReceived.orderByChild("credit_id");
        queryCredit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    KhaataModelReceived model = data.getValue(KhaataModelReceived.class);
                    modelListReceived.add(model);
                }
                khaataAdapterReceived.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("TAG", "Failed to read value.", error.toException());
            }
        });

    }

    private void initWidget() {
        String business;
        if (Paper.book().exist("UserBusiness")) {
            business = Paper.book().read("UserBusiness");
        } else {
            business = Prevalent.UserChosenBusiness;
        }
        String number = Prevalent.currentOnlineUser.getPhoneNo();
        KhaataRefPaid = FirebaseDatabase.getInstance().getReference("Company").child(business).child("User").child(number).child("Debit");
        KhaataRefReceived = FirebaseDatabase.getInstance().getReference("Company").child(business).child("User").child(number).child("Credit");
        transactionsDebit = findViewById(R.id.khaata_recycler_view);
        transactionsCredit = findViewById(R.id.khaata_recycler_view_received);
        noTransactionAvailable = findViewById(R.id.no_khaata_tv_req);
        drawerLayout = findViewById(R.id.drawer_layout_user_transaction);
        navigationView = findViewById(R.id.nav_view_user_transaction);
        toolbar = findViewById(R.id.toolbar_user_dash_transaction);
    }

    private void setupRecyclerDebit() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        transactionsDebit.setLayoutManager(linearLayoutManager);
        transactionsDebit.setHasFixedSize(false);
        khaataAdapterPaid = new KhaataAdapterPaid(modelListPaid);
        transactionsDebit.setAdapter(khaataAdapterPaid);
        khaataAdapterPaid.notifyDataSetChanged();
    }

    private void setupRecyclerCredit() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        transactionsCredit.setLayoutManager(linearLayoutManager);
        transactionsCredit.setHasFixedSize(false);
        khaataAdapterReceived = new KhaataAdapterReceived(modelListReceived);
        transactionsCredit.setAdapter(khaataAdapterReceived);
        khaataAdapterReceived.notifyDataSetChanged();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(UserKhataActivity.this, UserDashboardActivity.class));
                        finish();
                        break;
                    case R.id.nav_profile:
                        Intent intentx2 = new Intent(UserKhataActivity.this, SettingActivity.class);
                        startActivity(intentx2);
                        finish();
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();
                        Intent intenc = new Intent(UserKhataActivity.this, introductoryActivity.class);
                        startActivity(intenc);
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