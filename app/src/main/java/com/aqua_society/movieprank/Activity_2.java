package com.aqua_society.movieprank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import org.w3c.dom.Text;

import java.util.EmptyStackException;

public class Activity_2 extends AppCompatActivity {

    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;

    RelativeLayout home_start;
    RelativeLayout splashScreen;
    ImageView splashLogo;
    CircleProgressView mCircleProgressView;

    TextView textLoadingMoviesStart, error_loading;

    int current = 0;

    private boolean loadInterstitial = true;


    ListView listServeurs;
    private String[] serveursList = {
        "Serveur 1",
        "Serveur 2",
        "Serveur 3",
        "Serveur 4",
        "Serveur 5",
        "Serveur 6",
        "Serveur 7",
        "Serveur 8",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

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

        home_start = (RelativeLayout) findViewById(R.id.home_start);
        splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashScreen.setVisibility(View.GONE);
        splashLogo = (ImageView) findViewById(R.id.splashLogo);
        splashLogo.setVisibility(View.GONE);

        textLoadingMoviesStart = (TextView) findViewById(R.id.textLoadingMoviesStart);

        mCircleProgressView = (CircleProgressView) findViewById(R.id.circle_progress_view);
        error_loading = (TextView) findViewById(R.id.error_loading);

        if(!utils.protection(getBaseContext()))
            throw new EmptyStackException();

        listServeurs = (ListView) findViewById(R.id.listServeurs);
        SetupListServers();
    }

    private void SetupListServers(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.category_row, R.id.lists_cat_row, serveursList);
        listServeurs.setAdapter(arrayAdapter);
        listServeurs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mInterstitialAd.isLoaded() && loadInterstitial && !BuildConfig.DEBUG) {
                    mInterstitialAd.show();
                }
                else{
                    workToDoAfterCloseAdd(5);
                }
                Toast.makeText(getBaseContext(),"Selected : "+serveursList[position], Toast.LENGTH_LONG).show();
                textLoadingMoviesStart.setText("جاري تحميل "+ serveursList[position]);
                utils.type = serveursList[position];
            }
        });
    }

    public void workToDoAfterCloseAdd(int duration) {
        mCircleProgressView.setTextEnabled(true);
        mCircleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
        mCircleProgressView.setStartAngle(5);

        if(current == 0)
            mCircleProgressView.setProgressWithAnimation(55, 1000 * duration);
        else
            mCircleProgressView.setProgressWithAnimation(100, 1000 * duration * 2);

        home_start.setVisibility(View.GONE);
        splashScreen.setVisibility(View.VISIBLE);
        textLoadingMoviesStart.setVisibility(View.VISIBLE);
        native_adView.setVisibility(View.GONE);

        mCircleProgressView.addAnimationListener(new ProgressAnimationListener() {
            @Override
            public void onValueChanged(float value) {

            }

            @Override
            public void onAnimationEnd() {

                if(current == 0){
                    error_loading.setVisibility(View.VISIBLE);
                    home_start.setVisibility(View.VISIBLE);
                    splashScreen.setVisibility(View.GONE);
                    textLoadingMoviesStart.setVisibility(View.GONE);
                    native_adView.setVisibility(View.VISIBLE);
                    current ++;
                    mCircleProgressView.setProgress(0);
                }
                else{
                    Intent intent = new Intent(Activity_2.this, Activity_player.class);
                    Activity_2.this.startActivity(intent);
                    finish();
                }
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
