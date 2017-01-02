package com.aqua_society.movieprank;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.EmptyStackException;

public class Activity_1 extends AppCompatActivity implements View.OnClickListener {


    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;

    LinearLayout home_start;
    RelativeLayout splashScreen;
    CircleProgressView mCircleProgressView;
    ImageView splashLogo;
    TextView textLoadingMoviesStart;

    Button translated_movies, no_translated_movies;
    String selected = "";
    String type = "";

    private boolean loadInterstitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        initView();

        start();
    }

    private void start() {

        //Start Ads :
        native_AdsInit();
        banner_AdsInit();
        interstitial_AddInit();
    }

    private void initView() {
        translated_movies = (Button) findViewById(R.id.translated_movies);
        no_translated_movies = (Button) findViewById(R.id.no_translated_movies);

        home_start = (LinearLayout) findViewById(R.id.home_start);
        splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashScreen.setVisibility(View.GONE);
        splashLogo = (ImageView) findViewById(R.id.splashLogo);
        splashLogo.setVisibility(View.GONE);
        textLoadingMoviesStart = (TextView) findViewById(R.id.textLoadingMoviesStart);

        if(!utils.protection(getBaseContext()))
            throw new EmptyStackException();

        mCircleProgressView = (CircleProgressView) findViewById(R.id.circle_progress_view);

        translated_movies.setOnClickListener(this);
        no_translated_movies.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (mInterstitialAd.isLoaded() && loadInterstitial && !BuildConfig.DEBUG) {
            mInterstitialAd.show();
        }
        else{
            workToDoAfterCloseAdd(5);
        }

        home_start.setVisibility(View.GONE);
        splashScreen.setVisibility(View.VISIBLE);
        textLoadingMoviesStart.setVisibility(View.VISIBLE);

        if (v.getId() == R.id.translated_movies) {
            selected = "جاري ضبط الجودة HD";

        } else {
            selected = "جاري ضبط الجودة SD";
        }
        textLoadingMoviesStart.setText(selected);

    }

    public void workToDoAfterCloseAdd(int duration) {
        mCircleProgressView.setTextEnabled(true);
        mCircleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
        mCircleProgressView.setStartAngle(5);
        mCircleProgressView.setProgressWithAnimation(100, 1000 * duration);

        mCircleProgressView.addAnimationListener(new ProgressAnimationListener() {
            @Override
            public void onValueChanged(float value) {

            }

            @Override
            public void onAnimationEnd() {

                Intent intent = new Intent(Activity_1.this, Activity_2.class);
                Activity_1.this.startActivity(intent);
                finish();

            }
        });
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
                workToDoAfterCloseAdd(5);
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(utils.adRequest());
    }

}
