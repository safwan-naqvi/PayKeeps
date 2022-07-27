package com.example.paykeeperfyp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.CommonCredential.SettingActivity;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class RequestAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    EditText body;
    Button submit;
    private DatabaseReference ProductRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_request_admin);
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
        navigationView.setCheckedItem(R.id.nav_share);
        //Hide or Show Item

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_add_product).setVisible(false);
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(body.getText().toString())) {

                    String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
                    String currentDate = new SimpleDateFormat("EEE,d MMM , yyyy", Locale.getDefault()).format(new Date());
                    String rid = currentDate + " " + currentTime;
                    Map<String, Object> hopperUpdates = new HashMap<>();
                    hopperUpdates.put("message", body.getText().toString());
                    hopperUpdates.put("req_id", rid);
                    hopperUpdates.put("req_by", Prevalent.currentOnlineUser.getEmail());
                    //reference.updateChildren(hopperUpdates);
                    ProductRef.child(rid).setValue(hopperUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            body.setText("");
                        }
                    });


                }
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
        ProductRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child("Requests");

        drawerLayout = findViewById(R.id.drawer_layout_user_req);
        navigationView = findViewById(R.id.nav_view_user_req);
        toolbar = findViewById(R.id.toolbar_user_dash_req);
        body = findViewById(R.id.request_body);
        submit = findViewById(R.id.submit_req);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(RequestAdminActivity.this, UserDashboardActivity.class));
                        finish();
                        break;
                    case R.id.nav_orders:
                        Intent intent2x = new Intent(RequestAdminActivity.this, UserKhataActivity.class);
                        startActivity(intent2x);
                        finish();
                        break;
                    case R.id.nav_profile:
                        Intent intentx = new Intent(RequestAdminActivity.this, SettingActivity.class);
                        startActivity(intentx);
                        finish();
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                        Paper.book().destroy();
                        Intent intent = new Intent(RequestAdminActivity.this, introductoryActivity.class);
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