package com.example.paykeeperfyp.CommonCredential;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.AddNewProductActivity;
import com.example.paykeeperfyp.Admin.AdminDashboardActivity;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.User.UserDashboardActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView imageView;
    Button update, change_profile;
    TextInputLayout name, password,email;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    //For Image Saving of New DP
    private Uri imageUri;
    private String myURL = "";
    private StorageReference storeProfileImageRef;
    private StorageTask uploadTask;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        //Reference to Database
        storeProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        //Nav Hooks
        drawerLayout = findViewById(R.id.drawer_layout_setting);
        navigationView = findViewById(R.id.nav_view_setting);
        toolbar = findViewById(R.id.toolbar_user_setting);

        //Hooks
        update = findViewById(R.id.update_user_settings);
        change_profile = findViewById(R.id.change_user_profile_image);
        name = findViewById(R.id.et_fullname_setting);
        email = findViewById(R.id.et_email_setting);
        password = findViewById(R.id.et_password_setting);
        imageView = findViewById(R.id.user_profile_image_setting);

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
        navigationView.setCheckedItem(R.id.nav_profile);
        //Hide or Show Item
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_add_product).setVisible(true);
        //--Filling Navigation Drawer Data --//
        View headerView = navigationView.getHeaderView(0);
        TextView nameText = headerView.findViewById(R.id.user_profile_name);
        TextView userNameText = headerView.findViewById(R.id.user_profile_username);
        CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).into(profileImageView);
        nameText.setText(Prevalent.currentOnlineUser.getFullName());
        userNameText.setText("@" + Prevalent.currentOnlineUser.getEmail());
        //---------------------
        userInfoDisplay(imageView, name,email, password);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                ImagePicker.with(SettingActivity.this)
                        .cropSquare()
                        .compress(1024)
                        .start();
            }
        });
    }

    private void updateOnlyUserInfo() {
        String business;
        if(Paper.book().exist("UserBusiness")){
            business = Paper.book().read("UserBusiness");
        }else{
            business = Prevalent.UserChosenBusiness;
        }
        String db;
        if(Paper.book().exist("UserDB")){
            db = Paper.book().read("UserDB");
        }else{
            db = Prevalent.ParentDB;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Company").child(business).child(db);

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", name.getEditText().getText().toString());
        userMap.put("password", password.getEditText().getText().toString());
        userMap.put("email", email.getEditText().getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhoneNo()).updateChildren(userMap);

        if (Prevalent.ParentDB.equals("Admin")) {
            startActivity(new Intent(SettingActivity.this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(SettingActivity.this, UserDashboardActivity.class));
        }
        finish();
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(name.getEditText().getText().toString())) {
            Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password.getEditText().getText().toString())) {
            Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email.getEditText().getText().toString())) {
            Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {

        if (imageUri != null) {
            final StorageReference fileRef = storeProfileImageRef.child(Prevalent.currentOnlineUser.getPhoneNo() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myURL = downloadUrl.toString();
                        String business;
                        if(Paper.book().exist("UserBusiness")){
                            business = Paper.book().read("UserBusiness");
                        }else{

                            business = Prevalent.UserChosenBusiness;
                        }
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Company").child(business).child(Prevalent.ParentDB);
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", name.getEditText().getText().toString());
                        userMap.put("password", password.getEditText().getText().toString());
                        userMap.put("image", myURL);
                        userMap.put("email", email.getEditText().getText().toString());

                        ref.child(Prevalent.currentOnlineUser.getPhoneNo()).updateChildren(userMap);


                        if (Prevalent.ParentDB.equals("Admin")) {
                            startActivity(new Intent(SettingActivity.this, AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(SettingActivity.this, UserDashboardActivity.class));
                        }
                        finish();


                    } else {
                        Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Image is not selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(CircleImageView imageView, TextInputLayout name, TextInputLayout email, TextInputLayout password) {
        String business;
        if(Paper.book().exist("UserBusiness")){
            business = Paper.book().read("UserBusiness");
        }else{
            business = Prevalent.UserChosenBusiness;
        }
        String db;
        if(Paper.book().exist("UserDB")){
            db = Paper.book().read("UserDB");
        }else{
            db = Prevalent.ParentDB;
        }

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child(db).child(Prevalent.currentOnlineUser.getPhoneNo());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if (snapshot.child("image").exists()) {
                        String image = snapshot.child("image").getValue().toString();
                        String Username = snapshot.child("fullName").getValue().toString();
                        String Useremail = snapshot.child("email").getValue().toString();
                        String Useraddress = snapshot.child("password").getValue().toString();
                        //Can use change password field if requried
                        Picasso.get().load(image).into(imageView);
                        name.getEditText().setText(Username);
                        email.getEditText().setText(Useremail);
                        password.getEditText().setText(Useraddress);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            imageUri = data.getData();
            Toast.makeText(getApplicationContext(), imageUri.toString(), Toast.LENGTH_SHORT).show();
            imageView.setImageURI(imageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                if(Prevalent.ParentDB.equals("Admin")){
                    startActivity(new Intent(SettingActivity.this, AdminDashboardActivity.class));
                }else{
                    startActivity(new Intent(SettingActivity.this, UserDashboardActivity.class));
                }
                finish();
                break;
            case R.id.nav_add_product:
                startActivity(new Intent(SettingActivity.this, AddNewProductActivity.class));
                finish();
                break;
            case R.id.nav_orders:
                Toast.makeText(getApplicationContext(), "Pending Orders", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                recreate();
                break;
            case R.id.nav_logout:
                Paper.book().destroy();
                Intent intent = new Intent(SettingActivity.this, introductoryActivity.class);
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
        //super.onBackPressed();
    }
}