package com.aqua_society.movieprank.RegisterActivities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aqua_society.movieprank.Activation;
import com.aqua_society.movieprank.BuildConfig;
import com.aqua_society.movieprank.MaillerApi;
import com.aqua_society.movieprank.R;
import com.aqua_society.movieprank.utils;
import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import org.w3c.dom.Text;

import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserEmail extends AppCompatActivity {

    //Ads Config :
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;

    Button btn_next;
    TextInputLayout input_layout_email;
    EditText input_email;
    TextView sendDone,information;
    LinearLayout activity_user_email;

    RelativeLayout splashScreen;
    ImageView splashLogo;
    TextView textLoadingMoviesStart;
    CircleProgressView circle_progress_view;

    String user_name,user_tele;

    private boolean loadInterstitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_email);

        Intent intent = getIntent();
        user_name = intent.getExtras().getString(utils.KEY_USER_NAME_INTENT);
        user_tele = intent.getExtras().getString(utils.KEY_USER_TELE_INTENT);

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
        activity_user_email = (LinearLayout) findViewById(R.id.activity_user_email);
        btn_next = (Button) findViewById(R.id.btn_next);
        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_email = (EditText) findViewById(R.id.input_email);
        sendDone = (TextView) findViewById(R.id.sendDone);
        information = (TextView) findViewById(R.id.information);

        splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashScreen.setVisibility(View.GONE);
        splashLogo = (ImageView) findViewById(R.id.splashLogo);
        splashLogo.setVisibility(View.GONE);
        textLoadingMoviesStart = (TextView) findViewById(R.id.textLoadingMoviesStart);
        textLoadingMoviesStart.setVisibility(View.VISIBLE);
        textLoadingMoviesStart.setText("جاري إرسال كود التفعيل للبريد "+ input_email.getText().toString());
        circle_progress_view = (CircleProgressView) findViewById(R.id.circle_progress_view);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail()){

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
        sendPine();
    }

    private void sendPine(){

        circle_progress_view.setTextEnabled(true);
        circle_progress_view.setInterpolator(new AccelerateDecelerateInterpolator());
        circle_progress_view.setStartAngle(5);
        circle_progress_view.setProgressWithAnimation(80, 1000 * 5);

        activity_user_email.setVisibility(View.GONE);
        splashScreen.setVisibility(View.VISIBLE);

        circle_progress_view.addAnimationListener(new ProgressAnimationListener(){

            @Override
            public void onValueChanged(float v) {

            }

            @Override
            public void onAnimationEnd() {
                Log.d("AnimationEnd", "End Animation");
            }
        });

        Random randomizer = new Random();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mrcharif.cf")
                .build();

        MaillerApi maillerApi = retrofit.create(MaillerApi.class);
        Call<ResponseBody> sentValidation =
                maillerApi.sendPine(input_email.getText().toString(),
                        user_name,
                        user_tele,
                        String.valueOf(utils.validPins[randomizer.nextInt(utils.validPins.length)]));

        sendDone.setText("جاري إرسال الرسالة لبريدك الإلكتروني");

        sentValidation.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                circle_progress_view.setProgressWithAnimation(100, 500);
                textLoadingMoviesStart.setText(" لقد تم إرسال الرسالة لك بنجاح المرجو التأكد من بريدك الإلكتروني و جلب كود التفعيل (سوف تحتاجه في الخطوة القادمة)");
                textLoadingMoviesStart.setText(textLoadingMoviesStart.getText().toString() + " \n سوف يتم تحويلك تلقائيا بعد 3 ثواني");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(UserEmail.this, Activation.class);
                        UserEmail.this.startActivity(intent);
                        finish();
                    }
                }, 3000);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                activity_user_email.setVisibility(View.VISIBLE);
                splashScreen.setVisibility(View.GONE);
                circle_progress_view.setProgress(0);
                sendDone.setVisibility(View.VISIBLE);
                information.setVisibility(View.GONE);
                sendDone.setText("حدث خطأ أثناء محاولة الإتصال بالسيرفر");
            }
        });
    }

    private boolean validateEmail() {
        String email = input_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            input_layout_email.setError("المرجو إدخال بريد إلكتروني صالح");
            Toast.makeText(getBaseContext(), "المرجو إدخال بريد إلكتروني صالح" , Toast.LENGTH_SHORT).show();
            requestFocus(input_email);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
