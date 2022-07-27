package com.example.paykeeperfyp.onBoardScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.R;

import java.util.ArrayList;
import java.util.List;

public class onBoardScreen extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    ViewPager2 onboardingViewPager;
    TextView skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_board_screen);

        initWidget();
        //Setting Up What to display on On Board Screen
        setupOnboardingItems();
        onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(onBoardScreen.this, introductoryActivity.class));
                finish();
            }
        });


    }

    private void setupOnboardingItems() {

        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem itemTime = new OnboardingItem();
        itemTime.setTitle("Time is Money");
        itemTime.setDescription("Save precious hours by updating daily business accounts in minutes, only on the Udhar Book app.");
        itemTime.setImage(R.drawable.img1);


        OnboardingItem itemFee = new OnboardingItem();
        itemFee.setTitle("Zero Fees");
        itemFee.setDescription("Bank your Life.We create something new you have never seen before");
        itemFee.setImage(R.drawable.img2);


        OnboardingItem itemSafe = new OnboardingItem();
        itemSafe.setTitle("Safe And Secure");
        itemSafe.setDescription("All details of credit-debit for any number of customers across multiple businesses is ready and handy on your phone.");
        itemSafe.setImage(R.drawable.img3);

        onboardingItems.add(itemTime);
        onboardingItems.add(itemFee);
        onboardingItems.add(itemSafe);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);

    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active));
            } else {

                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }
    }

    private void initWidget() {
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicator);
        skip = findViewById(R.id.skip);
    }
}