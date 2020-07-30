package com.athings.photoeditor.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.athings.photoeditor.fragments.WriteTextFragment;
import com.athings.photoeditor.utils.Constants;
import com.athings.photoeditor.utils.MirrorImageMode;
import com.athings.photoeditor.R;
import com.athings.photoeditor.adapter.MyRecyclerViewAdapter;
import com.athings.photoeditor.bitmap.BitmapResizer;
import com.athings.photoeditor.canvastextview.ApplyTextInterface;
import com.athings.photoeditor.canvastextview.CustomRelativeLayout;
import com.athings.photoeditor.canvastextview.SingleTapInterface;
import com.athings.photoeditor.canvastextview.TextDataItem;
import com.athings.photoeditor.utils.Utils;
import com.athings.photoeditor.fragments.EffectFragment;
import com.athings.photoeditor.utils.LibUtility;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MirrorImageActivity extends AppCompatActivity {

    public static final int INDEX_MIRROR = 0;
    public static final int INDEX_MIRROR_3D = 1;
    public static final int INDEX_MIRROR_ADJUSTMENT = 5;
    public static final int INDEX_MIRROR_EFFECT = 3;
    public static final int INDEX_MIRROR_INVISIBLE_VIEW = 7;
    public static final int INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX = 4;
    public static final int INDEX_MIRROR_RATIO = 2;
    public static final int TAB_SIZE = 6;
    private static final String TAG = "MirrorImageActivity";
    int D3_BUTTON_SIZE = 24;
    int MIRROR_BUTTON_SIZE = 15;
    int RATIO_BUTTON_SIZE = 11;
    CustomRelativeLayout customRelativeLayout;
    int currentSelectedTabIndex = -1;
    ImageView[] d3ButtonArray;
    EffectFragment effectFragment;
    Bitmap filterBitmap;
    WriteTextFragment writeTextFragment;
    int initialYPos = 0;
    RelativeLayout mainLayout;
    ImageView[] mirrorButtonArray;
    MirrorView mirrorView;
    float mulX = 16;
    float mulY = 16;
    Button[] ratioButtonArray;
    AlertDialog saveImageAlert;
    int screenHeightPixels;
    int screenWidthPixels;
    boolean showText = false;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    Bitmap sourceBitmap;
    View[] tabButtonList;
    ArrayList<TextDataItem> textDataList = new ArrayList<>();
    ViewFlipper viewFlipper;
    Matrix matrix1 = new Matrix();
    Matrix matrix2 = new Matrix();
    Matrix matrix3 = new Matrix();
    Matrix matrix4 = new Matrix();
    AdView mAdView;
    com.facebook.ads.AdView adView;
    com.facebook.ads.InterstitialAd interstitialAd;
    InterstitialAd mInterstitialAd;
    private int[] d3resList = new int[]{R.drawable.mirror_3d_14, R.drawable.mirror_3d_14, R.drawable.mirror_3d_10, R.drawable.mirror_3d_10, R.drawable.mirror_3d_11, R.drawable.mirror_3d_11, R.drawable.mirror_3d_4, R.drawable.mirror_3d_4, R.drawable.mirror_3d_3, R.drawable.mirror_3d_3, R.drawable.mirror_3d_1, R.drawable.mirror_3d_1, R.drawable.mirror_3d_6, R.drawable.mirror_3d_6, R.drawable.mirror_3d_13, R.drawable.mirror_3d_13, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16};

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        Bundle extras = getIntent().getExtras();
        sourceBitmap = BitmapResizer.decodeBitmapFromFile(extras.getString("selectedImagePath"), extras.getInt("MAX_SIZE"));
        if (sourceBitmap == null) {
            Toast msg = Toast.makeText(MirrorImageActivity.this, "Could not load the photo, please use another GALLERY app!", INDEX_MIRROR_3D);
            msg.setGravity(17, msg.getXOffset() / INDEX_MIRROR_RATIO, msg.getYOffset() / INDEX_MIRROR_RATIO);
            msg.show();
            finish();
            return;
        }
        int width;
        int height;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeightPixels = metrics.heightPixels;
        screenWidthPixels = metrics.widthPixels;
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        if (screenWidthPixels <= 0) {
            screenWidthPixels = width;
        }
        if (screenHeightPixels <= 0) {
            screenHeightPixels = height;
        }
        mirrorView = new MirrorView(MirrorImageActivity.this, screenWidthPixels, screenHeightPixels);
        setContentView(R.layout.mirror_image_activity);

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

        mainLayout = (RelativeLayout) findViewById(R.id.layout_mirror_activity);
        mainLayout.addView(mirrorView);
        viewFlipper = (ViewFlipper) findViewById(R.id.mirror_view_flipper);
        viewFlipper.bringToFront();
        findViewById(R.id.mirror_footer).bringToFront();
        slideLeftIn = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_in_left);
        slideLeftOut = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_out_left);
        slideRightIn = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_in_right);
        slideRightOut = AnimationUtils.loadAnimation(MirrorImageActivity.this, R.anim.slide_out_right);
