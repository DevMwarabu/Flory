package com.laxco.gardening;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.laxco.gardening.Introduction.PrefManager;
import com.laxco.gardening.Introduction.WelcomeActivity;

public class Splashscreen extends AppCompatActivity {
    private LottieAnimationView mLottieAnimationView;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        //init
        prefManager = new PrefManager(this);
        mLottieAnimationView = findViewById(R.id.lotty_main);
        loadingApp();
    }

    private void loadingApp() {

        mLottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //checking if first time
                if (!prefManager.isFirstTimeLaunch()){
                    //opening dashboard
                    startActivity(new Intent(Splashscreen.this, Dashboard.class));
                    //finishing activity
                    Splashscreen.this.finish();
                }else {
                    //opening dashboard
                    startActivity(new Intent(Splashscreen.this, WelcomeActivity.class));
                    //finishing activity
                    Splashscreen.this.finish();
                }
                super.onAnimationEnd(animation);
            }
        });
    }

    //disabling onBackpressed
    @Override
    public void onBackPressed() {

    }
}