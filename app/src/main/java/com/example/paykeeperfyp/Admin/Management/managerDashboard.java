package com.example.paykeeperfyp.Admin.Management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.AddNewProductActivity;
import com.example.paykeeperfyp.Admin.AdminDashboardActivity;
import com.example.paykeeperfyp.Admin.Management.Database.DatabaseHelper;
import com.example.paykeeperfyp.BuildConfig;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormatSymbols;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class managerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    DrawerLayout drawerLayout;
    NavigationView side_navigation;
    Toolbar toolbar;
    ImageView customer_image;
    TextView status_bar_text, customer_business_name, customer_name, transaction_date;
    String user_id, user_business_name, user_name;
    String phone_number, database_passcode, database_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_manager_dashboard);
        Paper.init(this);
        if (Paper.book().exist("UserPhone")) {
            phone_number = Paper.book().read("UserPhone");
        } else {
            phone_number = Prevalent.currentOnlineUser.getPhoneNo();
            Paper.book().write("UserPhone",phone_number);
        }
        File f = new File("/data/data/" + getPackageName() + "/shared_prefs/"+phone_number+".xml");

        String DB_name = phone_number;

        DatabaseHelper myDB = new DatabaseHelper(managerDashboard.this);

        Cursor cursor = myDB.check_usernumber_exist(phone_number, 1);
        if (cursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "No such user exist", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), managerRegister.class);
            startActivity(intent);
            finish();

        } else {
            while (cursor.moveToNext()) {
                database_id = cursor.getString(0);
                database_passcode = cursor.getString(3);
            }
            SharedPreferences SharedPreferences = getSharedPreferences(DB_name, MODE_PRIVATE);
            SharedPreferences.Editor myEdit = SharedPreferences.edit();
            myEdit.putBoolean("is_logged_in", true);
            myEdit.putString("Id", database_id);
            myEdit.putString("user_phone_number", phone_number);
            myEdit.putString("user_passcode", database_passcode);
            myEdit.commit();
        }



        /* Status Bar */
        status_bar_text = findViewById(R.id.status_bar_text);
        //Saving User UD
        /* Side Navigation */
        drawerLayout = findViewById(R.id.side_drawer);
        side_navigation = findViewById(R.id.side_navigation);
        toolbar = findViewById(R.id.toolbar_nav_manager);
        //Toggle Sync
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*Navigation Drawer*/
        side_navigation.bringToFront();
        /*Navigation View*/
        side_navigation.setNavigationItemSelectedListener(this);
        //To Make highlight the current activity
        side_navigation.setCheckedItem(R.id.nav_add_product);
        //Hide or Show Item
        Menu menu = side_navigation.getMenu();
        menu.findItem(R.id.nav_add_product).setVisible(true);
        //--Filling Navigation Drawer Data --//
        View headerView = side_navigation.getHeaderView(0);
        TextView nameText = headerView.findViewById(R.id.user_profile_name);
        TextView userNameText = headerView.findViewById(R.id.user_profile_username);
        CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.dummy_user).into(profileImageView);
        nameText.setText(Prevalent.currentOnlineUser.getFullName());
        userNameText.setText(Prevalent.currentOnlineUser.getEmail());

        SettingUpVariables();

        /* Bottom Navigation Fragment */

        final BottomNavigationView bottom_nav = findViewById(R.id.bottom_navigation);
        bottom_nav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new fragment_home_dashboard()).commit();


    }

    private void SettingUpVariables() {
        user_id = Prevalent.currentOnlineUser.getEmail();
        user_name = Prevalent.currentOnlineUser.getFullName();
        user_business_name = Prevalent.UserChosenBusiness;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(managerDashboard.this, AdminDashboardActivity.class));
                finish();
                break;
            case R.id.nav_add_product:
                startActivity(new Intent(managerDashboard.this, AddNewProductActivity.class));
                break;
            case R.id.nav_orders:
                Toast.makeText(getApplicationContext(), "Pending Orders", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Paper.book().destroy();
                Intent intent = new Intent(managerDashboard.this, introductoryActivity.class);
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /* Bottom Navigation Fragment */

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home_manager:
                    selectedFragment = new fragment_home_dashboard();
                    status_bar_text.setText(R.string.homedashboardstatusbar);
                    break;
                case R.id.nav_user_manager:
                    selectedFragment = new fragment_customer_dashboard();
                    status_bar_text.setText(R.string.selectcustomerdashboardstatusbar);
                    break;
                case R.id.nav_book_manager:
                    selectedFragment = new fragment_transaction_dashboard();
                    status_bar_text.setText(R.string.transactionlistdashboardstatusbar);
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            EditText user_location = findViewById(R.id.user_location);
            user_location.setText(place.getName());
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String month_name = new DateFormatSymbols().getMonths()[month];
        String temp_date = String.valueOf(day) + " - " + month_name.substring(0, 3) + " - " + String.valueOf(year);
        transaction_date.setText(temp_date);

    }

    public void transaction_chat(View view) {
        MaterialCardView card = (MaterialCardView) view;
        String a = card.getTag().toString();
        Intent intent = new Intent(getApplicationContext(), transaction_chat.class);
        intent.putExtra("Friend_id", a);
        startActivity(intent);
    }

    public void transaction_summary(View view) {
        String transaction_id, transaction_sender_id = null, transaction_receiver_id = null, transaction_amount_text = null, transaction_remarks_text = null, transaction_date_text = null, sender_id, customer_phone_number_alert_text = null;
        Bitmap customer_image_alert_text = null;
        final ImageView close_alert, customer_image_alert, share_icon;
        TextView transaction_amount, transaction_remarks, transaction_time, customer_phone_number_alert,transactionamountsymbol;
        final MaterialCardView alert_dialog;
        final LinearLayout share_layout;

        MaterialCardView card = (MaterialCardView) view;
        transaction_id = card.getTag().toString();
        DatabaseHelper myDB = new DatabaseHelper(this);
        Cursor cursor = myDB.get_transaction_details(transaction_id);

        while (cursor.moveToNext()) {
            transaction_sender_id = cursor.getString(1);
            transaction_receiver_id = cursor.getString(2);
            transaction_amount_text = cursor.getString(3);
            transaction_remarks_text = cursor.getString(5);
            transaction_date_text = cursor.getString(7);
        }

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_transaction_alert);

        alert_dialog = dialog.findViewById(R.id.alert_dialog);
        close_alert = dialog.findViewById(R.id.close_alert);
        customer_image_alert = dialog.findViewById(R.id.customer_image);
        customer_phone_number_alert = dialog.findViewById(R.id.customer_contact_number);
        transactionamountsymbol = dialog.findViewById(R.id.transactionamountsymbol);
        transaction_amount = dialog.findViewById(R.id.transaction_amount);
        transaction_remarks = dialog.findViewById(R.id.transaction_remarks);
        transaction_time = dialog.findViewById(R.id.transaction_time);
        share_icon = dialog.findViewById(R.id.share_icon);
        share_layout = dialog.findViewById(R.id.share_layout);

        if (transaction_sender_id.compareTo(user_id) == 0) {
            sender_id = transaction_receiver_id;
            transaction_amount.setText("- " + transaction_amount_text);
            transaction_amount.setTextColor(getApplicationContext().getResources().getColor(R.color.warning));
            transactionamountsymbol.setText("Rs");
            close_alert.setImageResource(R.drawable.alertbox_cross_icon_debit);
        } else {
            sender_id = transaction_sender_id;
            transaction_amount.setText("+ " + transaction_amount_text);
            transaction_amount.setTextColor(getApplicationContext().getResources().getColor(R.color.sucess));
            transactionamountsymbol.setText("Rs");
            close_alert.setImageResource(R.drawable.alertbox_cross_icon_credit);
            share_icon.setImageResource(R.drawable.credit_share_icon);
        }

        Cursor cursor1 = myDB.get_user_details(sender_id);

        while (cursor1.moveToNext()) {
            customer_phone_number_alert_text = "+92-" + cursor1.getString(0).substring(2);
            customer_image_alert_text = BitmapFactory.decodeByteArray(cursor1.getBlob(4), 0, cursor1.getBlob(4).length);
        }

        transaction_remarks.setText(transaction_remarks_text);
        transaction_time.setText(transaction_date_text);
        customer_phone_number_alert.setText(customer_phone_number_alert_text);
        customer_image_alert.setImageBitmap(customer_image_alert_text);

        close_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                share_layout.setVisibility(View.INVISIBLE);
                close_alert.setVisibility(View.INVISIBLE);
                Bitmap bitmap = Bitmap.createBitmap(alert_dialog.getWidth(), alert_dialog.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                alert_dialog.draw(canvas);

                try {
                    File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + user_name + "_" + user_business_name + ".jpg");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/jpg");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}