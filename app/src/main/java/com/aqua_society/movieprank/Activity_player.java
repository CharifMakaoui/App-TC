package com.aqua_society.movieprank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aqua_society.movieprank.RegisterActivities.UserName;
import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.EmptyStackException;

public class Activity_player extends AppCompatActivity {

    RelativeLayout player;
    LinearLayout activation;

    Button newPine,btn_activate;
    EditText input_code_pine;

    WebView videoView;

    RelativeLayout splashScreen;
    ImageView splashLogo;
    TextView textLoadingMoviesStart;
    CircleProgressView circle_progress_view;


    private boolean loadInterstitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        initView();
        try {
            StartPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        player = (RelativeLayout) findViewById(R.id.player);
        activation = (LinearLayout) findViewById(R.id.activation);

        player.setVisibility(View.VISIBLE);
        activation.setVisibility(View.GONE);

        videoView = (WebView) findViewById(R.id.videoView);

        input_code_pine = (EditText) findViewById(R.id.input_code_pine);

        newPine = (Button) findViewById(R.id.newPine);
        btn_activate = (Button) findViewById(R.id.btn_activate);

        if(!utils.protection(getBaseContext()))
            throw new EmptyStackException();

        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!utils.validatePine(Integer.parseInt(input_code_pine.getText().toString()))){
                    Toast.makeText(getBaseContext(),"الكود الذي أدخلته غير صالح",Toast.LENGTH_LONG).show();
                }
                else{
                    workStart();
                }
            }
        });

        newPine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_player.this, UserName.class);
                Activity_player.this.startActivity(intent);
                finish();
            }
        });


        splashScreen = (RelativeLayout) findViewById(R.id.splashScreen);
        splashScreen.setVisibility(View.GONE);
        splashLogo = (ImageView) findViewById(R.id.splashLogo);
        splashLogo.setVisibility(View.GONE);
        textLoadingMoviesStart = (TextView) findViewById(R.id.textLoadingMoviesStart);
        textLoadingMoviesStart.setVisibility(View.VISIBLE);
        circle_progress_view = (CircleProgressView) findViewById(R.id.circle_progress_view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void StartPlayer() throws IOException {

        InputStream is = getAssets().open("animated_signal.html");
        int size = is.available();

        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        String str = new String(buffer);

        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.loadDataWithBaseURL("", str, "text/html", "UTF-8", "");

        WowWait();
    }

    private void WowWait(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                player.setVisibility(View.GONE);
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activation.setVisibility(View.VISIBLE);
            }
        }, 15000);
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
                textLoadingMoviesStart.setText("يبدو أن هناك مشكلة أثناء التفعيل لم نتمكن من تحديد إتمامك لأحد الخطوات . سوف نقوم بالتواصل معك و تفعيل حسابك يدويا في مدة أقصاها 24 ساعة . ");
            }
        });

    }


    private void StartAfterTime(){
        player.setVisibility(View.GONE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
