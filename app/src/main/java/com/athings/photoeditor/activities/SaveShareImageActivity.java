package com.athings.photoeditor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.athings.photoeditor.R;
import com.athings.photoeditor.bitmap.BitmapLoader;
import com.athings.photoeditor.image.ImageUtility;
import com.athings.photoeditor.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.List;

public class SaveShareImageActivity extends AppCompatActivity {

    private Bundle bundle;
    private String imagePath;
    private ImageView shareImageview;
    AdView mAdView;
    com.facebook.ads.AdView adView;
    com.facebook.ads.InterstitialAd interstitialAd;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_share_image_activity);

        LinearLayout linearAdsBanner = (LinearLayout) findViewById(R.id.linearAds);

        if (isOnline() && Constants.ADS_STATUS) {
            linearAdsBanner.setVisibility(View.VISIBLE);

            if (Constants.ADS_TYPE.equals("admob")) {
                try {
                    addBannnerAds();
                    loadFullScreenAds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Constants.ADS_TYPE.equals("facebook")) {
                try {
                    addFBBannnerAds();
                    loadFBFullscreenAds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            linearAdsBanner.setVisibility(View.GONE);
        }

        bundle = getIntent().getExtras();
        if (bundle != null) {
            imagePath = bundle.getString("imagePath");
        }

        shareImageview = (ImageView) findViewById(R.id.share_imageView);
        new BitmapWorkerTask().execute();
        // Set a toolbar to replace the action bar.
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.tab_title_stores));
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    void loadFBFullscreenAds() {

        interstitialAd = new com.facebook.ads.InterstitialAd(this, Constants.ADS_FACEBOOK_FULLSCREEN_ID);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {

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

    void loadFullScreenAds() throws Exception {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.ADS_ADMOB_FULLSCREEN_ID);
        AdRequest localAdRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(localAdRequest);
    }

    void addBannnerAds() {
        LinearLayout linearAdsBanner = (LinearLayout) findViewById(R.id.linearAds);

        if (isOnline()) {

            linearAdsBanner.setVisibility(View.VISIBLE);
            if (linearAdsBanner.getChildCount() <= 0) {
                mAdView = new AdView(SaveShareImageActivity.this);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(Constants.ADS_ADMOB_BANNER_ID);
                AdRequest adRequest = new AdRequest.Builder().build();
                linearAdsBanner.addView(mAdView);
                mAdView.loadAd(adRequest);
            }
        } else {
            linearAdsBanner.setVisibility(View.GONE);
        }
    }

    void addFBBannnerAds() {
        LinearLayout linearAdsBanner = (LinearLayout) findViewById(R.id.linearAds);

        if (isOnline()) {

            linearAdsBanner.setVisibility(View.VISIBLE);
            if (linearAdsBanner.getChildCount() <= 0) {
                // Instantiate an AdView view
                adView = new com.facebook.ads.AdView(this, Constants.ADS_FACEBOOK_BANNER_ID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                // Add the ad view to your activity layout
                linearAdsBanner.addView(adView);

                // Request to load an ad
                adView.loadAd();
            }
        } else {
            linearAdsBanner.setVisibility(View.GONE);
        }
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_image_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_save_home) {
            Intent intent = new Intent(SaveShareImageActivity.this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            if (Constants.ADS_STATUS) {
                if (Constants.ADS_TYPE.equals("admob")) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                } else if (Constants.ADS_TYPE.equals("facebook")) {
                    if (interstitialAd.isAdLoaded()) {
                        interstitialAd.show();
                    }
                }
            }
        } else if (id == R.id.action_rate) {
            rate();
        } else if (id == R.id.action_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getApplicationInfo().loadLabel(getPackageManager()).toString());
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommand_message) + "  https://play.google.com/store/apps/details?id=" + getPackageName().toLowerCase() + " \n");
                startActivity(Intent.createChooser(i, "Choose one"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.action_more) {
            Intent inMoreapp = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Dabster+Apps"));
            if (isAvailable(inMoreapp)) {
                startActivity(inMoreapp);
            } else {
                Toast.makeText(SaveShareImageActivity.this, "There is no app availalbe for this task", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    void rate() {
        Intent intentRateMe = new Intent(Intent.ACTION_VIEW);
        if (ImageUtility.getAmazonMarket(SaveShareImageActivity.this)) {
            intentRateMe.setData(Uri.parse("amzn://apps/android?p=" + getPackageName().toLowerCase()));
        } else {
            intentRateMe.setData(Uri.parse("market://details?id=" + getPackageName().toLowerCase()));
        }
        startActivity(intentRateMe);
    }

    private class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        DisplayMetrics metrics;
        BitmapLoader bitmapLoader;

        public BitmapWorkerTask() {
            metrics = getResources().getDisplayMetrics();
            bitmapLoader = new BitmapLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Void... arg0) {
            try {
                return bitmapLoader.load(getApplicationContext(), new int[]{metrics.widthPixels, metrics.heightPixels}, imagePath);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                shareImageview.setImageBitmap(bitmap);
            } else {
                Toast.makeText(SaveShareImageActivity.this, getString(R.string.error_img_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void myClickHandler(View view) {
        int id = view.getId();
        if (id == R.id.instagramShare) {
            try {
                Uri uriImage = FileProvider.getUriForFile(SaveShareImageActivity.this,
                        getString(R.string.file_provider_authority), new File(imagePath));
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uriImage);
                intent.setPackage("com.instagram.android");
                startActivity(intent);
            } catch (Exception i) {
                Toast.makeText(this, getString(R.string.no_instagram_app), Toast.LENGTH_SHORT).show();
            }


        }
        if (id == R.id.share_imageView) {
            Toast.makeText(this, getString(R.string.saved_image_message), Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.whatsup_share) {
            try {
                Uri uriImage = FileProvider.getUriForFile(SaveShareImageActivity.this,
                        getString(R.string.file_provider_authority), new File(imagePath));

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uriImage);
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            } catch (Exception i) {
                Toast.makeText(this, getString(R.string.no_whatsapp_app), Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.facebook_share) {
            initShareIntent("face");
        }
        if (id == R.id.more) {
            Uri uriImage = FileProvider.getUriForFile(SaveShareImageActivity.this,
                    getString(R.string.file_provider_authority), new File(imagePath));

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uriImage);
            startActivity(sharingIntent);
        }
    }

    private void initShareIntent(String type) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/jpeg");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
                share, 0);
        if (!resInfo.isEmpty()) {
            // FilePath = getImagePath();

            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type)
                        || info.activityInfo.name.toLowerCase().contains(type)) {

                    Uri uriImage = FileProvider.getUriForFile(SaveShareImageActivity.this,
                            getString(R.string.file_provider_authority), new File(imagePath));

                    share.putExtra(Intent.EXTRA_SUBJECT, "Created With #Photo Collage Editor App");
                    share.putExtra(Intent.EXTRA_TEXT, "Created With #Photo Collage Editor App");
                    share.putExtra(Intent.EXTRA_STREAM, uriImage);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, getString(R.string.no_facebook_app), Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(Intent.createChooser(share, "Select"));
        }
    }
}
