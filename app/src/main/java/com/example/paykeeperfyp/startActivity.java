package com.example.paykeeperfyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.onBoardScreen.onBoardScreen;

public class startActivity extends AppCompatActivity {
    SharedPreferences onBoardingScreen;
    ImageView background;
    TextView title, description, creator;
    LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initWidget();

        background.animate().alpha(0).setStartDelay(4000);
        title.animate().alpha(0).setStartDelay(4000);
        description.animate().alpha(0).setStartDelay(4000);
        lottieAnimationView.animate().alpha(0).setStartDelay(4000);
        creator.animate().alpha(0).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                onBoardingScreen = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);
                //By default make and sett true

                if(isFirstTime){
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    Intent intent = new Intent(startActivity.this,onBoardScreen.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(startActivity.this, introductoryActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 5000);

    }

    private void initWidget() {

        background = findViewById(R.id.splash_bg);
        title = findViewById(R.id.app_title);
        description = findViewById(R.id.app_description);
        lottieAnimationView = findViewById(R.id.lottie);
        creator = findViewById(R.id.creator);

    }
}