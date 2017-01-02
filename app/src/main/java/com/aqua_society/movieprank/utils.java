package com.aqua_society.movieprank;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by MrCharif on 21/12/2016.
 */

public class utils {

    public static String KEY_USER_NAME_INTENT = "USER_NAME_KEY";
    public static String KEY_USER_TELE_INTENT = "USER_TELE_KEY";
    public static String KEY_USER_EMAIL_INTENT = "USER_EMAIL_KEY";

    public static String bannerAd_CODE = "";
    public static String interstitialAd_CODE = "";
    public static String nativeAd_CODE = "";

    public static boolean protection(Context context){
        String protection_code = "MDExMDAwMTEgMDExMDExMTEgMDExMDExMDEgMDAxMDExMTAgMDExMDAwMDEgMDExMTAwMDEgMDExMTAxMDEgMDExMDAwMDEgMDEwMTExMTEgMDExMTAwMTEgMDExMDExMTEgMDExMDAwMTEgMDExMDEwMDEgMDExMDAxMDEgMDExMTAxMDAgMDExMTEwMDEgMDAxMDExMTAgMDExMDExMDEgMDExMDExMTEgMDExMTAxMTAgMDExMDEwMDEgMDExMDAxMDEgMDExMTAwMDAgMDExMTAwMTAgMDExMDAwMDEgMDExMDExMTAgMDExMDEwMTE=";
        String p_name = context.getPackageName();

        byte[] valueDecoded= Base64.decode(protection_code,0);
        String str = utils.int2str(new String(valueDecoded));

        return str.equals(p_name);
    }

    public static String int2str( String s ) {
        String[] ss = s.split( " " );
        StringBuilder sb = new StringBuilder();
        for (String s1 : ss) {
            sb.append((char) Integer.parseInt(s1, 2));
        }
        return sb.toString();
    }

    public static String type = "";

    public static int[] validPins = {15789,58962,65489,99845,22567,58896,36482,21578,64892,53189,51473,62148};

    public static boolean validatePine(int pin){
        for(int i=0; i< utils.validPins.length; i++){
            if(pin == utils.validPins[i]){
                return true;
            }
        }

        return false;
    }

    public static AdView bannerAds(Context context, View view){
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);

        if(!utils.bannerAd_CODE.equals("") && utils.bannerAd_CODE != null){
            mAdView.setAdUnitId(utils.bannerAd_CODE);
        }
        else{
            mAdView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
        }

        ((RelativeLayout) view).addView(mAdView);
        mAdView.loadAd(utils.adRequest());

        return mAdView;
    }

    public static NativeExpressAdView nativAds(Context context, View view){
        NativeExpressAdView native_adView = new NativeExpressAdView(context);
        native_adView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 132));

        if(utils.nativeAd_CODE != null && !utils.nativeAd_CODE.equals("")){
            native_adView.setAdUnitId(utils.nativeAd_CODE);
        }
        else{
            native_adView.setAdUnitId(context.getString(R.string.native_ad_unit_id));
        }


        ((LinearLayout) view).addView(native_adView);
        native_adView.loadAd(utils.adRequest());

        return  native_adView;
    }

    public static AdRequest adRequest(){
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        return request;
    }

    public static InterstitialAd interstitialAds(Context context){
        InterstitialAd interstitialAd = new InterstitialAd(context);

        if(!utils.nativeAd_CODE.equals("") && utils.interstitialAd_CODE != null){
            interstitialAd.setAdUnitId(utils.interstitialAd_CODE);
        }

        else{
            interstitialAd.setAdUnitId(context.getString(R.string.interstitial_ad_unit_id));
        }

        return interstitialAd;
    }
}
