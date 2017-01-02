package com.aqua_society.movieprank;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.EmptyStackException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;

    LinearLayout home_start;
    RelativeLayout splashScreen;
    CircleProgressView mCircleProgressView;
    Button btn_getMovies;
    ImageView splashLogo;
    TextView textLoadingMoviesStart;
    ListView listCategory;

    private int current = -1;
    private boolean splashLoadingEnd = false;

    String selected = "";

    String categoryList[] = {
            "Bein Sport HD1",
            "Bein Sport HD2",
            "Bein Sport HD3",
            "Bein Sport HD4",
            "Bein Sport HD5",
            "Bein Sport HD6",
            "Bein Sport HD7",
            "Bein Sport HD8",
            "Bein Sport HD9",
            "Bein Sport HD10",
            "Bein Sport HD11",
            "Bein Sport HD12",
            "Bein Sport HD13",
            "Bein Sport HD14",
            "Bein Sport HD15",
            "Bein Sport HD16",
            "Bein Sport HD17",
    };

    private boolean loadInterstitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewInit();

        startSplash(5); // Splash Duration seconds

        GetAdsCodes();
    }

    private void GetAdsCodes(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rawgit.com/CharifMakaoui/17f872ce6045d96987f85085b523c80b/raw/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MaillerApi admobCodes = retrofit.create(MaillerApi.class);

        Call<AdsObject> connection = admobCodes.getAdsConfig();
        connection.enqueue(new Callback<AdsObject>() {
            @Override
            public void onResponse(Call<AdsObject> call, Response<AdsObject> response) {
                Log.d("data","Nice Get Admob Codes From File URL "+ response.body().getBannerAd());
                if(response.body().getBannerAd() != null)
                    utils.bannerAd_CODE = response.body().getBannerAd();

                if(response.body().getInterstitialAd() != null)
                    utils.interstitialAd_CODE = response.body().getInterstitialAd();

                if(response.body().getNativeAd() != null)
                    utils.nativeAd_CODE = response.body().getNativeAd();

                start();
            }

            @Override
            public void onFailure(Call<AdsObject> call, Throwable t) {
                Log.d("data","Error Get Admob Codes From File URL");
            }
        });

    }

    private void viewInit() {
        home_start = (LinearLayout) findViewById(R.id.home_start);
        splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashLogo = (ImageView) findViewById(R.id.splashLogo);
        textLoadingMoviesStart = (TextView) findViewById(R.id.textLoadingMoviesStart);

        listCategory = (ListView) findViewById(R.id.listCategory);
        setupListCat();

        if(!utils.protection(getBaseContext()))
            throw new EmptyStackException();

        mCircleProgressView = (CircleProgressView) findViewById(R.id.circle_progress_view);
        btn_getMovies = (Button) findViewById(R.id.btn_getMovies);
        btn_getMovies.setOnClickListener(new View.OnClickListener() {
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

    private void start() {
        //Start Ads :
        native_AdsInit();
        //banner_AdsInit();
        interstitial_AddInit();
    }

    private void setupListCat(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.category_row, R.id.lists_cat_row, categoryList);
        listCategory.setAdapter(arrayAdapter);
        listCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mInterstitialAd.isLoaded() && loadInterstitial && !BuildConfig.DEBUG) {
                    mInterstitialAd.show();
                }
                else{
                    workToDoAfterCloseAdd();
                }
                Toast.makeText(getBaseContext(),"Selected : "+categoryList[position], Toast.LENGTH_LONG).show();
                selected = categoryList[position];
                textLoadingMoviesStart.setText("جاري تحميل "+ selected);
                utils.type = selected;
            }
        });
    }

    private void workToDoAfterCloseAdd() {
        current++;
        switch (current) {
            case 0:
                goNextActivity(5);
                break;
        }
    }

    private void goNextActivity(int duration) {
        splashScreen.setVisibility(View.VISIBLE);
        home_start.setVisibility(View.GONE);

        mCircleProgressView.setStartAngle(5);
        mCircleProgressView.setProgressWithAnimation(100, 1000 * duration);
    }

    private void startSplash(int duration) {
        splashScreen.setVisibility(View.VISIBLE);
        home_start.setVisibility(View.GONE);

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
                if (current == -1 && !splashLoadingEnd) {
                    splashScreen.setVisibility(View.GONE);
                    home_start.setVisibility(View.VISIBLE);
                    splashLogo.setVisibility(View.GONE);
                    textLoadingMoviesStart.setVisibility(View.VISIBLE);
                    splashLoadingEnd = true;
                    mCircleProgressView.setProgress(0);
                } else if (current == 0) {
                    Intent intent = new Intent(MainActivity.this, Activity_1.class);
                    MainActivity.this.startActivity(intent);
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
                workToDoAfterCloseAdd();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(utils.adRequest());
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
