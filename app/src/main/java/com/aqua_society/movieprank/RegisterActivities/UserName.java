package com.aqua_society.movieprank.RegisterActivities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aqua_society.movieprank.BuildConfig;
import com.aqua_society.movieprank.R;
import com.aqua_society.movieprank.utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

public class UserName extends AppCompatActivity {


    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;


    Button btn_next;
    EditText input_name;
    TextInputLayout input_layout_name;

    private boolean loadInterstitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        initView();

        start();
    }

    private void start() {
        //Start Ads :
        native_AdsInit();
        banner_AdsInit();
        interstitial_AddInit();
    }

    private void initView(){
        btn_next = (Button) findViewById(R.id.btn_next);
        input_name = (EditText) findViewById(R.id.input_name);
        input_layout_name = (TextInputLayout) findViewById(R.id.input_layout_name);


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateName()){
                    if (mInterstitialAd.isLoaded() && loadInterstitial && !BuildConfig.DEBUG) {
                        mInterstitialAd.show();
                    }
                    else{
                        workToDoAfterCloseAdd();
                    }
                }
            }
        });
    }

    private void workToDoAfterCloseAdd(){
        Intent intent = new Intent(UserName.this, UserTele.class);
        intent.putExtra(utils.KEY_USER_NAME_INTENT,input_name.getText().toString());
        UserName.this.startActivity(intent);
    }


    private boolean validateName() {
        if (input_name.getText().toString().trim().isEmpty()) {
            input_layout_name.setError("المرجو إدخال إسمك الكامل");
            requestFocus(input_name);
            return false;
        } else {
            input_layout_name.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    // Ads Functions :
    private void native_AdsInit() {

        LinearLayout container = (LinearLayout) findViewById(R.id.adView_native);
        native_adView = utils.nativAds(getBaseContext(), container);
    }

    private void banner_AdsInit() {
        View adContainer = findViewById(R.id.adMobView);
        mAdView = utils.bannerAds(getBaseContext(), adContainer);
    }

    private void interstitial_AddInit() {
        mInterstitialAd = utils.interstitialAds(getBaseContext());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                workToDoAfterCloseAdd();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(utils.adRequest());
    }
}
