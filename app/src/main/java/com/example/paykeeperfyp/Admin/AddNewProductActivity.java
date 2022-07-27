package com.example.paykeeperfyp.Admin;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.Management.managerDashboard;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class AddNewProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private String name, price, category, desc, saveCurrentDate, saveCurrentTime, featured, qty;
    public static final int GALLERY_PICK = 1;
    private Uri imageUri;
    private String productRandomKey, DownloadImageUrl;

    private Button add_product_btn;
    private Switch simpleSwitch;
    private RelativeLayout progress_bar;
    private TextInputLayout new_pro_name, new_pro_desc, new_pro_price, new_pro_category, new_pro_qty;
    private ImageView new_pro_image;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_new_product);
        //Nav Hooks
        drawerLayout = findViewById(R.id.drawer_layout_add_product);
        navigationView = findViewById(R.id.nav_view_add_product);
        toolbar = findViewById(R.id.toolbar_nav_view_add_product);
        initwidget();

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
        navigationView.setCheckedItem(R.id.nav_add_product);
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
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.dummy_user).into(profileImageView);
        nameText.setText(Prevalent.currentOnlineUser.getFullName());
        userNameText.setText(Prevalent.currentOnlineUser.getEmail());

        //---------------------

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        String business = Prevalent.UserChosenBusiness;
        ProductsRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child("Products");
        onImageCapture();
        addNewButton();


    }


    private void addNewButton() {

        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                new_pro_name.setVisibility(View.INVISIBLE);
                new_pro_desc.setVisibility(View.INVISIBLE);
                new_pro_price.setVisibility(View.INVISIBLE);
                new_pro_category.setVisibility(View.INVISIBLE);
                new_pro_qty.setVisibility(View.INVISIBLE);

                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {

        name = new_pro_name.getEditText().getText().toString();
        desc = new_pro_desc.getEditText().getText().toString();
        price = new_pro_price.getEditText().getText().toString();
        category = new_pro_category.getEditText().getText().toString();
        qty = new_pro_qty.getEditText().getText().toString();

        if (simpleSwitch.isChecked()) {
            featured = "true";
        } else {
            featured = "false";
        }

        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), "Product Image is Required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(desc)) {
            Toast.makeText(getApplicationContext(), "Please write product description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(getApplicationContext(), "Please write product price", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(category)) {
            Toast.makeText(getApplicationContext(), "Please write product category", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductImageInformation();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    private void StoreProductImageInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment() + productRandomKey);

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress_bar.setVisibility(View.INVISIBLE);
                new_pro_name.setVisibility(View.VISIBLE);
                new_pro_desc.setVisibility(View.VISIBLE);
                new_pro_price.setVisibility(View.VISIBLE);
                new_pro_category.setVisibility(View.VISIBLE);
                new_pro_qty.setVisibility(View.VISIBLE);
                String message = e.getMessage().toString();
                Toast.makeText(AddNewProductActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddNewProductActivity.this, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                //if image is uploaded it will get that link of image to be stored in database

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            progress_bar.setVisibility(View.INVISIBLE);
                            new_pro_name.setVisibility(View.VISIBLE);
                            new_pro_desc.setVisibility(View.VISIBLE);
                            new_pro_price.setVisibility(View.VISIBLE);
                            new_pro_category.setVisibility(View.VISIBLE);
                            new_pro_qty.setVisibility(View.VISIBLE);
                            throw task.getException();
                        }
                        DownloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            DownloadImageUrl = task.getResult().toString();
                            Toast.makeText(getApplicationContext(), "Getting Product image is added to DATABASE!", Toast.LENGTH_SHORT).show();
                            SaveProductInformationToDB();

                        } else {
                            progress_bar.setVisibility(View.INVISIBLE);
                            new_pro_name.setVisibility(View.VISIBLE);
                            new_pro_desc.setVisibility(View.VISIBLE);
                            new_pro_price.setVisibility(View.VISIBLE);
                            new_pro_category.setVisibility(View.VISIBLE);
                            new_pro_qty.setVisibility(View.VISIBLE);
                            String msg = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "Error " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInformationToDB() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", desc);
        productMap.put("image", DownloadImageUrl);
        productMap.put("category", category);
        productMap.put("price", price);
        productMap.put("pname", name);
        productMap.put("featured", featured);
        productMap.put("qty", qty);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progress_bar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(AddNewProductActivity.this, AdminDashboardActivity.class));
                            Toast.makeText(getApplicationContext(), "Products is added Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "Error " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void onImageCapture() {

        new_pro_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(AddNewProductActivity.this)
                        .cropSquare().compress(1024).start();
            }
        });

    }

    private void initwidget() {
        //Hooks
        progress_bar = findViewById(R.id.progress_bar5);
        add_product_btn = findViewById(R.id.add_product_btn);
        new_pro_image = findViewById(R.id.img_new_product);
        new_pro_name = findViewById(R.id.et_new_product_name);
        new_pro_desc = findViewById(R.id.et_new_product_desc);
        new_pro_price = findViewById(R.id.et_new_product_price);
        new_pro_category = findViewById(R.id.et_new_product_category);
        new_pro_qty = findViewById(R.id.et_new_product_qty);
        simpleSwitch = findViewById(R.id.feature_product);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(AddNewProductActivity.this, AdminDashboardActivity.class));
                finish();
                break;
            case R.id.nav_add_product:
                startActivity(new Intent(AddNewProductActivity.this, AddNewProductActivity.class));
                break;
            case R.id.nav_orders:
                startActivity(new Intent(AddNewProductActivity.this, managerDashboard.class));
                break;
            case R.id.nav_profile:
                Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Paper.book().destroy();
                Intent intent = new Intent(AddNewProductActivity.this, introductoryActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            new_pro_image.setImageURI(imageUri);
        }else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


}