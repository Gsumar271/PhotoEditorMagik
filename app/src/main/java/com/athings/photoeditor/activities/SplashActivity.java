package com.athings.photoeditor.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.athings.photoeditor.R;
import com.athings.photoeditor.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SplashActivity extends AppCompatActivity {
    protected boolean _active = true;
    protected int _splashTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    e.toString();
                } finally {
                    Intent localIntent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                    startActivity(localIntent);
                    finish();
                }
            }
        };
        splashTread.start();
        /*
        if (isOnline()) {
            try {
                if (Constants.ADS_STATUS) {
                    if (Constants.ADS_TYPE.equals("admob")) {
                       // loadFullScreenAds();
                    } else if (Constants.ADS_TYPE.equals("facebook")) {
                        loadFBFullscreenAds();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Thread splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        while (_active && (waited < _splashTime)) {
                            sleep(100);
                            if (_active) {
                                waited += 100;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.toString();
                    } finally {
                        Intent localIntent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                        startActivity(localIntent);
                        finish();
                    }
                }
            };
            splashTread.start();
        }
        */
    }



    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    void loadFullScreenAds() throws Exception {
        final InterstitialAd mInterstitialAd = new InterstitialAd(SplashActivity.this);
        mInterstitialAd.setAdUnitId(Constants.ADS_ADMOB_FULLSCREEN_ID);
       // AdRequest adRequest1 = new AdRequest.Builder().addTestDevice("53A509AC02225B8FEF6B9787D326F76F").build();
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest1);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void loadFBFullscreenAds() {

        final com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(this, Constants.ADS_FACEBOOK_FULLSCREEN_ID);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        interstitialAd.loadAd();

    }
}
