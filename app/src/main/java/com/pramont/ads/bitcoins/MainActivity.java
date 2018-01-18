package com.pramont.ads.bitcoins;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TAG = "DEBUG";
    private InterstitialAd mInterstitialAd;
    public static final String URL = "https://bitso.com/login";
    private WebView mWebView = null ;
    private WebSettings mWebSettings ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        /** WebView embedded no need to add it into any layout**/
        mWebView = new WebView(this);
        setContentView(mWebView);

        /** Enabling java script for web view**/
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        /** Overriding the loading page**/
        mWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                Map<String,String> myMap = new HashMap<String, String>();
                myMap = request.getRequestHeaders();
                Log.d(TAG,"Request:"+request.getUrl());
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        mWebView.loadUrl(URL);

    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ads done");
               loadInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e(TAG, "Error to load adds: Code "+ errorCode);
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToNextLevel();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else
        {
            Log.d(TAG,"ad did not load");
            goToNextLevel();
        }
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
        showInterstitial();
    }

    private void goToNextLevel() {
        Log.d(TAG,"Load webview");
        // Show the next level and reload the ad to prepare for the level after.
        //mLevelTextView.setText("Level " + (++mLevel));
        //mInterstitialAd = newInterstitialAd();
        //loadInterstitial();
    }

    /** Giving the UX for back button (android) as keyboard back space **/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