//        if (CommonLibrary.isAppPro(this.context)) {
//            this.adWhirlLayout = (AdView) findViewById(R.id.mirror_edit_ad_id);
//            this.adWhirlLayout.setVisibility(8);
//        } else {
//            this.adWhirlLayout = (AdView) findViewById(R.id.mirror_edit_ad_id);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            this.adWhirlLayout.loadAd(adRequest);
//            this.adWhirlLayout.bringToFront();
//            if (this.context.getResources().getBoolean(R.bool.showInterstitialAds)) {
//                this.interstitial = new InterstitialAd(this.context);
//                this.interstitial.setAdUnitId(getString(R.string.interstital_ad_id));
//                AdRequest req = new AdRequest.Builder().build();
//                this.interstitial.loadAd(req);
//            }
//        }
        findViewById(R.id.mirror_header).bringToFront();
        findViewById(R.id.linearAds).bringToFront();
        addEffectFragment();
        setSelectedTab(0);
    }

    WriteTextFragment.FontChoosedListener fontChoosedListener = new WriteTextFragment.FontChoosedListener() {
        @Override
        public void onOk(TextDataItem textData) {
            customRelativeLayout.addTextView(textData);
            getSupportFragmentManager().beginTransaction().remove(writeTextFragment).commit();
        }
    };

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
                mAdView = new AdView(MirrorImageActivity.this);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(Constants.ADS_ADMOB_BANNER_ID);
              // AdRequest adRequest = new AdRequest.Builder().addTestDevice("53A509AC02225B8FEF6B9787D326F76F").build();
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
              //  AdSettings.addTestDevice("e6976e2040b914dcfc9ad146b3cf9731");
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

    void addEffectFragment() {
        if (effectFragment == null) {
            effectFragment = (EffectFragment) getSupportFragmentManager().findFragmentByTag("MY_EFFECT_FRAGMENT");
            if (effectFragment == null) {
                effectFragment = new EffectFragment();
                Log.e(TAG, "EffectFragment == null");
                effectFragment.setBitmap(sourceBitmap);
                effectFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.mirror_effect_fragment_container, effectFragment, "MY_EFFECT_FRAGMENT").commit();
            } else {
                effectFragment.setBitmap(sourceBitmap);
                effectFragment.setSelectedTabIndex(INDEX_MIRROR);
            }
            effectFragment.setBitmapReadyListener(new EffectFragment.BitmapReady() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    filterBitmap = bitmap;
                    mirrorView.postInvalidate();
                }
            });
            effectFragment.setBorderIndexChangedListener(new MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener() {
                @Override
                public void onIndexChanged(int i) {
                    mirrorView.setFrame(i);
                }
            });
        }
    }

    final class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {
        private MediaScannerConnection mConn;
        private String mFilename;
        private String mMimetype;

        public MyMediaScannerConnectionClient(Context ctx, File file, String mimetype) {
            this.mFilename = file.getAbsolutePath();
            this.mConn = new MediaScannerConnection(ctx, this);
            this.mConn.connect();
        }

        public void onMediaScannerConnected() {
            this.mConn.scanFile(this.mFilename, this.mMimetype);
        }

        public void onScanCompleted(String path, Uri uri) {
            this.mConn.disconnect();
        }
    }

    private class SaveImageTask extends AsyncTask<Object, Object, Object> {
        ProgressDialog progressDialog;
        String resultPath;

        private SaveImageTask() {
            this.resultPath = null;
        }

        protected Object doInBackground(Object... arg0) {
            this.resultPath = mirrorView.saveBitmap(true, screenWidthPixels, screenHeightPixels);
            return null;
        }

        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(MirrorImageActivity.this);
            this.progressDialog.setMessage("Saving image ...");
            this.progressDialog.show();
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.cancel();
            }
            if (this.resultPath != null) {
                Intent intent = new Intent(MirrorImageActivity.this, SaveShareImageActivity.class);
                intent.putExtra("imagePath", this.resultPath);
                startActivity(intent);
            }

            MyMediaScannerConnectionClient myMediaScannerConnectionClient = new MyMediaScannerConnectionClient(MirrorImageActivity.this.getApplicationContext(), new File(this.resultPath), null);
        }
    }

    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
        }
        if (filterBitmap != null) {
            filterBitmap.recycle();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("show_text", this.showText);
        savedInstanceState.putSerializable("text_data", this.textDataList);
        if (writeTextFragment != null && writeTextFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(writeTextFragment).commit();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showText = savedInstanceState.getBoolean("show_text");
        textDataList = (ArrayList) savedInstanceState.getSerializable("text_data");
        if (textDataList == null) {
            textDataList = new ArrayList<>();
        }
    }

    public void myClickHandler(View view) {
        int id = view.getId();
        this.mirrorView.drawSavedImage = false;
        if (id == R.id.button_save_mirror_image) {
            new SaveImageTask().execute();
        } else if (id == R.id.closeScreen) {
            backButtonAlertBuilder();
        } else if (id == R.id.button_mirror) {
            setSelectedTab(INDEX_MIRROR);
        } else if (id == R.id.button_mirror_frame) {
            setSelectedTab(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
        } else if (id == R.id.button_mirror_ratio) {
            setSelectedTab(INDEX_MIRROR_RATIO);
        } else if (id == R.id.button_mirror_effect) {
            setSelectedTab(INDEX_MIRROR_EFFECT);
        } else if (id == R.id.button_mirror_adj) {
            setSelectedTab(INDEX_MIRROR_ADJUSTMENT);
        } else if (id == R.id.button_mirror_3d) {
            setSelectedTab(INDEX_MIRROR_3D);
        } else if (id == R.id.button_3d_1) {
            set3dMode(INDEX_MIRROR);
        } else if (id == R.id.button_3d_2) {
            set3dMode(INDEX_MIRROR_3D);
        } else if (id == R.id.button_3d_3) {
            set3dMode(INDEX_MIRROR_RATIO);
        } else if (id == R.id.button_3d_4) {
            set3dMode(INDEX_MIRROR_EFFECT);
        } else if (id == R.id.button_3d_5) {
            set3dMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
        } else if (id == R.id.button_3d_6) {
            set3dMode(INDEX_MIRROR_ADJUSTMENT);
        } else if (id == R.id.button_3d_7) {
            set3dMode(TAB_SIZE);
        } else if (id == R.id.button_3d_8) {
            set3dMode(INDEX_MIRROR_INVISIBLE_VIEW);
        } else if (id == R.id.button_3d_9) {
            set3dMode(8);
        } else if (id == R.id.button_3d_10) {
            set3dMode(9);
        } else if (id == R.id.button_3d_11) {
            set3dMode(10);
        } else if (id == R.id.button_3d_12) {
            set3dMode(11);
        } else if (id == R.id.button_3d_13) {
            set3dMode(12);
        } else if (id == R.id.button_3d_14) {
            set3dMode(13);
        } else if (id == R.id.button_3d_15) {
            set3dMode(14);
        } else if (id == R.id.button_3d_16) {
            set3dMode(15);
        } else if (id == R.id.button_3d_17) {
            set3dMode(16);
        } else if (id == R.id.button_3d_18) {
            set3dMode(17);
        } else if (id == R.id.button_3d_19) {
            set3dMode(18);
        } else if (id == R.id.button_3d_20) {
            set3dMode(19);
        } else if (id == R.id.button_3d_21) {
            set3dMode(20);
        } else if (id == R.id.button_3d_22) {
            set3dMode(21);
        } else if (id == R.id.button_3d_23) {
            set3dMode(22);
        } else if (id == R.id.button_3d_24) {
            set3dMode(23);
        } else if (id == R.id.button11) {
            this.mulX = 1.0f;
            this.mulY = 1.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR);
        } else if (id == R.id.button21) {
            this.mulX = 2.0f;
            this.mulY = 1.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR_3D);
        } else if (id == R.id.button12) {
            this.mulX = 1.0f;
            this.mulY = 2.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR_RATIO);
        } else if (id == R.id.button32) {
            this.mulX = 3.0f;
            this.mulY = 2.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR_EFFECT);
        } else if (id == R.id.button23) {
            this.mulX = 2.0f;
            this.mulY = 3.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
        } else if (id == R.id.button43) {
            this.mulX = 4.0f;
            this.mulY = 3.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR_ADJUSTMENT);
        } else if (id == R.id.button34) {
            this.mulX = 3.0f;
            this.mulY = 4.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(TAB_SIZE);
        } else if (id == R.id.button45) {
            this.mulX = 4.0f;
            this.mulY = 5.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(INDEX_MIRROR_INVISIBLE_VIEW);
        } else if (id == R.id.button57) {
            this.mulX = 5.0f;
            this.mulY = 7.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(8);
        } else if (id == R.id.button169) {
            this.mulX = 16.0f;
            this.mulY = 9.0f;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setRatioButtonBg(9);
        } else if (id == R.id.button916) {
            this.mulX = 9.0f;
            this.mulY = 16.0f;
            this.mirrorView.reset(screenWidthPixels, screenHeightPixels, true);
            setRatioButtonBg(10);
        } else if (id == R.id.button_m1) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR);
        } else if (id == R.id.button_m2) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR_3D);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR_3D);
        } else if (id == R.id.button_m3) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR_RATIO);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR_RATIO);
        } else if (id == R.id.button_m4) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR_EFFECT);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR_EFFECT);
        } else if (id == R.id.button_m5) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
        } else if (id == R.id.button_m6) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR_ADJUSTMENT);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR_ADJUSTMENT);
        } else if (id == R.id.button_m7) {
            this.mirrorView.setCurrentMode(TAB_SIZE);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(TAB_SIZE);
        } else if (id == R.id.button_m8) {
            this.mirrorView.setCurrentMode(INDEX_MIRROR_INVISIBLE_VIEW);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(INDEX_MIRROR_INVISIBLE_VIEW);
        } else if (id == R.id.button_m9) {
            this.mirrorView.setCurrentMode(8);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(8);
        } else if (id == R.id.button_m10) {
            this.mirrorView.setCurrentMode(9);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(9);
        } else if (id == R.id.button_m11) {
            this.mirrorView.setCurrentMode(10);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(10);
        } else if (id == R.id.button_m12) {
            this.mirrorView.setCurrentMode(11);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(11);
        } else if (id == R.id.button_m13) {
            this.mirrorView.setCurrentMode(12);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(12);
        } else if (id == R.id.button_m14) {
            this.mirrorView.setCurrentMode(13);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(13);
        } else if (id == R.id.button_m15) {
            this.mirrorView.setCurrentMode(14);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(14);
        } else if (id == R.id.button_mirror_text) {
            addCanvasTextView();
            clearViewFlipper();
        } else {
            this.effectFragment.myClickHandler(id);
            if (id == R.id.buttonCancel || id == R.id.buttonOk) {
                clearFxAndFrame();
            }
        }
    }

    private void clearFxAndFrame() {
        int selectedTabIndex = this.effectFragment.getSelectedTabIndex();
        if (this.currentSelectedTabIndex != INDEX_MIRROR_EFFECT && this.currentSelectedTabIndex != INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX) {
            return;
        }
        if (selectedTabIndex == 0 || selectedTabIndex == INDEX_MIRROR_3D) {
            clearViewFlipper();
        }
    }

    void addCanvasTextView() {
        customRelativeLayout = new CustomRelativeLayout(MirrorImageActivity.this, textDataList, mirrorView.f510I, (SingleTapInterface) new SingleTapInterface() {

            @Override
            public void onSingleTap(TextDataItem textData) {
                writeTextFragment = new WriteTextFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("text_data", textData);
                writeTextFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.text_view_fragment_container, writeTextFragment, "FONT_FRAGMENT").commit();
                Log.e(MirrorImageActivity.TAG, "replace fragment");
                writeTextFragment.setFontChoosedListener(fontChoosedListener);
            }
        });
        customRelativeLayout.setApplyTextListener(new ApplyTextInterface() {
            @Override
            public void onCancel() {
                showText = true;
                mainLayout.removeView(customRelativeLayout);
                mirrorView.postInvalidate();
            }

            @Override
            public void onOk(ArrayList<TextDataItem> arrayList) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((TextDataItem) it.next()).setImageSaveMatrix(mirrorView.f510I);
                }
                textDataList = arrayList;
                showText = true;
                if (mainLayout == null) {
                    mainLayout = (RelativeLayout) findViewById(R.id.layout_mirror_activity);
                }
                mainLayout.removeView(customRelativeLayout);
                mirrorView.postInvalidate();
            }
        });
        showText = false;
        mirrorView.invalidate();
        mainLayout.addView(customRelativeLayout);
        findViewById(R.id.text_view_fragment_container).bringToFront();
        writeTextFragment = new WriteTextFragment();
        writeTextFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction().add(R.id.text_view_fragment_container, writeTextFragment, "FONT_FRAGMENT").commit();
        Log.e(TAG, "add fragment");
        writeTextFragment.setFontChoosedListener(this.fontChoosedListener);
    }

    private void set3dMode(int index) {
        mirrorView.d3Mode = true;
        if (index > 15 && index < 20) {
            mirrorView.setCurrentMode(index);
        } else if (index > 19) {
            mirrorView.setCurrentMode(index - 4);
        } else if (index % INDEX_MIRROR_RATIO == 0) {
            mirrorView.setCurrentMode(INDEX_MIRROR);
        } else {
            mirrorView.setCurrentMode(INDEX_MIRROR_3D);
        }
        this.mirrorView.reset(screenWidthPixels, screenHeightPixels, false);
        if (Build.VERSION.SDK_INT < 11) {
            if (!(mirrorView.d3Bitmap == null || mirrorView.d3Bitmap.isRecycled())) {
                mirrorView.d3Bitmap.recycle();
            }
            mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), this.d3resList[index]);
        } else {
            loadInBitmap(this.d3resList[index]);
        }
        mirrorView.postInvalidate();
        setD3ButtonBg(index);
    }

    @SuppressLint({"NewApi"})
    private void loadInBitmap(int resId) {
        Log.e(TAG, "loadInBitmap");
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (mirrorView.d3Bitmap == null || mirrorView.d3Bitmap.isRecycled()) {
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            BitmapFactory.decodeResource(getResources(), resId, options);
            mirrorView.d3Bitmap = Bitmap.createBitmap(options.outWidth, options.outHeight, Bitmap.Config.ARGB_8888);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = INDEX_MIRROR_3D;
        options.inBitmap = mirrorView.d3Bitmap;
        try {
            mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            if (!(mirrorView.d3Bitmap == null || mirrorView.d3Bitmap.isRecycled())) {
                mirrorView.d3Bitmap.recycle();
            }
            mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), resId);
        }
    }

    private void setD3ButtonBg(int index) {
        if (d3ButtonArray == null) {
            d3ButtonArray = new ImageView[D3_BUTTON_SIZE];
            d3ButtonArray[0] = (ImageView) findViewById(R.id.button_3d_1);
            d3ButtonArray[1] = (ImageView) findViewById(R.id.button_3d_2);
            d3ButtonArray[2] = (ImageView) findViewById(R.id.button_3d_3);
            d3ButtonArray[3] = (ImageView) findViewById(R.id.button_3d_4);
            d3ButtonArray[4] = (ImageView) findViewById(R.id.button_3d_5);
            d3ButtonArray[5] = (ImageView) findViewById(R.id.button_3d_6);
            d3ButtonArray[6] = (ImageView) findViewById(R.id.button_3d_7);
            d3ButtonArray[7] = (ImageView) findViewById(R.id.button_3d_8);
            d3ButtonArray[8] = (ImageView) findViewById(R.id.button_3d_9);
            d3ButtonArray[9] = (ImageView) findViewById(R.id.button_3d_10);
            d3ButtonArray[10] = (ImageView) findViewById(R.id.button_3d_11);
            d3ButtonArray[11] = (ImageView) findViewById(R.id.button_3d_12);
            d3ButtonArray[12] = (ImageView) findViewById(R.id.button_3d_13);
            d3ButtonArray[13] = (ImageView) findViewById(R.id.button_3d_14);
            d3ButtonArray[14] = (ImageView) findViewById(R.id.button_3d_15);
            d3ButtonArray[15] = (ImageView) findViewById(R.id.button_3d_16);
            d3ButtonArray[16] = (ImageView) findViewById(R.id.button_3d_17);
            d3ButtonArray[17] = (ImageView) findViewById(R.id.button_3d_18);
            d3ButtonArray[18] = (ImageView) findViewById(R.id.button_3d_19);
            d3ButtonArray[19] = (ImageView) findViewById(R.id.button_3d_20);
            d3ButtonArray[20] = (ImageView) findViewById(R.id.button_3d_21);
            d3ButtonArray[21] = (ImageView) findViewById(R.id.button_3d_22);
            d3ButtonArray[22] = (ImageView) findViewById(R.id.button_3d_23);
            d3ButtonArray[23] = (ImageView) findViewById(R.id.button_3d_24);
        }
        for (int i = 0; i < D3_BUTTON_SIZE; i++) {
            d3ButtonArray[i].setBackgroundColor(getResources().getColor(R.color.primary));
        }
        d3ButtonArray[index].setBackgroundColor(getResources().getColor(R.color.footer_button_color_pressed));
    }

    private void setMirrorButtonBg(int index) {
        if (mirrorButtonArray == null) {
            mirrorButtonArray = new ImageView[MIRROR_BUTTON_SIZE];
            mirrorButtonArray[0] = (ImageView) findViewById(R.id.button_m1);
            mirrorButtonArray[1] = (ImageView) findViewById(R.id.button_m2);
            mirrorButtonArray[2] = (ImageView) findViewById(R.id.button_m3);
            mirrorButtonArray[3] = (ImageView) findViewById(R.id.button_m4);
            mirrorButtonArray[4] = (ImageView) findViewById(R.id.button_m5);
            mirrorButtonArray[5] = (ImageView) findViewById(R.id.button_m6);
            mirrorButtonArray[6] = (ImageView) findViewById(R.id.button_m7);
            mirrorButtonArray[7] = (ImageView) findViewById(R.id.button_m8);
            mirrorButtonArray[8] = (ImageView) findViewById(R.id.button_m9);
            mirrorButtonArray[9] = (ImageView) findViewById(R.id.button_m10);
            mirrorButtonArray[10] = (ImageView) findViewById(R.id.button_m11);
            mirrorButtonArray[11] = (ImageView) findViewById(R.id.button_m12);
            mirrorButtonArray[12] = (ImageView) findViewById(R.id.button_m13);
            mirrorButtonArray[13] = (ImageView) findViewById(R.id.button_m14);
            mirrorButtonArray[14] = (ImageView) findViewById(R.id.button_m15);
        }
        for (int i = 0; i < MIRROR_BUTTON_SIZE; i += INDEX_MIRROR_3D) {
            mirrorButtonArray[i].setBackgroundResource(R.color.primary);
        }
        mirrorButtonArray[index].setBackgroundResource(R.color.footer_button_color_pressed);
    }

    private void setRatioButtonBg(int index) {
        if (ratioButtonArray == null) {
            ratioButtonArray = new Button[RATIO_BUTTON_SIZE];
            ratioButtonArray[0] = (Button) findViewById(R.id.button11);
            ratioButtonArray[1] = (Button) findViewById(R.id.button21);
            ratioButtonArray[2] = (Button) findViewById(R.id.button12);
            ratioButtonArray[3] = (Button) findViewById(R.id.button32);
            ratioButtonArray[4] = (Button) findViewById(R.id.button23);
            ratioButtonArray[5] = (Button) findViewById(R.id.button43);
            ratioButtonArray[6] = (Button) findViewById(R.id.button34);
            ratioButtonArray[7] = (Button) findViewById(R.id.button45);
            ratioButtonArray[8] = (Button) findViewById(R.id.button57);
            ratioButtonArray[9] = (Button) findViewById(R.id.button169);
            ratioButtonArray[10] = (Button) findViewById(R.id.button916);
        }
        for (int i = 0; i < RATIO_BUTTON_SIZE; i += INDEX_MIRROR_3D) {
            ratioButtonArray[i].setBackgroundResource(R.drawable.selector_collage_ratio_button);
        }
        ratioButtonArray[index].setBackgroundResource(R.drawable.collage_ratio_bg_pressed);
    }

    void setSelectedTab(int index) {
        setTabBg(INDEX_MIRROR);
        int displayedChild = viewFlipper.getDisplayedChild();
        if (index == 0) {
            if (displayedChild != 0) {
                viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideRightOut);
                viewFlipper.setDisplayedChild(INDEX_MIRROR);
            } else {
                return;
            }
        }
        if (index == INDEX_MIRROR_3D) {
            setTabBg(INDEX_MIRROR_3D);
            if (displayedChild != INDEX_MIRROR_3D) {
                if (displayedChild == 0) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                viewFlipper.setDisplayedChild(INDEX_MIRROR_3D);
            } else {
                return;
            }
        }
        if (index == INDEX_MIRROR_RATIO) {
            setTabBg(INDEX_MIRROR_RATIO);
            if (displayedChild != INDEX_MIRROR_RATIO) {
                if (displayedChild == 0) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                this.viewFlipper.setDisplayedChild(INDEX_MIRROR_RATIO);
            } else {
                return;
            }
        }
        if (index == INDEX_MIRROR_EFFECT) {
            setTabBg(INDEX_MIRROR_EFFECT);
            effectFragment.setSelectedTabIndex(INDEX_MIRROR);
            if (displayedChild != INDEX_MIRROR_EFFECT) {
                if (displayedChild == 0 || displayedChild == INDEX_MIRROR_RATIO) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                viewFlipper.setDisplayedChild(INDEX_MIRROR_EFFECT);
            } else {
                return;
            }
        }
        if (index == INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX) {
            setTabBg(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
            effectFragment.setSelectedTabIndex(INDEX_MIRROR_3D);
            if (displayedChild != INDEX_MIRROR_EFFECT) {
                if (displayedChild == INDEX_MIRROR_ADJUSTMENT) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                } else {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                }
                viewFlipper.setDisplayedChild(INDEX_MIRROR_EFFECT);
            } else {
                return;
            }
        }
        if (index == INDEX_MIRROR_ADJUSTMENT) {
            setTabBg(INDEX_MIRROR_ADJUSTMENT);
            effectFragment.showToolBar();
            if (displayedChild != INDEX_MIRROR_EFFECT) {
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.setDisplayedChild(INDEX_MIRROR_EFFECT);
            } else {
                return;
            }
        }
        if (index == INDEX_MIRROR_INVISIBLE_VIEW) {
            setTabBg(-1);
            if (displayedChild != INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX) {
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.setDisplayedChild(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
            }
        }
    }

    private void setTabBg(int index) {
        currentSelectedTabIndex = index;
        if (tabButtonList == null) {
            tabButtonList = new View[TAB_SIZE];
            tabButtonList[INDEX_MIRROR] = findViewById(R.id.button_mirror);
            tabButtonList[INDEX_MIRROR_3D] = findViewById(R.id.button_mirror_3d);
            tabButtonList[INDEX_MIRROR_EFFECT] = findViewById(R.id.button_mirror_effect);
            tabButtonList[INDEX_MIRROR_RATIO] = findViewById(R.id.button_mirror_ratio);
            tabButtonList[INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX] = findViewById(R.id.button_mirror_frame);
            tabButtonList[INDEX_MIRROR_ADJUSTMENT] = findViewById(R.id.button_mirror_adj);
        }
        for (int i = INDEX_MIRROR; i < tabButtonList.length; i += INDEX_MIRROR_3D) {
            tabButtonList[i].setBackgroundResource(R.drawable.collage_footer_button);
        }
        if (index >= 0) {
            tabButtonList[index].setBackgroundResource(R.color.footer_button_color_pressed);
        }
    }

    void clearViewFlipper() {
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
        viewFlipper.setDisplayedChild(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX);
        setTabBg(-1);
    }

    public void onBackPressed() {
        if (writeTextFragment != null && writeTextFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(writeTextFragment).commit();
        } else if (viewFlipper.getDisplayedChild() == INDEX_MIRROR_EFFECT) {
            clearFxAndFrame();
            clearViewFlipper();
        } else if (!showText && customRelativeLayout != null) {
            showText = true;
            mainLayout.removeView(customRelativeLayout);
            mirrorView.postInvalidate();
            customRelativeLayout = null;
            Log.e(TAG, "replace fragment");
        } else if (viewFlipper.getDisplayedChild() != INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX) {
            clearViewFlipper();
        } else {
            backButtonAlertBuilder();
        }
    }

    private void backButtonAlertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MirrorImageActivity.this);
        builder.setMessage("Would you like to save image ?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SaveImageTask().execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                if (Constants.ADS_STATUS) {
                    if (Constants.ADS_TYPE.equals("admob")) {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    } else if (Constants.ADS_TYPE.equals("facebook")) {
                        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                            interstitialAd.show();
                        }
                    }
                }
            }
        });
        saveImageAlert = builder.create();
        saveImageAlert.show();
    }

    class MirrorView extends View {
        final Matrix f510I;
        int currentModeIndex;
        Bitmap d3Bitmap;
        boolean d3Mode;
        int defaultColor;
        RectF destRect1;
        RectF destRect1X;
        RectF destRect1Y;
        RectF destRect2;
        RectF destRect2X;
        RectF destRect2Y;
        RectF destRect3;
        RectF destRect4;
        boolean drawSavedImage;
        RectF dstRectPaper1;
        RectF dstRectPaper2;
        RectF dstRectPaper3;
        RectF dstRectPaper4;
        Bitmap frameBitmap;
        Paint framePaint;
        int height;
        boolean isTouchStartedLeft;
        boolean isTouchStartedTop;
        boolean isVerticle;
        Matrix m1;
        Matrix m2;
        Matrix m3;
        MirrorImageMode[] mirrorModeList;
        MirrorImageMode modeX;
        MirrorImageMode modeX10;
        MirrorImageMode modeX11;
        MirrorImageMode modeX12;
        MirrorImageMode modeX13;
        MirrorImageMode modeX14;
        MirrorImageMode modeX15;
        MirrorImageMode modeX16;
        MirrorImageMode modeX17;
        MirrorImageMode modeX18;
        MirrorImageMode modeX19;
        MirrorImageMode modeX2;
        MirrorImageMode modeX20;
        MirrorImageMode modeX3;
        MirrorImageMode modeX4;
        MirrorImageMode modeX5;
        MirrorImageMode modeX6;
        MirrorImageMode modeX7;
        MirrorImageMode modeX8;
        MirrorImageMode modeX9;
        float oldX;
        float oldY;
        RectF srcRect1;
        RectF srcRect2;
        RectF srcRect3;
        RectF srcRectPaper;
        int tMode1;
        int tMode2;
        int tMode3;
        Matrix textMatrix;
        Paint textRectPaint;
        RectF totalArea1;
        RectF totalArea2;
        RectF totalArea3;
        int width;

        public MirrorView(Context context, int screenWidth, int screenHeight) {
            super(context);
            f510I = new Matrix();
            framePaint = new Paint();
            isVerticle = false;
            defaultColor = R.color.bg;
            mirrorModeList = new MirrorImageMode[20];
            currentModeIndex = INDEX_MIRROR;
            drawSavedImage = false;
            d3Mode = false;
            textMatrix = new Matrix();
            textRectPaint = new Paint(INDEX_MIRROR_3D);
            m1 = new Matrix();
            m2 = new Matrix();
            m3 = new Matrix();
            width = sourceBitmap.getWidth();
            height = sourceBitmap.getHeight();
            int widthPixels = screenWidth;
            int heightPixels = screenHeight;
            createMatrix(widthPixels, heightPixels);
            createRectX(widthPixels, heightPixels);
            createRectY(widthPixels, heightPixels);
            createRectXY(widthPixels, heightPixels);
            createModes();
            framePaint.setAntiAlias(true);
            framePaint.setFilterBitmap(true);
            framePaint.setDither(true);
            textRectPaint.setColor(getResources().getColor(R.color.bg));
        }

        private void reset(int widthPixels, int heightPixels, boolean invalidate) {
            createMatrix(widthPixels, heightPixels);
            createRectX(widthPixels, heightPixels);
            createRectY(widthPixels, heightPixels);
            createRectXY(widthPixels, heightPixels);
            createModes();
            if (invalidate) {
                postInvalidate();
            }
        }

        private String saveBitmap(boolean saveToFile, int widthPixel, int heightPixel) {
            int i;
            float minDimen = (float) Math.min(widthPixel, heightPixel);
            float upperScale = (float) Utils.maxSizeForSave();
            float scale = upperScale / minDimen;
            Log.e(MirrorImageActivity.TAG, "upperScale" + upperScale);
            Log.e(MirrorImageActivity.TAG, "scale" + scale);
            if (mulY > mulX) {
                float f = mulX;
                float r0 = 1.0f;
                scale = (r0 * scale) / mulY;
            }
            if (scale <= 0.0f) {
                scale = 1.0f;
            }
            Log.e(MirrorImageActivity.TAG, "scale" + scale);
            int wP = Math.round(((float) widthPixel) * scale);
            int wH = Math.round(((float) heightPixel) * scale);
            RectF srcRect = mirrorModeList[currentModeIndex].getSrcRect();
            reset(wP, wH, false);
            int btmWidth = Math.round(mirrorView.getCurrentMirrorMode().rectTotalArea.width());
            int btmHeight = Math.round(mirrorView.getCurrentMirrorMode().rectTotalArea.height());
            if (btmWidth % MirrorImageActivity.INDEX_MIRROR_RATIO == MirrorImageActivity.INDEX_MIRROR_3D) {
                btmWidth--;
            }
            if (btmHeight % MirrorImageActivity.INDEX_MIRROR_RATIO == MirrorImageActivity.INDEX_MIRROR_3D) {
                btmHeight--;
            }
            Bitmap savedBitmap = Bitmap.createBitmap(btmWidth, btmHeight, Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(savedBitmap);
            Matrix matrix = new Matrix();
            matrix.reset();
            Log.e(MirrorImageActivity.TAG, "btmWidth " + btmWidth);
            Log.e(MirrorImageActivity.TAG, "btmHeight " + btmHeight);
            matrix.postTranslate(((float) (-(wP - btmWidth))) / 2.0f, ((float) (-(wH - btmHeight))) / 2.0f);
            MirrorImageMode saveMode = mirrorModeList[currentModeIndex];
            saveMode.setSrcRect(srcRect);
            if (MirrorImageActivity.this.filterBitmap == null) {
                drawMode(bitmapCanvas, sourceBitmap, saveMode, matrix);
            } else {
                drawMode(bitmapCanvas, filterBitmap, saveMode, matrix);
            }
            if (d3Mode && this.d3Bitmap != null) {
                if (!d3Bitmap.isRecycled()) {
                    bitmapCanvas.setMatrix(matrix);
                    bitmapCanvas.drawBitmap(d3Bitmap, null, mirrorModeList[currentModeIndex].rectTotalArea, framePaint);
                }
            }
            Matrix mat;
            if (MirrorImageActivity.this.textDataList != null) {
                i = MirrorImageActivity.INDEX_MIRROR;
                while (true) {
                    if (i >= MirrorImageActivity.this.textDataList.size()) {
                        break;
                    }
                    mat = new Matrix();
                    mat.set(MirrorImageActivity.this.textDataList.get(i).imageSaveMatrix);
                    mat.postScale(scale, scale);
                    mat.postTranslate(((float) (-(wP - btmWidth))) / 2.0f, ((float) (-(wH - btmHeight))) / 2.0f);
                    bitmapCanvas.setMatrix(mat);
                    bitmapCanvas.drawText(textDataList.get(i).message, textDataList.get(i).xPos, textDataList.get(i).yPos, textDataList.get(i).textPaint);
                    i += MirrorImageActivity.INDEX_MIRROR_3D;
                }
            }
            if (frameBitmap != null) {
                if (!frameBitmap.isRecycled()) {
                    bitmapCanvas.setMatrix(matrix);
                    bitmapCanvas.drawBitmap(frameBitmap, null, mirrorModeList[currentModeIndex].rectTotalArea, framePaint);
                }
            }
            String resultPath = null;
            if (saveToFile) {
                String twitterUploadFile = String.valueOf(System.currentTimeMillis());
                resultPath = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append(MirrorImageActivity.this.getString(R.string.directory)).append(twitterUploadFile).append(".jpg").toString();
                new File(resultPath).getParentFile().mkdirs();
                try {
                    FileOutputStream out = new FileOutputStream(resultPath);
                    savedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            savedBitmap.recycle();
            reset(widthPixel, heightPixel, false);
            mirrorModeList[currentModeIndex].setSrcRect(srcRect);
            return resultPath;
        }

        private void setCurrentMode(int index) {
            currentModeIndex = index;
        }

        public MirrorImageMode getCurrentMirrorMode() {
            return mirrorModeList[currentModeIndex];
        }

        private void createModes() {
            modeX = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect1, destRect1, destRect3, destRect3, matrix1, f510I, matrix1, tMode3, totalArea3);
            modeX2 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect1, destRect4, destRect1, destRect4, matrix1, matrix1, f510I, tMode3, totalArea3);
            modeX3 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect3, destRect2, destRect3, destRect2, matrix1, matrix1, f510I, tMode3, totalArea3);
            modeX8 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect1, destRect1, destRect1, destRect1, matrix1, matrix2, matrix3, tMode3, totalArea3);
            int m9TouchMode = INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX;
            if (tMode3 == 0) {
                m9TouchMode = INDEX_MIRROR;
            }
            modeX9 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect2, destRect2, destRect2, destRect2, matrix1, matrix2, matrix3, m9TouchMode, totalArea3);
            int m10TouchMode = INDEX_MIRROR_EFFECT;
            if (tMode3 == INDEX_MIRROR_3D) {
                m10TouchMode = INDEX_MIRROR_3D;
            }
            modeX10 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect3, destRect3, destRect3, destRect3, matrix1, matrix2, matrix3, m10TouchMode, totalArea3);
            int m11TouchMode = INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX;
            if (tMode3 == 0) {
                m11TouchMode = INDEX_MIRROR_EFFECT;
            }
            modeX11 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRect3, destRect4, destRect4, destRect4, destRect4, matrix1, matrix2, matrix3, m11TouchMode, totalArea3);
            modeX4 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect1, destRect1X, destRect1X, matrix1, tMode1, totalArea1);
            int m5TouchMode = INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX;
            if (this.tMode1 == 0) {
                m5TouchMode = INDEX_MIRROR;
            } else if (tMode1 == INDEX_MIRROR_ADJUSTMENT) {
                m5TouchMode = INDEX_MIRROR_ADJUSTMENT;
            }
            modeX5 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect1, destRect2X, destRect2X, matrix1, m5TouchMode, totalArea1);
            modeX6 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect2, destRect1Y, destRect1Y, matrix2, tMode2, totalArea2);
            int m7TouchMode = INDEX_MIRROR_EFFECT;
            if (tMode2 == INDEX_MIRROR_3D) {
                m7TouchMode = INDEX_MIRROR_3D;
            } else if (tMode2 == TAB_SIZE) {
                m7TouchMode = TAB_SIZE;
            }
            modeX7 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect2, destRect2Y, destRect2Y, matrix2, m7TouchMode, totalArea2);
            modeX12 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect1, destRect1X, destRect2X, matrix4, tMode1, totalArea1);
            modeX13 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect2, destRect1Y, destRect2Y, matrix4, tMode2, totalArea2);
            modeX14 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect1, destRect1X, destRect1X, matrix3, tMode1, totalArea1);
            modeX15 = new MirrorImageMode(INDEX_MIRROR_RATIO, srcRect2, destRect1Y, destRect1Y, matrix3, tMode2, totalArea2);
            modeX16 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRectPaper, dstRectPaper1, dstRectPaper2, dstRectPaper3, dstRectPaper4, matrix1, matrix1, f510I, tMode1, totalArea1);
            modeX17 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRectPaper, dstRectPaper1, dstRectPaper3, dstRectPaper3, dstRectPaper1, f510I, matrix1, matrix1, tMode1, totalArea1);
            modeX18 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRectPaper, dstRectPaper2, dstRectPaper4, dstRectPaper2, dstRectPaper4, f510I, matrix1, matrix1, tMode1, totalArea1);
            modeX19 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRectPaper, dstRectPaper1, dstRectPaper2, dstRectPaper2, dstRectPaper1, f510I, matrix1, matrix1, tMode1, totalArea1);
            modeX20 = new MirrorImageMode(INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX, srcRectPaper, dstRectPaper4, dstRectPaper3, dstRectPaper3, dstRectPaper4, f510I, matrix1, matrix1, tMode1, totalArea1);
            mirrorModeList[INDEX_MIRROR] = modeX4;
            mirrorModeList[INDEX_MIRROR_3D] = modeX5;
            mirrorModeList[INDEX_MIRROR_RATIO] = modeX6;
            mirrorModeList[INDEX_MIRROR_EFFECT] = modeX7;
            mirrorModeList[INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX] = modeX8;
            mirrorModeList[INDEX_MIRROR_ADJUSTMENT] = modeX9;
            mirrorModeList[TAB_SIZE] = modeX10;
            mirrorModeList[INDEX_MIRROR_INVISIBLE_VIEW] = modeX11;
            mirrorModeList[8] = modeX12;
            mirrorModeList[9] = modeX13;
            mirrorModeList[10] = modeX14;
            mirrorModeList[11] = modeX15;
            mirrorModeList[12] = modeX;
            mirrorModeList[13] = modeX2;
            mirrorModeList[14] = modeX3;
            mirrorModeList[15] = modeX7;
            mirrorModeList[16] = modeX17;
            mirrorModeList[17] = modeX18;
            mirrorModeList[18] = modeX19;
            mirrorModeList[19] = modeX20;
        }

        public Bitmap getBitmap() {
            setDrawingCacheEnabled(true);
            buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(getDrawingCache());
            setDrawingCacheEnabled(false);
            return bmp;
        }

        public void setFrame(int index) {
            if (!(this.frameBitmap == null || this.frameBitmap.isRecycled())) {
                this.frameBitmap.recycle();
                this.frameBitmap = null;
            }
            if (index == 0) {
                postInvalidate();
                return;
            }
            this.frameBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.borderRes[index]);
            postInvalidate();
        }

        private void createMatrix(int widthPixels, int heightPixels) {
            f510I.reset();
            matrix1.reset();
            matrix1.postScale(-1.0f, 1.0f);
            matrix1.postTranslate((float) widthPixels, 0.0f);
            matrix2.reset();
            matrix2.postScale(1.0f, -1.0f);
            matrix2.postTranslate(0.0f, (float) heightPixels);
            matrix3.reset();
            matrix3.postScale(-1.0f, -1.0f);
            matrix3.postTranslate((float) widthPixels, (float) heightPixels);
        }

        private void createRectX(int widthPixels, int heightPixels) {
            float destH = ((float) widthPixels) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX);
            float destW = ((float) widthPixels) / 2.0f;
            float destX = 0.0f;
            float destY = (float) initialYPos;
            if (destH > ((float) heightPixels)) {
                destH = (float) heightPixels;
                destW = ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * destH) / 2.0f;
                destX = (((float) widthPixels) / 2.0f) - destW;
            }
            destY = ((float) initialYPos) + ((((float) heightPixels) - destH) / 2.0f);
            float srcX = 0.0f;
            float srcY = 0.0f;
            float srcX2 = (float) this.width;
            float srcY2 = (float) this.height;
            this.destRect1X = new RectF(destX, destY, destW + destX, destH + destY);
            float destXX = destX + destW;
            this.destRect2X = new RectF(destXX, destY, destW + destXX, destH + destY);
            this.totalArea1 = new RectF(destX, destY, destW + destXX, destH + destY);
            this.tMode1 = MirrorImageActivity.INDEX_MIRROR_3D;
            if (MirrorImageActivity.this.mulX * ((float) this.height) <= (MirrorImageActivity.this.mulY * 2.0f) * ((float) this.width)) {
                srcX = (((float) this.width) - (((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height)) / 2.0f)) / 2.0f;
                srcX2 = srcX + (((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height)) / 2.0f);
            } else {
                srcY = (((float) this.height) - (((float) (this.width * MirrorImageActivity.INDEX_MIRROR_RATIO)) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX))) / 2.0f;
                srcY2 = srcY + (((float) (this.width * MirrorImageActivity.INDEX_MIRROR_RATIO)) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX));
                this.tMode1 = MirrorImageActivity.INDEX_MIRROR_ADJUSTMENT;
            }
            this.srcRect1 = new RectF(srcX, srcY, srcX2, srcY2);
            this.srcRectPaper = new RectF(srcX, srcY, ((srcX2 - srcX) / 2.0f) + srcX, srcY2);
            float destWPapar = destW / 2.0f;
            this.dstRectPaper1 = new RectF(destX, destY, destWPapar + destX, destH + destY);
            float dextXP = destX + destWPapar;
            this.dstRectPaper2 = new RectF(dextXP, destY, destWPapar + dextXP, destH + destY);
            dextXP += destWPapar;
            this.dstRectPaper3 = new RectF(dextXP, destY, destWPapar + dextXP, destH + destY);
            dextXP += destWPapar;
            this.dstRectPaper4 = new RectF(dextXP, destY, destWPapar + dextXP, destH + destY);
        }

        private void createRectY(int widthPixels, int heightPixels) {
            float destH = (((float) widthPixels) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX)) / 2.0f;
            float destW = (float) widthPixels;
            float destX = 0.0f;
            float destY = (float) MirrorImageActivity.this.initialYPos;
            if (destH > ((float) heightPixels)) {
                destH = (float) heightPixels;
                destW = ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * destH) / 2.0f;
                destX = (((float) widthPixels) / 2.0f) - destW;
            }
            destY = ((float) MirrorImageActivity.this.initialYPos) + ((((float) heightPixels) - (2.0f * destH)) / 2.0f);
            this.destRect1Y = new RectF(destX, destY, destW + destX, destH + destY);
            float destYY = destY + destH;
            this.destRect2Y = new RectF(destX, destYY, destW + destX, destH + destYY);
            this.totalArea2 = new RectF(destX, destY, destW + destX, destH + destYY);
            float srcX = 0.0f;
            float srcY = 0.0f;
            float srcX2 = (float) this.width;
            float srcY2 = (float) this.height;
            this.tMode2 = MirrorImageActivity.INDEX_MIRROR;
            if ((MirrorImageActivity.this.mulX * 2.0f) * ((float) this.height) > MirrorImageActivity.this.mulY * ((float) this.width)) {
                srcY = (((float) this.height) - (((MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX) * ((float) this.width)) / 2.0f)) / 2.0f;
                srcY2 = srcY + (((MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX) * ((float) this.width)) / 2.0f);
            } else {
                srcX = (((float) this.width) - (((float) (this.height * MirrorImageActivity.INDEX_MIRROR_RATIO)) * (MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY))) / 2.0f;
                srcX2 = srcX + (((float) (this.height * MirrorImageActivity.INDEX_MIRROR_RATIO)) * (MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY));
                this.tMode2 = MirrorImageActivity.TAB_SIZE;
            }
            this.srcRect2 = new RectF(srcX, srcY, srcX2, srcY2);
        }

        private void createRectXY(int widthPixels, int heightPixels) {
            float destH = (((float) widthPixels) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX)) / 2.0f;
            float destW = ((float) widthPixels) / 2.0f;
            float destX = 0.0f;
            float destY = (float) MirrorImageActivity.this.initialYPos;
            if (destH > ((float) heightPixels)) {
                destH = (float) heightPixels;
                destW = ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * destH) / 2.0f;
                destX = (((float) widthPixels) / 2.0f) - destW;
            }
            destY = ((float) MirrorImageActivity.this.initialYPos) + ((((float) heightPixels) - (2.0f * destH)) / 2.0f);
            float srcX = 0.0f;
            float srcY = 0.0f;
            float srcX2 = (float) this.width;
            float srcY2 = (float) this.height;
            this.destRect1 = new RectF(destX, destY, destW + destX, destH + destY);
            float destX2 = destX + destW;
            this.destRect2 = new RectF(destX2, destY, destW + destX2, destH + destY);
            float destY2 = destY + destH;
            this.destRect3 = new RectF(destX, destY2, destW + destX, destH + destY2);
            this.destRect4 = new RectF(destX2, destY2, destW + destX2, destH + destY2);
            this.totalArea3 = new RectF(destX, destY, destW + destX2, destH + destY2);
            if (MirrorImageActivity.this.mulX * ((float) this.height) <= MirrorImageActivity.this.mulY * ((float) this.width)) {
                srcX = (((float) this.width) - ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height))) / 2.0f;
                srcX2 = srcX + ((MirrorImageActivity.this.mulX / MirrorImageActivity.this.mulY) * ((float) this.height));
                this.tMode3 = MirrorImageActivity.INDEX_MIRROR_3D;
            } else {
                srcY = (((float) this.height) - (((float) this.width) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX))) / 2.0f;
                srcY2 = srcY + (((float) this.width) * (MirrorImageActivity.this.mulY / MirrorImageActivity.this.mulX));
                this.tMode3 = MirrorImageActivity.INDEX_MIRROR;
            }
            this.srcRect3 = new RectF(srcX, srcY, srcX2, srcY2);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawColor(this.defaultColor);
            if (MirrorImageActivity.this.filterBitmap == null) {
                drawMode(canvas, MirrorImageActivity.this.sourceBitmap, this.mirrorModeList[this.currentModeIndex], this.f510I);
            } else {
                drawMode(canvas, MirrorImageActivity.this.filterBitmap, this.mirrorModeList[this.currentModeIndex], this.f510I);
            }
            if (!(!this.d3Mode || this.d3Bitmap == null || this.d3Bitmap.isRecycled())) {
                canvas.setMatrix(this.f510I);
                canvas.drawBitmap(this.d3Bitmap, null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            if (MirrorImageActivity.this.showText) {
                for (int i = MirrorImageActivity.INDEX_MIRROR; i < MirrorImageActivity.this.textDataList.size(); i += MirrorImageActivity.INDEX_MIRROR_3D) {
                    this.textMatrix.set(((TextDataItem) MirrorImageActivity.this.textDataList.get(i)).imageSaveMatrix);
                    this.textMatrix.postConcat(this.f510I);
                    canvas.setMatrix(this.textMatrix);
                    canvas.drawText(((TextDataItem) MirrorImageActivity.this.textDataList.get(i)).message, ((TextDataItem) MirrorImageActivity.this.textDataList.get(i)).xPos, ((TextDataItem) MirrorImageActivity.this.textDataList.get(i)).yPos, ((TextDataItem) MirrorImageActivity.this.textDataList.get(i)).textPaint);
                    canvas.setMatrix(this.f510I);
                    canvas.drawRect(0.0f, 0.0f, this.mirrorModeList[this.currentModeIndex].rectTotalArea.left, (float) MirrorImageActivity.this.screenHeightPixels, this.textRectPaint);
                    canvas.drawRect(0.0f, 0.0f, (float) MirrorImageActivity.this.screenWidthPixels, this.mirrorModeList[this.currentModeIndex].rectTotalArea.top, this.textRectPaint);
                    canvas.drawRect(this.mirrorModeList[this.currentModeIndex].rectTotalArea.right, 0.0f, (float) MirrorImageActivity.this.screenWidthPixels, (float) MirrorImageActivity.this.screenHeightPixels, this.textRectPaint);
                    canvas.drawRect(0.0f, this.mirrorModeList[this.currentModeIndex].rectTotalArea.bottom, (float) MirrorImageActivity.this.screenWidthPixels, (float) MirrorImageActivity.this.screenHeightPixels, this.textRectPaint);
                }
            }
            if (!(this.frameBitmap == null || this.frameBitmap.isRecycled())) {
                canvas.setMatrix(this.f510I);
                canvas.drawBitmap(this.frameBitmap, null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            super.onDraw(canvas);
        }

        private void drawMode(Canvas canvas, Bitmap bitmap, MirrorImageMode mirrorMode, Matrix matrix) {
            canvas.setMatrix(matrix);
            canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect1, this.framePaint);
            this.m1.set(mirrorMode.matrix1);
            this.m1.postConcat(matrix);
            canvas.setMatrix(this.m1);
            if (!(bitmap == null || bitmap.isRecycled())) {
                canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect2, this.framePaint);
            }
            if (mirrorMode.count == MirrorImageActivity.INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX) {
                this.m2.set(mirrorMode.matrix2);
                this.m2.postConcat(matrix);
                canvas.setMatrix(this.m2);
                if (!(bitmap == null || bitmap.isRecycled())) {
                    canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect3, this.framePaint);
                }
                this.m3.set(mirrorMode.matrix3);
                this.m3.postConcat(matrix);
                canvas.setMatrix(this.m3);
                if (bitmap != null && !bitmap.isRecycled()) {
                    canvas.drawBitmap(bitmap, mirrorMode.getDrawBitmapSrc(), mirrorMode.rect4, this.framePaint);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MirrorImageActivity.INDEX_MIRROR /*0*/:
                    if (x < ((float) (MirrorImageActivity.this.screenWidthPixels / MirrorImageActivity.INDEX_MIRROR_RATIO))) {
                        this.isTouchStartedLeft = true;
                    } else {
                        this.isTouchStartedLeft = false;
                    }
                    if (y < ((float) (MirrorImageActivity.this.screenHeightPixels / MirrorImageActivity.INDEX_MIRROR_RATIO))) {
                        this.isTouchStartedTop = true;
                    } else {
                        this.isTouchStartedTop = false;
                    }
                    this.oldX = x;
                    this.oldY = y;
                    break;
                case MirrorImageActivity.INDEX_MIRROR_RATIO /*2*/:
                    moveGrid(this.mirrorModeList[this.currentModeIndex].getSrcRect(), x - this.oldX, y - this.oldY);
                    this.mirrorModeList[this.currentModeIndex].updateBitmapSrc();
                    this.oldX = x;
                    this.oldY = y;
                    break;
            }
            postInvalidate();
            return true;
        }

        void moveGrid(RectF srcRect, float x, float y) {
            if (this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_MIRROR_3D || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.TAB_SIZE) {
                if (this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_MIRROR_INVISIBLE_VIEW_ACTUAL_INDEX) {
                    x *= -1.0f;
                }
                if (this.isTouchStartedLeft && this.mirrorModeList[this.currentModeIndex].touchMode != MirrorImageActivity.TAB_SIZE) {
                    x *= -1.0f;
                }
                if (srcRect.left + x < 0.0f) {
                    x = -srcRect.left;
                }
                if (srcRect.right + x >= ((float) this.width)) {
                    x = ((float) this.width) - srcRect.right;
                }
                srcRect.left += x;
                srcRect.right += x;
            } else if (this.mirrorModeList[this.currentModeIndex].touchMode == 0 || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_MIRROR_EFFECT || this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_MIRROR_ADJUSTMENT) {
                if (this.mirrorModeList[this.currentModeIndex].touchMode == MirrorImageActivity.INDEX_MIRROR_EFFECT) {
                    y *= -1.0f;
                }
                if (this.isTouchStartedTop && this.mirrorModeList[this.currentModeIndex].touchMode != MirrorImageActivity.INDEX_MIRROR_ADJUSTMENT) {
                    y *= -1.0f;
                }
                if (srcRect.top + y < 0.0f) {
                    y = -srcRect.top;
                }
                if (srcRect.bottom + y >= ((float) this.height)) {
                    y = ((float) this.height) - srcRect.bottom;
                }
                srcRect.top += y;
                srcRect.bottom += y;
            }
        }
    }
}
