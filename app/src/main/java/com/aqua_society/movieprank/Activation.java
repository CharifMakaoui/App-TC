package com.aqua_society.movieprank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aqua_society.movieprank.RegisterActivities.UserName;
import com.aqua_society.movieprank.RegisterActivities.UserTele;
import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

public class Activation extends AppCompatActivity {

    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;

    EditText input_code_pine;
    Button btn_activate;
    LinearLayout activation;

    RelativeLayout splashScreen;
    ImageView splashLogo;
    TextView textLoadingMoviesStart;
    CircleProgressView circle_progress_view;
    Button hidden_splash_button;

    private boolean loadInterstitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

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
        input_code_pine = (EditText) findViewById(R.id.input_code_pine);
        btn_activate = (Button) findViewById(R.id.btn_activate);
        activation = (LinearLayout) findViewById(R.id.activation);


        splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashScreen.setVisibility(View.GONE);
        splashLogo = (ImageView) findViewById(R.id.splashLogo);
        splashLogo.setVisibility(View.GONE);
        textLoadingMoviesStart = (TextView) findViewById(R.id.textLoadingMoviesStart);
        textLoadingMoviesStart.setVisibility(View.VISIBLE);
        circle_progress_view = (CircleProgressView) findViewById(R.id.circle_progress_view);
        hidden_splash_button = (Button) findViewById(R.id.hidden_splash_button);

        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded() && loadInterstitial && !BuildConfig.DEBUG) {
                    mInterstitialAd.show();
                }
                else{
                    workToDoAfterCloseAdd();
                }
            }
        });
    }

    private void workToDoAfterCloseAdd(){
        if(!utils.validatePine(Integer.parseInt(input_code_pine.getText().toString()))){
            Toast.makeText(getBaseContext(),"الكود الذي أدخلته غير صالح",Toast.LENGTH_LONG).show();
        }
        else{
            workStart();
        }
    }

    private void workStart(){
        circle_progress_view.setTextEnabled(true);
        circle_progress_view.setInterpolator(new AccelerateDecelerateInterpolator());
        circle_progress_view.setStartAngle(5);
        circle_progress_view.setProgressWithAnimation(80, 1000 * 5);

        activation.setVisibility(View.GONE);
        splashScreen.setVisibility(View.VISIBLE);
        textLoadingMoviesStart.setText("جاري تفعيل حسابك مع التطبيق");

        circle_progress_view.addAnimationListener(new ProgressAnimationListener(){

            @Override
            public void onValueChanged(float v) {
                Log.d("valueChanged", String.valueOf(v));
            }

            @Override
            public void onAnimationEnd() {
                hidden_splash_button.setVisibility(View.VISIBLE);
                hidden_splash_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Activation.this, MainActivity.class);
                        Activation.this.startActivity(intent);
                        finish();
                    }
                });
                circle_progress_view.setVisibility(View.GONE);
                textLoadingMoviesStart.setText("يبدو أن هناك مشكلة أثناء التفعيل لم نتمكن من تحديد إتمامك لأحد الخطوات . سوف نقوم بالتواصل معك و تفعيل حسابك يدويا في مدة أقصاها 24 ساعة . ");
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
                workToDoAfterCloseAdd();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(utils.adRequest());
    }
}
