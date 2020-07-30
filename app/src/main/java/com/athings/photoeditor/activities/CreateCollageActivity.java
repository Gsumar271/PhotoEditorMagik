package com.athings.photoeditor.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.athings.photoeditor.R;
import com.athings.photoeditor.adapter.CollageImageAdapter;
import com.athings.photoeditor.adapter.ColorPickerAdapter;
import com.athings.photoeditor.adapter.MyRecylceAdapterBase;
import com.athings.photoeditor.canvastextview.ApplyTextInterface;
import com.athings.photoeditor.canvastextview.CustomRelativeLayout;
import com.athings.photoeditor.canvastextview.SingleTapInterface;
import com.athings.photoeditor.canvastextview.TextDataItem;
import com.athings.photoeditor.collagelist.Collage;
import com.athings.photoeditor.collagelist.CollageLayout;
import com.athings.photoeditor.collagelist.MaskPair;
import com.athings.photoeditor.fragments.FullEffectFragment;
import com.athings.photoeditor.fragments.WriteTextFragment;
import com.athings.photoeditor.image.ImageBlurNormal;
import com.athings.photoeditor.utils.Constants;
import com.athings.photoeditor.utils.LibUtility;
import com.athings.photoeditor.utils.Parameter;
import com.athings.photoeditor.utils.RotationGestureDetector;
import com.athings.photoeditor.utils.Shape;
import com.athings.photoeditor.utils.ShapeLayout;
import com.athings.photoeditor.utils.Utility;
import com.athings.photoeditor.utils.Utils;
import com.commit451.nativestackblur.NativeStackBlur;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"NewApi"})
public class CreateCollageActivity extends FragmentActivity {
    public static final int INDEX_COLLAGE = 0;
    public static final int INDEX_COLLAGE_BACKGROUND = 1;
    public static final int INDEX_COLLAGE_BLUR = 4;
    public static final int INDEX_COLLAGE_INVISIBLE_VIEW = 5;
    public static final int INDEX_COLLAGE_RATIO = 3;
    public static final int INDEX_COLLAGE_SPACE = 2;
    public static final int TAB_SIZE = 6;
    private static final String TAG = "CreateCollageActivity";
    private static final float UPPER_SIZE_FOR_LOAD = 1500.0f;
    int RATIO_BUTTON_SIZE = 11;
    AdView adWhirlLayout;
    Bitmap[] bitmapList;
    Bitmap btmDelete;
    Bitmap btmScale;
    CustomRelativeLayout canvasText;
    CollageImageAdapter collageAdapter;
    RecyclerView recyclerViewCollage;
    CollageView collageView;
    LinearLayout colorContainer;
    ViewGroup contextFooter;
    WriteTextFragment fontFragment;
    FullEffectFragment fullEffectFragment;
    int height;
    boolean isScrapBook = false;
    private RotationGestureDetector mRotationDetector;

    RelativeLayout mainLayout;
    float mulX = 1;
    float mulY = 1;
    NinePatchDrawable npd;
    Parameter[] parameterList;
    ArrayList<MyRecylceAdapterBase> patternAdapterList = new ArrayList<>();
    Button[] ratioButtonArray;
    AlertDialog saveImageAlert;
    SeekBar seekBarPadding;
    SeekBar seekBarRound;
    SeekBar seekbarBlur;
    SeekBar seekbarSize;
    View selectFilterTextView;
    boolean selectImageForAdj = false;
    View selectSwapTextView;
    boolean showText = false;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    boolean swapMode = false;
    View[] tabButtonList;
    ArrayList<TextDataItem> textDataList = new ArrayList<>();
    ViewFlipper viewFlipper;
    int width;
    AdView mAdView;
    com.facebook.ads.AdView adView;
    LinearLayout linearAdsBanner;
    com.facebook.ads.InterstitialAd interstitialAd;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        setContentView(R.layout.create_collage_activity);
        linearAdsBanner = (LinearLayout) findViewById(R.id.linearAds);
        if (isOnline() && Constants.ADS_STATUS) {
            linearAdsBanner.setVisibility(View.VISIBLE);

            if (Constants.ADS_TYPE.equals("admob")) {
                try {
                    //addBannnerAds();
                   // loadFullScreenAds();
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


        Bundle extras = getIntent().getExtras();
        int arraySize = getCollageSize(extras);
        seekBarRound = (SeekBar) findViewById(R.id.seekbar_round);
        seekBarRound.setOnSeekBarChangeListener(mSeekBarListener);
        seekBarPadding = (SeekBar) findViewById(R.id.seekbar_padding);
        seekBarPadding.setOnSeekBarChangeListener(mSeekBarListener);
        seekbarSize = (SeekBar) findViewById(R.id.seekbar_size);
        seekbarSize.setOnSeekBarChangeListener(mSeekBarListener);
        seekbarBlur = (SeekBar) findViewById(R.id.seekbar_collage_blur);
        seekbarBlur.setOnSeekBarChangeListener(mSeekBarListener);
        final RecyclerView recyclerViewColor = (RecyclerView) findViewById(R.id.recyclerViewColor);
        recyclerViewCollage = (RecyclerView) findViewById(R.id.recyclerViewCollage);
        int colorDefault = getResources().getColor(R.color.view_flipper_bg_color);
        int colorSelected = getResources().getColor(R.color.footer_button_color_pressed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CreateCollageActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCollage.setLayoutManager(layoutManager);
        collageAdapter = new CollageImageAdapter(Collage.collageIconArray[arraySize - 1], new CollageImageAdapter.CurrentCollageIndexChangedListener() {
            @Override
            public void onIndexChanged(int i) {
                collageView.setCurrentCollageIndex(i);
            }
        }, colorDefault, colorSelected, false, true);
        recyclerViewCollage.setAdapter(collageAdapter);
        recyclerViewCollage.setItemAnimator(new DefaultItemAnimator());
        viewFlipper = (ViewFlipper) findViewById(R.id.collage_view_flipper);
        viewFlipper.setDisplayedChild(5);
        createAdapterList(colorDefault, colorSelected);
        RecyclerView recyclerViewPattern = (RecyclerView) findViewById(R.id.recyclerViewPattern);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateCollageActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        colorContainer = (LinearLayout) findViewById(R.id.color_container);
        recyclerViewPattern.setLayoutManager(linearLayoutManager);
        recyclerViewPattern.setAdapter(new CollageImageAdapter(Utils.patternResIdList3, new CollageImageAdapter.CurrentCollageIndexChangedListener() {
            @Override
            public void onIndexChanged(int position) {
                collageView.backgroundMode = CreateCollageActivity.INDEX_COLLAGE;
                if (position == 0) {
                    collageView.setPatternPaint(-1);
                    return;
                }
                int newPos = position - 1;
                if (patternAdapterList.get(newPos) != recyclerViewColor.getAdapter()) {
                    recyclerViewColor.setAdapter(patternAdapterList.get(newPos));
                    (patternAdapterList.get(newPos)).setSelectedPositinVoid();
                } else {
                    (patternAdapterList.get(newPos)).setSelectedPositinVoid();
                    (patternAdapterList.get(newPos)).notifyDataSetChanged();
                }
                colorContainer.setVisibility(View.VISIBLE);
            }
        }, colorDefault, colorSelected, false, false));
        recyclerViewPattern.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManagerColor = new LinearLayoutManager(CreateCollageActivity.this);
        layoutManagerColor.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewColor.setLayoutManager(layoutManagerColor);
        recyclerViewColor.setAdapter(new ColorPickerAdapter(new CollageImageAdapter.CurrentCollageIndexChangedListener() {
            @Override
            public void onIndexChanged(int color) {
                collageView.setPatternPaintColor(color);
            }
        }, colorDefault, colorSelected));
        recyclerViewColor.setItemAnimator(new DefaultItemAnimator());
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.collage_footer);
        horizontalScrollView.bringToFront();
        horizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.scrollTo(horizontalScrollView.getChildAt(CreateCollageActivity.INDEX_COLLAGE).getMeasuredWidth(), CreateCollageActivity.INDEX_COLLAGE);
            }
        }, 50);
        horizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.fullScroll(17);
            }
        }, 600);
        new BitmapWorkerTask().execute(extras, bundle);

    }

    WriteTextFragment.FontChoosedListener fontChoosedListener = new WriteTextFragment.FontChoosedListener() {
        @Override
        public void onOk(TextDataItem textData) {
            if (canvasText == null) {
                addCanvasTextView();
            }
            canvasText.addTextView(textData);
            getSupportFragmentManager().beginTransaction().remove(fontFragment).commit();
            Log.e(CreateCollageActivity.TAG, "onOK called");
        }
    };
    OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            int id = seekBar.getId();
            if (id == R.id.seekbar_round) {
                if (collageView != null) {
                    collageView.setCornerRadius((float) progress);
                }
            } else if (id == R.id.seekbar_padding) {
                if (collageView != null) {
                    collageView.setPathPadding(collageView.currentCollageIndex, (float) progress);
                }
            } else if (id == R.id.seekbar_size) {
                if (collageView != null) {
                    collageView.setCollageSize(collageView.sizeMatrix, progress);
                }
            } else if (id == R.id.seekbar_collage_blur) {
                float f = ((float) progress) / 4.0f;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.getId() == R.id.seekbar_collage_blur) {
                float radius = ((float) seekBar.getProgress()) / 4.0f;
                if (radius > 25f) {
                    radius = 25f;
                }
                if (radius < 0.0f) {
                    radius = 0.0f;
                }
                Log.e(CreateCollageActivity.TAG, "blur radius " + radius);
                collageView.setBlurBitmap((int) radius, false);
            }
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
        if (isOnline()) {

            linearAdsBanner.setVisibility(View.VISIBLE);
            if (linearAdsBanner.getChildCount() <= 0) {
                mAdView = new AdView(CreateCollageActivity.this);
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
        if (isOnline()) {

            linearAdsBanner.setVisibility(View.VISIBLE);
            if (linearAdsBanner.getChildCount() <= 0) {
                // Instantiate an AdView view
                adView = new com.facebook.ads.AdView(this, Constants.ADS_FACEBOOK_BANNER_ID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                linearAdsBanner.addView(adView);

                // Request to load an ad
                adView.loadAd();
            }
        } else {
            linearAdsBanner.setVisibility(View.GONE);
        }
    }

    private void createAdapterList(int colorDefault, int colorSelected) {
        int size = Utils.patternResIdList2.length;
        patternAdapterList.clear();
        patternAdapterList.add(new ColorPickerAdapter(new CollageImageAdapter.CurrentCollageIndexChangedListener() {
            @Override
            public void onIndexChanged(int color) {
                collageView.setPatternPaintColor(color);
            }
        }, colorDefault, colorSelected));
        for (int i = INDEX_COLLAGE; i < size; i += INDEX_COLLAGE_BACKGROUND) {
            patternAdapterList.add(new CollageImageAdapter(Utils.patternResIdList2[i], new CollageImageAdapter.CurrentCollageIndexChangedListener() {
                public void onIndexChanged(int positionOrColor) {
                    collageView.setPatternPaint(positionOrColor);
                }
            }, colorDefault, colorSelected, true, true));
        }
    }

    int getCollageSize(Bundle extras) {
        long[] selectedImageList = extras.getLongArray("photo_id_list");
        if (selectedImageList == null) {
            return 1;
        }
        return selectedImageList.length;
    }

    public void addCanvasTextView() {
        canvasText = new CustomRelativeLayout(CreateCollageActivity.this, textDataList, collageView.identityMatrix, new SingleTapInterface() {
            public void onSingleTap(TextDataItem textData) {
                fontFragment = new WriteTextFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("text_data", textData);
                fontFragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.collage_text_view_fragment_container, fontFragment, "FONT_FRAGMENT").commit();
                Log.e(CreateCollageActivity.TAG, "replace fragment");
                fontFragment.setFontChoosedListener(fontChoosedListener);
            }
        });
        canvasText.setApplyTextListener(new ApplyTextInterface() {
            public void onOk(ArrayList<TextDataItem> tdList) {
                Iterator it = tdList.iterator();
                while (it.hasNext()) {
                    ((TextDataItem) it.next()).setImageSaveMatrix(collageView.identityMatrix);
                }
                textDataList = tdList;
                showText = true;
                if (mainLayout == null) {
                    mainLayout = (RelativeLayout) findViewById(R.id.collage_main_layout);
                }
                mainLayout.removeView(canvasText);
                collageView.postInvalidate();
            }

            public void onCancel() {
                showText = true;
                mainLayout.removeView(canvasText);
                collageView.postInvalidate();
            }
        });
        showText = false;
        collageView.invalidate();
        mainLayout.addView(canvasText);
        findViewById(R.id.collage_text_view_fragment_container).bringToFront();
        fontFragment = new WriteTextFragment();
        fontFragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction().add(R.id.collage_text_view_fragment_container, fontFragment, "FONT_FRAGMENT").commit();
        Log.e(TAG, "add fragment");
        fontFragment.setFontChoosedListener(fontChoosedListener);
    }

    class BitmapWorkerTask extends AsyncTask<Bundle, Void, Void> {
        int arraySize;
        Bundle data;
        ProgressDialog progressDialog;
        Bundle savedInstanceState;

        BitmapWorkerTask() {
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CreateCollageActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("loading images!");
            progressDialog.show();
        }

        protected Void doInBackground(Bundle... params) {
            int i;
            data = params[0];
            savedInstanceState = params[1];
            isScrapBook = data.getBoolean("is_scrap_book", false);
            long[] selectedImageList = data.getLongArray("photo_id_list");
            int[] selectedImageOrientationList = data.getIntArray("photo_orientation_list");
            arraySize = 0;
            int maxDivider;
            if (selectedImageList == null) {
                String selectedImagePath = data.getString("selected_image_path");
                if (selectedImagePath != null) {
                    arraySize = 1;
                    bitmapList = new Bitmap[arraySize];
                    maxDivider = arraySize;
                    if (maxDivider < 3) {
                        maxDivider = 3;
                    }
                    bitmapList[0] = Utils.decodeFile(selectedImagePath, Utility.maxSizeForDimension(CreateCollageActivity.this, maxDivider, UPPER_SIZE_FOR_LOAD), isScrapBook);
                }
            } else {
                arraySize = selectedImageList.length;
                bitmapList = new Bitmap[arraySize];
                maxDivider = arraySize;
                if (maxDivider < 3) {
                    maxDivider = 3;
                }
                int requiredSize = Utility.maxSizeForDimension(CreateCollageActivity.this, maxDivider, UPPER_SIZE_FOR_LOAD);
                int loadingImageError = 0;
                for (i = CreateCollageActivity.INDEX_COLLAGE; i < arraySize; i += CreateCollageActivity.INDEX_COLLAGE_BACKGROUND) {
                    Bitmap bitmap = Utils.getScaledBitmapFromId(CreateCollageActivity.this, selectedImageList[i], selectedImageOrientationList[i], requiredSize, isScrapBook);
                    if (bitmap != null) {
                        bitmapList[i] = bitmap;
                    } else {
                        loadingImageError += CreateCollageActivity.INDEX_COLLAGE_BACKGROUND;
                    }
                }
                if (loadingImageError > 0) {
                    int newSize = arraySize - loadingImageError;
                    Bitmap[] arr = new Bitmap[newSize];
                    int j = 0;
                    for (i = 0; i < arraySize; i++) {
                        if (bitmapList[i] != null) {
                            arr[j] = bitmapList[i];
                            j += 1;
                        }
                    }
                    arraySize = newSize;
                    bitmapList = arr;
                }
            }
            parameterList = new Parameter[arraySize];
            for (i = 0; i < parameterList.length; i++) {
                parameterList[i] = new Parameter();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
            }
            if (arraySize <= 0) {
                Toast msg = Toast.makeText(CreateCollageActivity.this, "Couldn't load images!", Toast.LENGTH_SHORT);
                msg.setGravity(17, msg.getXOffset() / CreateCollageActivity.INDEX_COLLAGE_SPACE, msg.getYOffset() / CreateCollageActivity.INDEX_COLLAGE_SPACE);
                msg.show();
                finish();
                return;
            }
            if (Collage.collageIconArray[bitmapList.length - 1] != collageAdapter.iconList) {
                collageAdapter.setData(Collage.collageIconArray[bitmapList.length - 1]);
                collageAdapter.notifyDataSetChanged();
                Log.e(CreateCollageActivity.TAG, "change collage icons");
            }
            if (isScrapBook) {
                btmDelete = BitmapFactory.decodeResource(getResources(), R.drawable.scrapbook_remove);
                btmScale = BitmapFactory.decodeResource(getResources(), R.drawable.scrapbook_scale);
            }
            if (isScrapBook) {
                npd = (NinePatchDrawable) ContextCompat.getDrawable(CreateCollageActivity.this, R.drawable.shadow_7);
                Log.e(CreateCollageActivity.TAG, "ndp width " + npd.getMinimumHeight());
            }
            collageView = new CollageView(CreateCollageActivity.this, width, height);
            mainLayout = (RelativeLayout) findViewById(R.id.collage_main_layout);
            mainLayout.addView(collageView);
            viewFlipper.bringToFront();
            linearAdsBanner.bringToFront();
            slideLeftIn = AnimationUtils.loadAnimation(CreateCollageActivity.this, R.anim.slide_in_left);
            slideLeftOut = AnimationUtils.loadAnimation(CreateCollageActivity.this, R.anim.slide_out_left);
            slideRightIn = AnimationUtils.loadAnimation(CreateCollageActivity.this, R.anim.slide_in_right);
            slideRightOut = AnimationUtils.loadAnimation(CreateCollageActivity.this, R.anim.slide_out_right);
            addEffectFragment();
            if (arraySize == CreateCollageActivity.INDEX_COLLAGE_BACKGROUND) {
                setVisibilityForSingleImage();
            }
            if (isScrapBook) {
                setVisibilityForScrapbook();
            }
            viewFlipper = (ViewFlipper) findViewById(R.id.collage_view_flipper);
            viewFlipper.bringToFront();
            findViewById(R.id.collage_footer).bringToFront();
            findViewById(R.id.collage_header).bringToFront();
            contextFooter = (ViewGroup) findViewById(R.id.collage_context_menu);
            contextFooter.bringToFront();
            selectSwapTextView = findViewById(R.id.select_image_swap);
            selectSwapTextView.bringToFront();
            selectSwapTextView.setVisibility(View.INVISIBLE);
            selectFilterTextView = findViewById(R.id.select_image_filter);
            selectFilterTextView.bringToFront();
            selectFilterTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setVisibilityForScrapbook() {
        findViewById(R.id.buttonCollageLayout).setVisibility(View.GONE);
        findViewById(R.id.buttonSpace).setVisibility(View.GONE);
        findViewById(R.id.buttonSwap).setVisibility(View.GONE);
        findViewById(R.id.buttonFit).setVisibility(View.GONE);
        findViewById(R.id.buttonCenter).setVisibility(View.GONE);
        findViewById(R.id.buttonDelete).setVisibility(View.VISIBLE);
    }

    void addEffectFragment() {
        if (fullEffectFragment == null) {
            fullEffectFragment = (FullEffectFragment) getSupportFragmentManager().findFragmentByTag("FULL_FRAGMENT");
            Log.e(TAG, "addEffectFragment");
            if (fullEffectFragment == null) {
                fullEffectFragment = new FullEffectFragment();
                Log.e(TAG, "EffectFragment == null");
                fullEffectFragment.setArguments(getIntent().getExtras());
                Log.e(TAG, "fullEffectFragment null");
                getSupportFragmentManager().beginTransaction().add(R.id.collage_effect_fragment_container, fullEffectFragment, "FULL_FRAGMENT").commitAllowingStateLoss();
            } else {
                Log.e(TAG, "not null null");
                if (collageView.shapeIndex >= 0) {
                    fullEffectFragment.setBitmapWithParameter(bitmapList[collageView.shapeIndex], parameterList[collageView.shapeIndex]);
                }
            }
            getSupportFragmentManager().beginTransaction().hide(fullEffectFragment).commitAllowingStateLoss();
            fullEffectFragment.setFullBitmapReadyListener(new FullEffectFragment.FullBitmapReady() {
                public void onBitmapReady(Bitmap bitmap, Parameter parameter) {
                    collageView.updateShapeListForFilterBitmap(bitmap);
                    collageView.updateParamList(parameter);
                    collageView.postInvalidate();
                    getSupportFragmentManager().beginTransaction().hide(fullEffectFragment).commit();
                    collageView.postInvalidate();
                }

                public void onCancel() {
                    setVisibilityOfFilterHorizontalListview(false);
                    collageView.postInvalidate();
                }
            });
            findViewById(R.id.collage_effect_fragment_container).bringToFront();
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


    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adView != null) {
            adView.destroy();
        }
        int i;
        super.onDestroy();
        if (bitmapList != null) {
            for (i = INDEX_COLLAGE; i < bitmapList.length; i += INDEX_COLLAGE_BACKGROUND) {
                if (bitmapList[i] != null) {
                    bitmapList[i].recycle();
                }
            }
        }
        if (collageView != null) {
            if (collageView.shapeLayoutList != null) {
                for (i = INDEX_COLLAGE; i < collageView.shapeLayoutList.size(); i += INDEX_COLLAGE_BACKGROUND) {
                    for (int j = INDEX_COLLAGE; j < collageView.shapeLayoutList.get(i).shapeArr.length; j += INDEX_COLLAGE_BACKGROUND) {
                        if (collageView.shapeLayoutList.get(i).shapeArr[j] != null) {
                            collageView.shapeLayoutList.get(i).shapeArr[j].freeBitmaps();
                        }
                    }
                }
            }
            if (collageView.maskBitmapArray != null) {
                for (i = INDEX_COLLAGE; i < collageView.maskBitmapArray.length; i += INDEX_COLLAGE_BACKGROUND) {
                    if (collageView.maskBitmapArray[i] != null) {
                        if (!collageView.maskBitmapArray[i].isRecycled()) {
                            collageView.maskBitmapArray[i].recycle();
                        }
                        collageView.maskBitmapArray[i] = null;
                    }
                }
            }
        }
        if (adWhirlLayout != null) {
            adWhirlLayout.removeAllViews();
            adWhirlLayout.destroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("show_text", showText);
        outState.putSerializable("text_data", textDataList);
        if (fontFragment != null && fontFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fontFragment).commit();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showText = savedInstanceState.getBoolean("show_text");
        textDataList = (ArrayList) savedInstanceState.getSerializable("text_data");
        if (textDataList == null) {
            textDataList = new ArrayList<>();
        }
        if (contextFooter == null) {
            contextFooter = ((ViewGroup) findViewById(R.id.collage_context_menu));
        }
        if (contextFooter != null) {
            contextFooter.bringToFront();
        }
    }

    @Override
    public void onBackPressed() {
        if (fontFragment != null && fontFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fontFragment).commit();
        } else if (!showText && canvasText != null) {
            showText = true;
            mainLayout.removeView(canvasText);
            collageView.postInvalidate();
            canvasText = null;
            Log.e(TAG, "replace fragment");
        } else if (fullEffectFragment == null || !fullEffectFragment.isVisible()) {
            if (colorContainer.getVisibility() == View.VISIBLE) {
                hideColorContainer();
            } else if (swapMode) {
                selectSwapTextView.setVisibility(View.INVISIBLE);
                swapMode = false;
            } else if (collageView != null && collageView.shapeIndex >= 0) {
                collageView.unselectShapes();
            } else if (selectImageForAdj) {
                selectFilterTextView.setVisibility(View.INVISIBLE);
                selectImageForAdj = false;
            } else if (viewFlipper == null || viewFlipper.getDisplayedChild() == INDEX_COLLAGE_INVISIBLE_VIEW) {
                backButtonAlertBuilder();
            } else {
                setSelectedTab(INDEX_COLLAGE_INVISIBLE_VIEW);
            }
        }
    }

    private void backButtonAlertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCollageActivity.this);
        builder.setMessage("Would you like to save image ?").setCancelable(true).setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new SaveImageTask().execute();
            }
        }).setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setNeutralButton("No", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
                finish();
            }
        });
        saveImageAlert = builder.create();
        saveImageAlert.show();
    }

    private class SaveImageTask extends AsyncTask<Object, Object, Object> {
        ProgressDialog progressDialog;
        String resultPath;

        private SaveImageTask() {
            resultPath = null;
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CreateCollageActivity.this);
            progressDialog.setMessage("Saving image...");
            progressDialog.show();
        }

        protected Object doInBackground(Object... arg0) {
            resultPath = collageView.saveBitmap(width, height);
            return null;
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            if (resultPath != null) {
                Intent intent = new Intent(CreateCollageActivity.this, SaveShareImageActivity.class);
                intent.putExtra("imagePath", resultPath);
                startActivity(intent);
            }

            MyMediaScannerConnectionClient myMediaScannerConnectionClient = new MyMediaScannerConnectionClient(getApplicationContext(), new File(resultPath), null);
        }
    }

    private final class MyMediaScannerConnectionClient implements MediaScannerConnectionClient {
        private MediaScannerConnection mConn;
        private String mFilename;
        private String mMimetype;

        MyMediaScannerConnectionClient(Context ctx, File file, String mimetype) {
            mFilename = file.getAbsolutePath();
            mConn = new MediaScannerConnection(ctx, this);
            mConn.connect();
        }

        public void onMediaScannerConnected() {
            mConn.scanFile(mFilename, mMimetype);
        }

        public void onScanCompleted(String path, Uri uri) {
            mConn.disconnect();
        }
    }

    public void myClickHandler(View view) {
        int id = view.getId();
        if (id == R.id.buttonCollageLayout) {
            setSelectedTab(INDEX_COLLAGE);
        } else if (id == R.id.buttonRatio) {
            setSelectedTab(INDEX_COLLAGE_RATIO);
        } else if (id == R.id.buttonBlur) {
            collageView.setBlurBitmap(collageView.blurRadius, false);
            setSelectedTab(INDEX_COLLAGE_BLUR);
            collageView.startAnimator();
        } else if (id == R.id.buttonBackground) {
            setSelectedTab(INDEX_COLLAGE_BACKGROUND);
        } else if (id == R.id.buttonSpace) {
            setSelectedTab(INDEX_COLLAGE_SPACE);
        } else if (id == R.id.buttonAdjustment) {
            if (collageView.shapeLayoutList.get(INDEX_COLLAGE).shapeArr.length == INDEX_COLLAGE_BACKGROUND) {
                collageView.shapeIndex = INDEX_COLLAGE;
                collageView.openFilterFragment();
            } else if (collageView.shapeIndex >= 0) {
                collageView.openFilterFragment();
                Log.e(TAG, "collageView.shapeIndex>=0 openFilterFragment");
            } else {
                setSelectedTab(INDEX_COLLAGE_INVISIBLE_VIEW);
                selectFilterTextView.setVisibility(View.VISIBLE);
                selectImageForAdj = true;
            }
        } else if (id == R.id.buttonSwap) {
            if (collageView.shapeLayoutList.get(collageView.currentCollageIndex).shapeArr.length == INDEX_COLLAGE_SPACE) {
                collageView.swapBitmaps(INDEX_COLLAGE, INDEX_COLLAGE_BACKGROUND);
            } else {
                selectSwapTextView.setVisibility(View.VISIBLE);
                swapMode = true;
            }
        } else if (id == R.id.buttonDelete) {
            createDeleteDialog();
        } else if (id == R.id.button_collage_context_filter) {
            collageView.openFilterFragment();
        } else if (id == R.id.button_save_collage_image) {
            setSelectedTab(INDEX_COLLAGE_INVISIBLE_VIEW);
            new SaveImageTask().execute();
        } else if (id == R.id.button_cancel_collage_image) {
            backButtonAlertBuilder();
        } else if (id == R.id.button11) {
            mulX = 1.0f;
            mulY = 1.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(0);
        } else if (id == R.id.button21) {
            mulX = 2.0f;
            mulY = 1.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(1);
        } else if (id == R.id.button12) {
            mulX = 1.0f;
            mulY = 2.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(2);
        } else if (id == R.id.button32) {
            mulX = 3.0f;
            mulY = 2.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(3);
        } else if (id == R.id.button23) {
            mulX = 2.0f;
            mulY = 3.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(4);
        } else if (id == R.id.button43) {
            mulX = 4.0f;
            mulY = 3.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(5);
        } else if (id == R.id.button34) {
            mulX = 3.0f;
            mulY = 4.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(6);
        } else if (id == R.id.button45) {
            mulX = 4.0f;
            mulY = 5.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(7);
        } else if (id == R.id.button57) {
            mulX = 5.0f;
            mulY = 7.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(8);
        } else if (id == R.id.button169) {
            mulX = 16.0f;
            mulY = 9.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(9);
        } else if (id == R.id.button916) {
            mulX = 9.0f;
            mulY = 16.0f;
            collageView.updateShapeListForRatio(width, height);
            setRatioButtonBg(10);
        } else if (id == R.id.hide_select_image_warning) {
            selectSwapTextView.setVisibility(View.INVISIBLE);
            swapMode = false;
        } else if (id == R.id.hide_select_image_warning_filter) {
            selectFilterTextView.setVisibility(View.INVISIBLE);
            selectImageForAdj = false;
        } else if (id == R.id.hide_color_container) {
            hideColorContainer();
        } else if (id == R.id.buttonText) {
            addCanvasTextView();
            clearViewFlipper();
        }
        if (id == R.id.buttonFit) {
            collageView.setShapeScaleMatrix(INDEX_COLLAGE);
        } else if (id == R.id.buttonCenter) {
            collageView.setShapeScaleMatrix(INDEX_COLLAGE_BACKGROUND);
        } else if (id == R.id.button_collage_context_rotate_left) {
            collageView.setShapeScaleMatrix(INDEX_COLLAGE_RATIO);
        } else if (id == R.id.button_collage_context_rotate_right) {
            collageView.setShapeScaleMatrix(INDEX_COLLAGE_SPACE);
        } else if (id == R.id.button_collage_context_flip_horizontal) {
            collageView.setShapeScaleMatrix(INDEX_COLLAGE_BLUR);
        } else if (id == R.id.button_collage_context_flip_vertical) {
            collageView.setShapeScaleMatrix(INDEX_COLLAGE_INVISIBLE_VIEW);
        } else if (id == R.id.button_collage_context_rotate_negative) {
            collageView.setShapeScaleMatrix(TAB_SIZE);
        } else if (id == R.id.button_collage_context_rotate_positive) {
            collageView.setShapeScaleMatrix(7);
        } else if (id == R.id.button_collage_context_zoom_in) {
            toastMatrixMessage(collageView.setShapeScaleMatrix(8));
        } else if (id == R.id.button_collage_context_zoom_out) {
            toastMatrixMessage(collageView.setShapeScaleMatrix(9));
        } else if (id == R.id.button_collage_context_move_left) {
            toastMatrixMessage(collageView.setShapeScaleMatrix(10));
        } else if (id == R.id.button_collage_context_move_right) {
            toastMatrixMessage(collageView.setShapeScaleMatrix(11));
        } else if (id == R.id.button_collage_context_move_up) {
            toastMatrixMessage(collageView.setShapeScaleMatrix(12));
        } else if (id == R.id.button_collage_context_move_down) {
            toastMatrixMessage(collageView.setShapeScaleMatrix(13));
        } else if (fullEffectFragment != null && fullEffectFragment.isVisible()) {
            fullEffectFragment.myClickHandler(view);
        }
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
        for (int i = 0; i < RATIO_BUTTON_SIZE; i++) {
            ratioButtonArray[i].setBackgroundResource(R.drawable.selector_collage_ratio_button);
        }
        ratioButtonArray[index].setBackgroundResource(R.drawable.collage_ratio_bg_pressed);
    }

    void toastMatrixMessage(int message) {
        String str = null;
        if (message == INDEX_COLLAGE_BACKGROUND) {
            str = "You reached maximum zoom!";
        } else if (message == INDEX_COLLAGE_SPACE) {
            str = "You reached minimum zoom!";
        } else if (message == TAB_SIZE) {
            str = "You reached max bottom!";
        } else if (message == INDEX_COLLAGE_INVISIBLE_VIEW) {
            str = "You reached max top!";
        } else if (message == INDEX_COLLAGE_BLUR) {
            str = "You reached max right!";
        } else if (message == INDEX_COLLAGE_RATIO) {
            str = "You reached max left!";
        }
        if (str != null) {
            Toast msg = Toast.makeText(CreateCollageActivity.this, str, Toast.LENGTH_SHORT);
            msg.setGravity(Gravity.CENTER, msg.getXOffset() / INDEX_COLLAGE_SPACE, msg.getYOffset() / INDEX_COLLAGE_SPACE);
            msg.show();
        }
    }

    void clearViewFlipper() {
        viewFlipper.setDisplayedChild(INDEX_COLLAGE_INVISIBLE_VIEW);
        setTabBg(-1);
    }

    class CollageView extends View {
        public static final int BACKGROUND_BLUR = 1;
        public static final int BACKGROUND_PATTERN = 0;
        private static final int INVALID_POINTER_ID = 1;
        public static final int PATTERN_SENTINEL = -1;
        static final float RATIO_CONSTANT = 1.25f;
        private static final int UPPER_SIZE_LIMIT = 2048;
        float MIN_ZOOM;
        RectF above;
        int animEpsilon;
        int animHalfTime;
        int animSizeSeekbarProgress;
        boolean animate;
        int animationCount;
        int animationDurationLimit;
        int animationLimit;
        private Runnable animator;
        int backgroundMode;
        Bitmap blurBitmap;
        ImageBlurNormal blurBuilderNormal;
        int blurRadius = 14;
        RectF blurRectDst;
        Rect blurRectSrc;
        Paint borderPaint;
        RectF bottom;
        RectF bottomLeft;
        RectF bottomRight;
        Paint circlePaint;
        float cornerRadius;
        int currentCollageIndex;
        RectF drawingAreaRect;
        final float epsilon;
        float finalAngle;
        Bitmap frameBitmap;
        int frameDuration;
        RectF frameRect;
        Matrix identityMatrix;
        boolean isInCircle;
        boolean isOnCross;
        RectF left;
        private int mActivePointerId;
        float mLastTouchX;
        float mLastTouchY;
        private ScaleGestureDetector mScaleDetector;
        float mScaleFactor;
        private GestureDetectorCompat mTouchDetector;
        Bitmap[] maskBitmapArray;
        int[] maskResIdList;
        float[] matrixValues;
        boolean move;
        int offsetX;
        int offsetY;
        boolean orthogonal;
        float paddingDistance;
        Paint paint;
        Paint paintGray;
        Bitmap patternBitmap;
        Paint patternPaint;
        int previousIndex;
        float[] pts;
        Rect rectAnim;
        RectF right;
        RotationGestureDetector.OnRotationGestureListener rotateListener;
        Shape scaleShape;
        int screenHeight;
        int screenWidth;
        int shapeIndex;
        List<ShapeLayout> shapeLayoutList;
        Matrix sizeMatrix;
        Matrix sizeMatrixSaved;
        float sizeScale;
        ArrayList<Float> smallestDistanceList;
        private float startAngle;
        Matrix startMatrix;
        long startTime;
        Matrix textMatrix;
        RectF topLeft;
        RectF topRight;
        float[] values;
        float xscale;
        float yscale;
        PointF zoomStart;

        class MyGestureListener extends SimpleOnGestureListener {
            private static final String DEBUG_TAG = "Gestures";

            MyGestureListener() {
            }

            public boolean onSingleTapConfirmed(MotionEvent event) {
                Log.d(DEBUG_TAG, "onSingleTapConfirmed: ");
                return true;
            }

            public boolean onSingleTapUp(MotionEvent event) {
                Log.d(DEBUG_TAG, "onSingleTapUp: ");
                if (!isOnCross) {
                    collageView.selectCurrentShape(event.getX(), event.getY(), true);
                }
                return true;
            }
        }

        private class ScaleListener extends SimpleOnScaleGestureListener {
            private ScaleListener() {
            }

            public boolean onScale(ScaleGestureDetector detector) {
                if (shapeIndex >= 0) {
                    mScaleFactor = detector.getScaleFactor();
                    detector.isInProgress();
                    mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
                    scaleShape = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex];
                    if (isScrapBook) {
                        scaleShape.bitmapMatrixScaleScrapBook(mScaleFactor, mScaleFactor);
                    } else {
                        scaleShape.bitmapMatrixScale(mScaleFactor, mScaleFactor, scaleShape.bounds.centerX(), scaleShape.bounds.centerY());
                    }
                    invalidate();
                    requestLayout();
                }
                return true;
            }
        }

        @SuppressLint({"NewApi"})
        public CollageView(Context context, int width, int height) {
            super(context);
            paint = new Paint();
            paddingDistance = 0.0f;
            cornerRadius = 0.0f;
            currentCollageIndex = 0;
            shapeIndex = PATTERN_SENTINEL;
            patternPaint = new Paint(1);
            shapeLayoutList = new ArrayList<>();
            identityMatrix = new Matrix();
            maskResIdList = new int[]{R.drawable.mask_butterfly, R.drawable.mask_cloud, R.drawable.mask_clover, R.drawable.mask_leaf, R.drawable.mask_left_foot, R.drawable.mask_diamond, R.drawable.mask_santa, R.drawable.mask_snowman, R.drawable.mask_paw, R.drawable.mask_egg, R.drawable.mask_twitter, R.drawable.mask_circle, R.drawable.mask_hexagon, R.drawable.mask_heart};
            smallestDistanceList = new ArrayList<>();
            yscale = 1.0f;
            xscale = 1.0f;
            sizeScale = 1.0f;
            sizeMatrix = new Matrix();
            animSizeSeekbarProgress = 0;
            animate = false;
            animationCount = 0;
            animationLimit = 31;
            animHalfTime = (animationLimit / INDEX_COLLAGE_SPACE) + INVALID_POINTER_ID;
            frameDuration = 10;
            animEpsilon = 20;
            animationDurationLimit = 50;
            startTime = System.nanoTime();
            animator = new Runnable() {
                @Override
                public void run() {
                    boolean scheduleNewFrame = false;
                    int iter = ((int) (((float) (System.nanoTime() - startTime)) / 1000000.0f)) / animationDurationLimit;
                    if (iter <= 0) {
                        iter = INVALID_POINTER_ID;
                    }
                    if (animationCount == 0) {
                        collageView.animationCount += INVALID_POINTER_ID;
                    } else {
                        collageView.animationCount += iter;
                    }
                    setCollageSize(sizeMatrix, animSize(animationCount));
                    if (animationCount < animationLimit) {
                        scheduleNewFrame = true;
                    } else {
                        animate = false;
                    }
                    if (scheduleNewFrame) {
                        postDelayed(this, (long) frameDuration);
                    } else {
                        sizeMatrix.set(sizeMatrixSaved);
                    }
                    shapeLayoutList.get(currentCollageIndex).shapeArr[BACKGROUND_PATTERN].f508r.roundOut(rectAnim);
                    invalidate(rectAnim);
                    startTime = System.nanoTime();
                }
            };
            rectAnim = new Rect();
            textMatrix = new Matrix();
            blurRectDst = new RectF();
            drawingAreaRect = new RectF();
            above = new RectF();
            left = new RectF();
            right = new RectF();
            bottom = new RectF();
            move = false;
            paintGray = new Paint(1);
            mActivePointerId = 1;
            zoomStart = new PointF();
            startMatrix = new Matrix();
            startAngle = 0.0f;
            MIN_ZOOM = 0.1f;
            isInCircle = false;
            isOnCross = false;
            orthogonal = false;
            mScaleFactor = 1.0f;
            matrixValues = new float[9];
            finalAngle = 0.0f;
            epsilon = 4.0f;
            rotateListener = new RotationGestureDetector.OnRotationGestureListener() {
                @Override
                public void OnRotation(RotationGestureDetector rotationGestureDetector) {
                    if (shapeIndex >= 0) {
                        float angle = rotationGestureDetector.getAngle();
                        scaleShape = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex];
                        float rotation = getMatrixRotation(scaleShape.bitmapMatrix);
                        if ((rotation == 0.0f || rotation == 90.0f || rotation == 180.0f || rotation == -180.0f || rotation == -90.0f) && Math.abs(finalAngle - angle) < 4.0f) {
                            orthogonal = true;
                            return;
                        }
                        if (Math.abs((rotation - finalAngle) + angle) < 4.0f) {
                            angle = finalAngle - rotation;
                            orthogonal = true;
                        }
                        if (Math.abs(90.0f - ((rotation - finalAngle) + angle)) < 4.0f) {
                            angle = (finalAngle + 90.0f) - rotation;
                            orthogonal = true;
                        }
                        if (Math.abs(180.0f - ((rotation - finalAngle) + angle)) < 4.0f) {
                            angle = (180.0f + finalAngle) - rotation;
                            orthogonal = true;
                        }
                        if (Math.abs(-180.0f - ((rotation - finalAngle) + angle)) < 4.0f) {
                            angle = (finalAngle - 0.024902344f) - rotation;
                            orthogonal = true;
                        }
                        if (Math.abs(-90.0f - ((rotation - finalAngle) + angle)) < 4.0f) {
                            angle = (finalAngle - 0.049804688f) - rotation;
                            orthogonal = true;
                        } else {
                            orthogonal = false;
                        }
                        scaleShape.bitmapMatrixRotate(finalAngle - angle);
                        finalAngle = angle;
                        invalidate();
                        requestLayout();
                    }
                }
            };
            values = new float[9];
            backgroundMode = 0;
            blurRectSrc = new Rect();
            maskBitmapArray = new Bitmap[maskResIdList.length];
            borderPaint = new Paint(1);
            borderPaint.setColor(getResources().getColor(R.color.blue));
            borderPaint.setStyle(Style.STROKE);
            borderPaint.setStrokeWidth(10.0f);
            screenWidth = width;
            screenHeight = height;
            circlePaint = new Paint();
            circlePaint.setColor(SupportMenu.CATEGORY_MASK);
            identityMatrix.reset();
            topLeft = new RectF((float) (width * BACKGROUND_PATTERN), (float) (height * BACKGROUND_PATTERN), 0.5f * ((float) width), 0.5f * ((float) height));
            topRight = new RectF(0.5f * ((float) width), 0.0f * ((float) height), 1.0f * ((float) width), 0.5f * ((float) height));
            bottomLeft = new RectF((float) (width * BACKGROUND_PATTERN), 0.5f * ((float) height), 0.5f * ((float) width), 1.0f * ((float) height));
            bottomRight = new RectF(0.5f * ((float) width), 0.5f * ((float) height), 1.0f * ((float) width), 1.0f * ((float) height));
            Path pathTopLeft = new Path();
            Path pathTopRight = new Path();
            Path pathBottomLeft = new Path();
            Path pathBottomRight = new Path();
            pathTopLeft.addRect(topLeft, Direction.CCW);
            pathTopRight.addRect(topRight, Direction.CCW);
            pathBottomLeft.addRect(bottomLeft, Direction.CCW);
            pathBottomRight.addRect(bottomRight, Direction.CCW);
            mTouchDetector = new GestureDetectorCompat(context, new MyGestureListener());
            mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
            mRotationDetector = new RotationGestureDetector(rotateListener);
            calculateOffset();
            patternPaint = new Paint(INVALID_POINTER_ID);
            patternPaint.setColor(PATTERN_SENTINEL);
            createShapeList(bitmapList.length, width, height);
            paintGray.setColor(Color.DKGRAY);
        }

        private void calculateOffset() {
            PointF scale = getRatio();
            offsetX = (int) ((((float) width) - (scale.x * ((float) width))) / 2.0f);
            offsetY = (int) ((((float) height) - (scale.y * ((float) width))) / 2.0f);
        }

        public void setCropBitmap(int left, int top, int right, int bottom) {
            if (shapeIndex >= 0) {
                Bitmap sourceBitmap = bitmapList[shapeIndex];
                boolean isFilter = sourceBitmap != ((ShapeLayout) shapeLayoutList.get(0)).shapeArr[shapeIndex].getBitmap();
                if (isFilter) {
                    doCrop(left, top, right, bottom, sourceBitmap, false, false);
                    doCrop(left, top, right, bottom, ((ShapeLayout) shapeLayoutList.get(0)).shapeArr[shapeIndex].getBitmap(), true, true);
                } else {
                    doCrop(left, top, right, bottom, sourceBitmap, false, true);
                }
                if (!(!isFilter || parameterList == null || parameterList[shapeIndex] == null)) {
                    parameterList[shapeIndex].setId(Parameter.uniqueId.getAndIncrement());
                }
                invalidate();
            }
        }

        public void doCrop(int left, int top, int right, int bottom, Bitmap sourceBitmap, boolean isFilter, boolean last) {
            Bitmap localCropBtm = sourceBitmap;
            int bitmapWidth = sourceBitmap.getWidth();
            int bitmapHeight = sourceBitmap.getHeight();
            if (right > bitmapWidth) {
                right = bitmapWidth;
            }
            if (bottom > bitmapHeight) {
                bottom = bitmapHeight;
            }
            if (right - left > 0 && bottom - top > 0) {
                if (VERSION.SDK_INT < 12) {
                    sourceBitmap = ImageBlurNormal.createCroppedBitmap(localCropBtm, left, top, right - left, bottom - top, false);
                } else {
                    sourceBitmap = Bitmap.createBitmap(localCropBtm, left, top, right - left, bottom - top);
                }
                if (localCropBtm != sourceBitmap) {
                    localCropBtm.recycle();
                }
                if (!isFilter) {
                    bitmapList[shapeIndex] = sourceBitmap;
                }
                if (last) {
                    for (int i = 0; i < shapeLayoutList.size(); i += 0) {
                        ((ShapeLayout) shapeLayoutList.get(i)).shapeArr[shapeIndex].setBitmap(sourceBitmap, false);
                        if (isScrapBook) {
                            ((ShapeLayout) shapeLayoutList.get(i)).shapeArr[shapeIndex].resetDashPaths();
                        }
                    }
                }
            }
        }

        public String saveBitmap(int width, int height) {
            int i;
            int btmWidth = (int) (((float) width) * collageView.xscale);
            int btmHeight = (int) (((float) width) * collageView.yscale);
            float max = (float) Math.max(btmWidth, btmHeight);
            float btmScale = ((float) Utils.maxSizeForSave(CreateCollageActivity.this, 2048.0f)) / max;
            int newBtmWidth = (int) (((float) btmWidth) * btmScale);
            int newBtmHeight = (int) (((float) btmHeight) * btmScale);
            if (newBtmWidth <= 0) {
                newBtmWidth = btmWidth;
                Log.e(CreateCollageActivity.TAG, "newBtmWidth");
            }
            if (newBtmHeight <= 0) {
                newBtmHeight = btmHeight;
                Log.e(CreateCollageActivity.TAG, "newBtmHeight");
            }
            Bitmap savedBitmap = Bitmap.createBitmap(newBtmWidth, newBtmHeight, Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(savedBitmap);
            ShapeLayout arr = shapeLayoutList.get(currentCollageIndex);
            Matrix sizeMat = new Matrix();
            sizeMat.reset();
            sizeMat.preScale(btmScale, btmScale);
            bitmapCanvas.setMatrix(sizeMat);
            if (backgroundMode == 0) {
                bitmapCanvas.drawRect(0.0f, 0.0f, (float) btmWidth, (float) btmHeight, patternPaint);
            }
            if (!(blurBitmap == null || blurBitmap.isRecycled() || backgroundMode != INVALID_POINTER_ID)) {
                RectF rectF = new RectF(0.0f, 0.0f, (float) btmWidth, (float) btmHeight);
                bitmapCanvas.drawBitmap(blurBitmap, blurRectSrc, rectF, paint);
            }
            sizeMat.postScale(sizeScale, sizeScale, ((float) newBtmWidth) / 2.0f, ((float) newBtmHeight) / 2.0f);
            sizeMat.preTranslate((float) (-offsetX), (float) (-offsetY));
            bitmapCanvas.setMatrix(sizeMat);
            int q = bitmapCanvas.saveLayer(((float) (-width)) / sizeScale, ((float) (-height)) / sizeScale, ((float) offsetX) + (((float) width) / sizeScale), ((float) offsetY) + (((float) height) / sizeScale), null, Canvas.ALL_SAVE_FLAG);
            for (i = BACKGROUND_PATTERN; i < arr.shapeArr.length; i += INVALID_POINTER_ID) {
                boolean drawPorterClear = false;
                if (i == arr.getClearIndex()) {
                    drawPorterClear = true;
                }
                Log.e(CreateCollageActivity.TAG, "drawPorterClear " + drawPorterClear);
                if (isScrapBook) {
                    arr.shapeArr[i].drawShapeForScrapBook(bitmapCanvas, newBtmWidth, newBtmHeight, false, false);
                } else {
                    arr.shapeArr[i].drawShapeForSave(bitmapCanvas, newBtmWidth, newBtmHeight, q, drawPorterClear);
                }
            }
            Matrix mat;
            if (textDataList != null) {
                for (i = BACKGROUND_PATTERN; i < textDataList.size(); i += INVALID_POINTER_ID) {
                    mat = new Matrix();
                    mat.set(textDataList.get(i).imageSaveMatrix);
                    mat.postTranslate((float) (-offsetX), (float) (-offsetY));
                    mat.postScale(btmScale, btmScale);
                    bitmapCanvas.setMatrix(mat);
                    bitmapCanvas.drawText(textDataList.get(i).message, textDataList.get(i).xPos, textDataList.get(i).yPos, textDataList.get(i).textPaint);
                }
            }
            bitmapCanvas.restoreToCount(q);
            String resultPath = String.valueOf(Environment.getExternalStorageDirectory().toString()) + getString(R.string.directory) + String.valueOf(System.currentTimeMillis()) + ".jpg";
            new File(resultPath).getParentFile().mkdirs();
            try {
                OutputStream fileOutputStream = new FileOutputStream(resultPath);
                savedBitmap.compress(CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            savedBitmap.recycle();
            return resultPath;
        }

        int getMaskIndex(int resId) {
            for (int i = BACKGROUND_PATTERN; i < maskResIdList.length; i += INVALID_POINTER_ID) {
                if (resId == maskResIdList[i]) {
                    return i;
                }
            }
            return PATTERN_SENTINEL;
        }

        private void createShapeList(int shapeCount, int width, int height) {
            shapeLayoutList.clear();
            smallestDistanceList.clear();
            Collage collage = Collage.CreateCollage(shapeCount, width, width, isScrapBook);
            int size = ((CollageLayout) collage.collageLayoutList.get(BACKGROUND_PATTERN)).shapeList.size();
            Log.e(CreateCollageActivity.TAG, "bitmapList.length " + bitmapList.length);
            int i = BACKGROUND_PATTERN;
            while (i < collage.collageLayoutList.size()) {
                Shape[] shapeArray = new Shape[size];
                for (int j = BACKGROUND_PATTERN; j < shapeCount; j += INVALID_POINTER_ID) {
                    boolean masked = false;
                    int resId = BACKGROUND_PATTERN;
                    if (!(((CollageLayout) collage.collageLayoutList.get(i)).maskPairList == null || ((CollageLayout) collage.collageLayoutList.get(i)).maskPairList.isEmpty())) {
                        for (MaskPair maskPair : ((CollageLayout) collage.collageLayoutList.get(i)).maskPairList) {
                            if (j == maskPair.index) {
                                masked = true;
                                resId = maskPair.id;
                            }
                        }
                    }
                    if (masked) {
                        Bitmap maskBitmap = null;
                        int maskIndex = getMaskIndex(resId);
                        if (maskIndex >= 0) {
                            if (maskBitmapArray == null) {
                                maskBitmapArray = new Bitmap[maskResIdList.length];
                            }
                            if (maskBitmapArray[maskIndex] == null) {
                                maskBitmapArray[maskIndex] = loadMaskBitmap2(resId);
                                Log.e(CreateCollageActivity.TAG, "load mask bitmap from factory");
                            } else {
                                Log.e(CreateCollageActivity.TAG, "load mask bitmap from pool");
                            }
                            maskBitmap = maskBitmapArray[maskIndex];
                        }
                        shapeArray[j] = new Shape((PointF[]) ((CollageLayout) collage.collageLayoutList.get(i)).shapeList.get(j), bitmapList[j], null, offsetX, offsetY, maskBitmap, isScrapBook, j, false, btmDelete, btmScale, screenWidth);
                        if (isScrapBook) {
                            shapeArray[j].initScrapBook(npd);
                        }
                    } else {
                        shapeArray[j] = new Shape((PointF[]) ((CollageLayout) collage.collageLayoutList.get(i)).shapeList.get(j), bitmapList[j], ((CollageLayout) collage.collageLayoutList.get(i)).getexceptionIndex(j), offsetX, offsetY, isScrapBook, j, false, btmDelete, btmScale, screenWidth);
                        if (isScrapBook) {
                            shapeArray[j].initScrapBook(npd);
                        }
                    }
                }
                smallestDistanceList.add(smallestDistance(shapeArray));
                ShapeLayout shapeLayout = new ShapeLayout(shapeArray);
                shapeLayout.setClearIndex(((CollageLayout) collage.collageLayoutList.get(i)).getClearIndex());
                shapeLayoutList.add(shapeLayout);
                i += INVALID_POINTER_ID;
            }
            if (!isScrapBook) {
                if (shapeCount != INVALID_POINTER_ID) {
                    for (int index = BACKGROUND_PATTERN; index < shapeLayoutList.size(); index += INVALID_POINTER_ID) {
                        setPathPadding(index, (float) getResources().getInteger(R.integer.default_space_value));
                        for (i = BACKGROUND_PATTERN; i < shapeLayoutList.get(index).shapeArr.length; i += INVALID_POINTER_ID) {
                            shapeLayoutList.get(index).shapeArr[i].setScaleMatrix(INVALID_POINTER_ID);
                        }
                    }
                    setCollageSize(sizeMatrix, getResources().getInteger(R.integer.default_ssize_value));
                } else if (bitmapList.length == INVALID_POINTER_ID) {
                    setCollageSize(sizeMatrix, getResources().getInteger(R.integer.default_ssize_value));
                }
            }
        }

        private int setShapeScaleMatrix(int mode) {
            if (shapeIndex < 0) {
                return PATTERN_SENTINEL;
            }
            int message = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].setScaleMatrix(mode);
            invalidate();
            return message;
        }

        private void deleteBitmap(int index, int width, int height) {
            Log.e(CreateCollageActivity.TAG, "index" + index);
            Shape[] scrapBookTemp = shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr;
            if (index >= 0 && index < shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr.length) {
                int i;
                int newSize = shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr.length + PATTERN_SENTINEL;
                Bitmap[] currentBitmapListTemp = new Bitmap[newSize];
                Bitmap[] bitmapListTemp = new Bitmap[newSize];
                int j = BACKGROUND_PATTERN;
                for (i = BACKGROUND_PATTERN; i < currentBitmapListTemp.length + INVALID_POINTER_ID; i += INVALID_POINTER_ID) {
                    if (i != index) {
                        currentBitmapListTemp[j] = shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr[i].getBitmap();
                        bitmapListTemp[j] = bitmapList[i];
                        j += INVALID_POINTER_ID;
                    }
                }
                bitmapList[index].recycle();
                shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr[index].getBitmap().recycle();
                shapeLayoutList.clear();
                smallestDistanceList.clear();
                Collage collage = Collage.CreateCollage(newSize, width, width, isScrapBook);
                int size = ((CollageLayout) collage.collageLayoutList.get(BACKGROUND_PATTERN)).shapeList.size();
                bitmapList = bitmapListTemp;
                i = BACKGROUND_PATTERN;
                while (i < collage.collageLayoutList.size()) {
                    Shape[] shapeArray = new Shape[size];
                    for (j = BACKGROUND_PATTERN; j < currentBitmapListTemp.length; j += INVALID_POINTER_ID) {
                        boolean masked = false;
                        int resId = BACKGROUND_PATTERN;
                        if (!(((CollageLayout) collage.collageLayoutList.get(i)).maskPairList == null || ((CollageLayout) collage.collageLayoutList.get(i)).maskPairList.isEmpty())) {
                            for (MaskPair maskPair : ((CollageLayout) collage.collageLayoutList.get(i)).maskPairList) {
                                if (j == maskPair.index) {
                                    masked = true;
                                    resId = maskPair.id;
                                }
                            }
                        }
                        if (masked) {
                            Bitmap maskBitmap = null;
                            int maskIndez = getMaskIndex(resId);
                            if (maskIndez >= 0) {
                                if (maskBitmapArray == null) {
                                    maskBitmapArray = new Bitmap[maskResIdList.length];
                                }
                                if (maskBitmapArray[maskIndez] == null) {
                                    maskBitmapArray[maskIndez] = loadMaskBitmap2(resId);
                                    Log.e(CreateCollageActivity.TAG, "load mask bitmap from factory");
                                } else {
                                    Log.e(CreateCollageActivity.TAG, "load mask bitmap from pool");
                                }
                                maskBitmap = maskBitmapArray[maskIndez];
                            }
                            shapeArray[j] = new Shape((PointF[]) ((CollageLayout) collage.collageLayoutList.get(i)).shapeList.get(j), currentBitmapListTemp[j], null, offsetX, offsetY, maskBitmap, isScrapBook, j, true, btmDelete, btmScale, screenWidth);
                            if (isScrapBook) {
                                shapeArray[j].initScrapBook(npd);
                            }
                        } else {
                            int i2 = j;
                            shapeArray[j] = new Shape((PointF[]) ((CollageLayout) collage.collageLayoutList.get(i)).shapeList.get(j), currentBitmapListTemp[j], ((CollageLayout) collage.collageLayoutList.get(i)).getexceptionIndex(j), offsetX, offsetY, isScrapBook, i2, true, btmDelete, btmScale, screenWidth);
                            if (isScrapBook) {
                                shapeArray[j].initScrapBook(npd);
                            }
                        }
                    }
                    if (isScrapBook) {
                        for (int k = BACKGROUND_PATTERN; k < scrapBookTemp.length; k += INVALID_POINTER_ID) {
                            if (k < index) {
                                shapeArray[k].bitmapMatrix.set(scrapBookTemp[k].bitmapMatrix);
                            }
                            if (k > index) {
                                shapeArray[k + PATTERN_SENTINEL].bitmapMatrix.set(scrapBookTemp[k].bitmapMatrix);
                            }
                        }
                    }
                    ShapeLayout shapeLayout = new ShapeLayout(shapeArray);
                    shapeLayout.setClearIndex(((CollageLayout) collage.collageLayoutList.get(i)).getClearIndex());
                    shapeLayoutList.add(shapeLayout);
                    smallestDistanceList.add(smallestDistance(shapeArray));
                    i += INVALID_POINTER_ID;
                }
                currentCollageIndex = BACKGROUND_PATTERN;
                collageAdapter.selectedPosition = BACKGROUND_PATTERN;
                collageAdapter.setData(Collage.collageIconArray[newSize + PATTERN_SENTINEL]);
                collageAdapter.notifyDataSetChanged();
                if (!isScrapBook) {
                    updateShapeListForRatio(width, height);
                }
                unselectShapes();
                for (i = BACKGROUND_PATTERN; i < shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr.length; i += INVALID_POINTER_ID) {
                    Log.e(CreateCollageActivity.TAG, "i " + i + "is recylced " + shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr[i].getBitmap().isRecycled());
                }
                invalidate();
                if (currentBitmapListTemp.length == INVALID_POINTER_ID) {
                    setVisibilityForSingleImage();
                }
                if (newSize == INVALID_POINTER_ID) {
                    setPathPadding(BACKGROUND_PATTERN, 0.0f);
                    if (sizeScale == 1.0f && !isScrapBook) {
                        setCollageSize(sizeMatrix, getResources().getInteger(R.integer.default_ssize_value));
                    }
                }
            }
        }

        Bitmap loadMaskBitmap2(int resId) {
            return convertToAlphaMask(BitmapFactory.decodeResource(getResources(), resId));
        }

        private Bitmap convertToAlphaMask(Bitmap b) {
            Bitmap a = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Config.ALPHA_8);
            new Canvas(a).drawBitmap(b, 0.0f, 0.0f, null);
            b.recycle();
            return a;
        }

        public float smallestDistance(Shape[] shapeArray) {
            float smallestDistance = shapeArray[BACKGROUND_PATTERN].smallestDistance();
            for (int i = BACKGROUND_PATTERN; i < shapeArray.length; i += INVALID_POINTER_ID) {
                float distance = shapeArray[i].smallestDistance();
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                }
            }
            return smallestDistance;
        }

        private void updateShapeListForRatio(int width, int height) {
            int shapeCount = shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr.length;
            PointF scale = getRatio();
            calculateOffset();
            Collage collage = Collage.CreateCollage(shapeCount, (int) (scale.x * ((float) width)), (int) (((float) width) * scale.y), isScrapBook);
            smallestDistanceList.clear();
            for (int index = BACKGROUND_PATTERN; index < shapeLayoutList.size(); index += INVALID_POINTER_ID) {
                if (shapeCount == INVALID_POINTER_ID) {
                    shapeLayoutList.get(index).shapeArr[BACKGROUND_PATTERN].changeRatio((PointF[]) ((CollageLayout) collage.collageLayoutList.get(index)).shapeList.get(BACKGROUND_PATTERN), null, offsetX, offsetY, isScrapBook, BACKGROUND_PATTERN, (int) (scale.x * ((float) width)), (int) (((float) width) * scale.y));
                } else {
                    for (int j = BACKGROUND_PATTERN; j < shapeCount; j += INVALID_POINTER_ID) {
                        shapeLayoutList.get(index).shapeArr[j].changeRatio((PointF[]) ((CollageLayout) collage.collageLayoutList.get(index)).shapeList.get(j), null, offsetX, offsetY, isScrapBook, j, (int) (scale.x * ((float) width)), (int) (((float) width) * scale.y));
                    }
                }
                smallestDistanceList.add(smallestDistance(shapeLayoutList.get(index).shapeArr));
                setPathPadding(index, paddingDistance);
                if (!isScrapBook) {
                    for (int k = BACKGROUND_PATTERN; k < shapeLayoutList.get(index).shapeArr.length; k += INVALID_POINTER_ID) {
                        shapeLayoutList.get(index).shapeArr[k].setScaleMatrix(INVALID_POINTER_ID);
                    }
                }
            }
            setCornerRadius(cornerRadius);
            if (blurBitmap != null) {
                setBlurRect2((float) blurBitmap.getWidth(), (float) blurBitmap.getHeight());
            }
            postInvalidate();
        }

        PointF getRatio() {
            yscale = 1.0f;
            xscale = 1.0f;
            yscale = mulY / mulX;
            if (!isScrapBook && yscale > RATIO_CONSTANT) {
                xscale = RATIO_CONSTANT / yscale;
                yscale = RATIO_CONSTANT;
            }
            return new PointF(xscale, yscale);
        }

        private void updateShapeListForFilterBitmap(Bitmap bitmap) {
            if (shapeIndex >= 0) {
                for (int i = BACKGROUND_PATTERN; i < shapeLayoutList.size(); i += INVALID_POINTER_ID) {
                    shapeLayoutList.get(i).shapeArr[shapeIndex].setBitmap(bitmap, true);
                }
            }
        }

        void updateParamList(Parameter p) {
            if (shapeIndex >= 0) {
                parameterList[shapeIndex] = new Parameter(p);
            }
        }

        private void swapBitmaps(int index1, int index2) {
            Bitmap bitmap1 = shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr[index1].getBitmap();
            Bitmap bitmap2 = shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr[index2].getBitmap();
            for (int i = BACKGROUND_PATTERN; i < shapeLayoutList.size(); i += INVALID_POINTER_ID) {
                shapeLayoutList.get(i).shapeArr[index1].setBitmap(bitmap2, false);
                shapeLayoutList.get(i).shapeArr[index2].setBitmap(bitmap1, false);
            }
            Bitmap temp = bitmapList[index1];
            bitmapList[index1] = bitmapList[index2];
            bitmapList[index2] = temp;
            Parameter tempParam = parameterList[index1];
            parameterList[index1] = parameterList[index2];
            parameterList[index2] = tempParam;
            float sd = smallestDistanceList.get(index1).floatValue();
            smallestDistanceList.set(index1, smallestDistanceList.get(index2));
            smallestDistanceList.set(index2, sd);
            selectSwapTextView.setVisibility(INVISIBLE);
            unselectShapes();
        }

        void setCurrentCollageIndex(int index) {
            currentCollageIndex = index;
            if (currentCollageIndex >= shapeLayoutList.size()) {
                currentCollageIndex = BACKGROUND_PATTERN;
            }
            if (currentCollageIndex < 0) {
                currentCollageIndex = shapeLayoutList.size() + PATTERN_SENTINEL;
            }
            setCornerRadius(cornerRadius);
            setPathPadding(currentCollageIndex, paddingDistance);
        }

        private void setCornerRadius(float radius) {
            cornerRadius = radius;
            CornerPathEffect corEffect = new CornerPathEffect(radius);
            for (int i = BACKGROUND_PATTERN; i < shapeLayoutList.get(currentCollageIndex).shapeArr.length; i += INVALID_POINTER_ID) {
                shapeLayoutList.get(currentCollageIndex).shapeArr[i].setRadius(corEffect);
            }
            postInvalidate();
        }

        private void setPathPadding(int index, float distance) {
            paddingDistance = distance;
            for (int i = BACKGROUND_PATTERN; i < shapeLayoutList.get(index).shapeArr.length; i += INVALID_POINTER_ID) {
                shapeLayoutList.get(index).shapeArr[i].scalePath((smallestDistanceList.get(index) / 250.0f) * distance, (float) screenWidth, (float) screenWidth);
                if (!isScrapBook) {
                    shapeLayoutList.get(index).shapeArr[i].checkScaleBounds();
                    shapeLayoutList.get(index).shapeArr[i].checkBoundries();
                }
            }
            postInvalidate();
        }

        private void setCollageSize(Matrix matrix, int progress) {
            matrix.reset();
            sizeScale = calculateSize((float) progress);
            matrix.postScale(sizeScale, sizeScale, (((float) (offsetX + offsetX)) + (((float) width) * xscale)) / 2.0f, (((float) (offsetY + offsetY)) + (((float) width) * yscale)) / 2.0f);
            invalidate();
        }

        float calculateSize(float progress) {
            return 1.0f - (progress / 200.0f);
        }

        int calculateSizeProgress(float size) {
            int progress = 200 - Math.round(200.0f * size);
            if (progress < 0) {
                progress = BACKGROUND_PATTERN;
            }
            if (progress > 100) {
                return 100;
            }
            return progress;
        }

        void setPatternPaint(int resId) {
            if (patternPaint == null) {
                patternPaint = new Paint(INVALID_POINTER_ID);
                patternPaint.setColor(PATTERN_SENTINEL);
            }
            if (resId == PATTERN_SENTINEL) {
                patternPaint.setShader(null);
                patternPaint.setColor(PATTERN_SENTINEL);
                postInvalidate();
                return;
            }
            patternBitmap = BitmapFactory.decodeResource(getResources(), resId);
            patternPaint.setShader(new BitmapShader(patternBitmap, TileMode.REPEAT, TileMode.REPEAT));
            postInvalidate();
        }

        void setPatternPaintColor(int color) {
            if (patternPaint == null) {
                patternPaint = new Paint(INVALID_POINTER_ID);
            }
            patternPaint.setShader(null);
            patternPaint.setColor(color);
            postInvalidate();
        }

        public void setFrame(int index) {
            if (frameRect == null) {
                frameRect = new RectF(0.0f, 0.0f, (float) screenWidth, (float) screenWidth);
            }
            if (!(frameBitmap == null || frameBitmap.isRecycled())) {
                frameBitmap.recycle();
                frameBitmap = null;
            }
            if (index != 0) {
                frameBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.borderRes[index]);
                postInvalidate();
            }
        }

        public void startAnimator() {
            if (seekbarSize != null) {
                animSizeSeekbarProgress = seekbarSize.getProgress();
            } else {
                animSizeSeekbarProgress = BACKGROUND_PATTERN;
            }
            sizeMatrixSaved = new Matrix(sizeMatrix);
            animationCount = BACKGROUND_PATTERN;
            animate = true;
            removeCallbacks(animator);
            postDelayed(animator, 150);
        }

        int animSize(int value) {
            int res;
            if (value < animHalfTime) {
                res = value;
            } else {
                res = animationLimit - value;
            }
            return animSizeSeekbarProgress + Math.round((float) (res * CreateCollageActivity.INDEX_COLLAGE_SPACE));
        }

        public void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            canvas.save();
            drawingAreaRect.set((float) offsetX, (float) offsetY, ((float) offsetX) + (((float) width) * xscale), ((float) offsetY) + (((float) width) * yscale));
            canvas.drawPaint(paintGray);
            if (backgroundMode == 0) {
                canvas.drawRect(drawingAreaRect, patternPaint);
            }
            if (!(blurBitmap == null || blurBitmap.isRecycled() || backgroundMode != INVALID_POINTER_ID)) {
                blurRectDst.set(drawingAreaRect);
                canvas.drawBitmap(blurBitmap, blurRectSrc, blurRectDst, paint);
            }
            if (!isScrapBook) {
                canvas.setMatrix(sizeMatrix);
            }
            int j = BACKGROUND_PATTERN;
            if (!isScrapBook || showText) {
                j = canvas.saveLayer(0.0f, 0.0f, ((float) width) / sizeScale, ((float) height) / sizeScale, null, Canvas.ALL_SAVE_FLAG);
            }
            int i = BACKGROUND_PATTERN;
            while (i < shapeLayoutList.get(currentCollageIndex).shapeArr.length) {
                boolean drawPorterClear = false;
                if (i == shapeLayoutList.get(currentCollageIndex).getClearIndex()) {
                    drawPorterClear = true;
                }
                if (isScrapBook) {
                    shapeLayoutList.get(currentCollageIndex).shapeArr[i].drawShapeForScrapBook(canvas, width, height, i == shapeIndex, orthogonal);
                } else {
                    shapeLayoutList.get(currentCollageIndex).shapeArr[i].drawShape(canvas, width, height, j, drawPorterClear);
                }
                i += INVALID_POINTER_ID;
            }
            if (!isScrapBook && shapeIndex >= 0 && shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr.length > INVALID_POINTER_ID) {
                canvas.drawRect(shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bounds, borderPaint);
            }
            if (showText) {
                canvas.restoreToCount(j);
                for (i = BACKGROUND_PATTERN; i < textDataList.size(); i += INVALID_POINTER_ID) {
                    textMatrix.set(textDataList.get(i).imageSaveMatrix);
                    canvas.setMatrix(textMatrix);
                    canvas.drawText(textDataList.get(i).message, textDataList.get(i).xPos, textDataList.get(i).yPos, textDataList.get(i).textPaint);
                    canvas.setMatrix(identityMatrix);
                }
            }
            if (!(frameBitmap == null || frameBitmap.isRecycled())) {
                canvas.drawBitmap(frameBitmap, null, frameRect, paint);
            }
            if (isScrapBook) {
                canvas.restore();
                above.set(0.0f, 0.0f, (float) canvas.getWidth(), drawingAreaRect.top);
                left.set(0.0f, drawingAreaRect.top, drawingAreaRect.left, drawingAreaRect.bottom);
                right.set(drawingAreaRect.right, drawingAreaRect.top, (float) canvas.getWidth(), drawingAreaRect.bottom);
                bottom.set(0.0f, drawingAreaRect.bottom, (float) canvas.getWidth(), (float) canvas.getHeight());
                canvas.drawRect(above, paintGray);
                canvas.drawRect(left, paintGray);
                canvas.drawRect(right, paintGray);
                canvas.drawRect(bottom, paintGray);
            }
        }

        public boolean onTouchEvent(MotionEvent ev) {
            mScaleDetector.onTouchEvent(ev);
            mTouchDetector.onTouchEvent(ev);
            if (isScrapBook) {
                mRotationDetector.onTouchEvent(ev);
            }
            int action = ev.getAction();
            float x;
            float y;
            int pointerIndex;
            switch (action & MotionEventCompat.ACTION_MASK) {
                case BACKGROUND_PATTERN /*0*/:
                    previousIndex = shapeIndex;
                    x = ev.getX();
                    y = ev.getY();
                    mLastTouchX = x;
                    mLastTouchY = y;
                    orthogonal = false;
                    mActivePointerId = ev.getPointerId(BACKGROUND_PATTERN);
                    if (isScrapBook && shapeIndex >= 0) {
                        zoomStart.set(x, y);
                        pts = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].getMappedCenter();
                        if (pts != null) {
                            startAngle = -Utils.pointToAngle(x, y, pts[BACKGROUND_PATTERN], pts[INVALID_POINTER_ID]);
                        }
                        isInCircle = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].isInCircle(x, y);
                        isOnCross = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].isOnCross(x, y);
                        break;
                    }
                    selectCurrentShape(x, y, false);
                    break;
                case INVALID_POINTER_ID /*1*/:
                    orthogonal = false;
                    mActivePointerId = INVALID_POINTER_ID;
                    if (isOnCross) {
                        createDeleteDialog();
                    }
                    isInCircle = false;
                    isOnCross = false;
                    invalidate();
                    break;
                case CreateCollageActivity.INDEX_COLLAGE_SPACE /*2*/:
                    if (!isOnCross) {
                        pointerIndex = ev.findPointerIndex(mActivePointerId);
                        x = ev.getX(pointerIndex);
                        y = ev.getY(pointerIndex);
                        if (shapeIndex < 0) {
                            selectCurrentShape(x, y, false);
                        }
                        if (shapeIndex >= 0) {
                            if (!isScrapBook || !isInCircle) {
                                shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bitmapMatrixTranslate(x - mLastTouchX, y - mLastTouchY);
                                mLastTouchX = x;
                                mLastTouchY = y;
                                invalidate();
                                break;
                            }
                            pts = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].getMappedCenter();
                            float currentAngle = -Utils.pointToAngle(x, y, pts[BACKGROUND_PATTERN], pts[INVALID_POINTER_ID]);
                            Log.d(CreateCollageActivity.TAG, "currentAngle " + Float.toString(currentAngle));
                            float rotation = getMatrixRotation(shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bitmapMatrix);
                            if ((rotation == 0.0f || rotation == 90.0f || rotation == 180.0f || rotation == -180.0f || rotation == -90.0f) && Math.abs(startAngle - currentAngle) < 4.0f) {
                                orthogonal = true;
                            } else {
                                if (Math.abs((rotation - startAngle) + currentAngle) < 4.0f) {
                                    currentAngle = startAngle - rotation;
                                    orthogonal = true;
                                    Log.d(CreateCollageActivity.TAG, "aaaaa " + Float.toString(rotation));
                                } else {
                                    if (Math.abs(90.0f - ((rotation - startAngle) + currentAngle)) < 4.0f) {
                                        currentAngle = (90.0f + startAngle) - rotation;
                                        orthogonal = true;
                                        Log.d(CreateCollageActivity.TAG, "bbbbb " + Float.toString(rotation));
                                    } else {
                                        if (Math.abs(180.0f - ((rotation - startAngle) + currentAngle)) < 4.0f) {
                                            currentAngle = (180.0f + startAngle) - rotation;
                                            orthogonal = true;
                                            Log.d(CreateCollageActivity.TAG, "cccc " + Float.toString(rotation));
                                        } else {
                                            if (Math.abs(-180.0f - ((rotation - startAngle) + currentAngle)) < 4.0f) {
                                                currentAngle = (-180.0f + startAngle) - rotation;
                                                orthogonal = true;
                                            } else {
                                                if (Math.abs(-90.0f - ((rotation - startAngle) + currentAngle)) < 4.0f) {
                                                    currentAngle = (-90.0f + startAngle) - rotation;
                                                    orthogonal = true;
                                                    Log.d(CreateCollageActivity.TAG, "dddd " + Float.toString(rotation));
                                                } else {
                                                    orthogonal = false;
                                                }
                                            }
                                        }
                                    }
                                }
                                shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bitmapMatrixRotate(startAngle - currentAngle);
                                startAngle = currentAngle;
                            }
                            float scaley = ((float) Math.sqrt((double) (((x - pts[BACKGROUND_PATTERN]) * (x - pts[BACKGROUND_PATTERN])) + ((y - pts[INVALID_POINTER_ID]) * (y - pts[INVALID_POINTER_ID]))))) / ((float) Math.sqrt((double) (((zoomStart.x - pts[BACKGROUND_PATTERN]) * (zoomStart.x - pts[BACKGROUND_PATTERN])) + ((zoomStart.y - pts[INVALID_POINTER_ID]) * (zoomStart.y - pts[INVALID_POINTER_ID])))));
                            float scale = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].getScale();
                            if (scale >= MIN_ZOOM || (scale < MIN_ZOOM && scaley > 1.0f)) {
                                shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bitmapMatrixScaleScrapBook(scaley, scaley);
                                zoomStart.set(x, y);
                            }
                            invalidate();
                            return true;
                        }
                    }
                    break;
                case CreateCollageActivity.INDEX_COLLAGE_RATIO:
                    mActivePointerId = INVALID_POINTER_ID;
                    isInCircle = false;
                    isOnCross = false;
                    break;
                case CreateCollageActivity.TAB_SIZE:
                    finalAngle = 0.0f;
                    pointerIndex = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & action) >> 8;
                    if (ev.getPointerId(pointerIndex) == mActivePointerId) {
                        int newPointerIndex = pointerIndex == 0 ? INVALID_POINTER_ID : BACKGROUND_PATTERN;
                        mLastTouchX = ev.getX(newPointerIndex);
                        mLastTouchY = ev.getY(newPointerIndex);
                        mActivePointerId = ev.getPointerId(newPointerIndex);
                        break;
                    }
                    break;
            }
            return true;
        }

        private void selectCurrentShapeScrapBook(float x, float y, boolean isSingleTap) {
            int i;
            int length = shapeLayoutList.get(currentCollageIndex).shapeArr.length;
            boolean isSelected = false;
            for (i = length + PATTERN_SENTINEL; i >= 0; i += PATTERN_SENTINEL) {
                if (shapeLayoutList.get(currentCollageIndex).shapeArr[i].isScrapBookSelected(x, y)) {
                    shapeIndex = i;
                    isSelected = true;
                    break;
                }
            }
            if (previousIndex == shapeIndex && isSingleTap) {
                unselectShapes();
            } else if (!isSelected) {
                unselectShapes();
            } else if (selectImageForAdj) {
                openFilterFragment();
            } else if (shapeIndex >= 0 && shapeIndex < length) {
                Shape shapeTemp = shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex];
                Bitmap btmTemp = bitmapList[shapeIndex];
                Parameter prmTemp = parameterList[shapeIndex];
                for (i = BACKGROUND_PATTERN; i < length; i += INVALID_POINTER_ID) {
                    if (i >= shapeIndex) {
                        if (i < length + PATTERN_SENTINEL) {
                            shapeLayoutList.get(currentCollageIndex).shapeArr[i] = shapeLayoutList.get(currentCollageIndex).shapeArr[i + INVALID_POINTER_ID];
                            bitmapList[i] = bitmapList[i + INVALID_POINTER_ID];
                            parameterList[i] = parameterList[i + INVALID_POINTER_ID];
                        } else {
                            shapeLayoutList.get(currentCollageIndex).shapeArr[i] = shapeTemp;
                            bitmapList[i] = btmTemp;
                            parameterList[i] = prmTemp;
                        }
                    }
                }
                if (previousIndex == shapeIndex) {
                    previousIndex = length + PATTERN_SENTINEL;
                } else if (previousIndex > shapeIndex) {
                    previousIndex += PATTERN_SENTINEL;
                }
                shapeIndex = length + PATTERN_SENTINEL;
                if (shapeLayoutList.get(BACKGROUND_PATTERN).shapeArr.length > 0) {
                    contextFooter.setVisibility(BACKGROUND_PATTERN);
                    setSelectedTab(CreateCollageActivity.INDEX_COLLAGE_INVISIBLE_VIEW);
                }
            }
            if (shapeIndex >= 0) {
                shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bitmapMatrixgGetValues(matrixValues);
                mScaleFactor = matrixValues[BACKGROUND_PATTERN];
            }
            postInvalidate();
        }

        private void selectCurrentShape(float x, float y, boolean isSingleTap) {
            if (isScrapBook) {
                selectCurrentShapeScrapBook(x, y, isSingleTap);
            } else {
                selectCurrentShapeCollage(x, y, isSingleTap);
            }
        }

        private void selectCurrentShapeCollage(float x, float y, boolean isSingleTap) {
            int swapIndex = shapeIndex;
            for (int i = BACKGROUND_PATTERN; i < shapeLayoutList.get(currentCollageIndex).shapeArr.length; i += INVALID_POINTER_ID) {
                if (shapeLayoutList.get(currentCollageIndex).shapeArr[i].region.contains((int) x, (int) y)) {
                    shapeIndex = i;
                }
            }
            if (selectImageForAdj) {
                openFilterFragment();
            } else if (swapMode) {
                Log.e(CreateCollageActivity.TAG, "PRE SWAP");
                if (swapIndex != shapeIndex && swapIndex > PATTERN_SENTINEL && shapeIndex > PATTERN_SENTINEL) {
                    Log.e(CreateCollageActivity.TAG, "SWAP");
                    swapBitmaps(shapeIndex, swapIndex);
                    swapMode = false;
                }
            } else if (previousIndex == shapeIndex && isSingleTap) {
                unselectShapes();
            } else if (shapeLayoutList.get(0).shapeArr.length > 0) {
                contextFooter.setVisibility(View.VISIBLE);
                setSelectedTab(CreateCollageActivity.INDEX_COLLAGE_INVISIBLE_VIEW);
                Log.e(CreateCollageActivity.TAG, "VISIBLE");
            }
            if (shapeIndex >= 0) {
                shapeLayoutList.get(currentCollageIndex).shapeArr[shapeIndex].bitmapMatrixgGetValues(matrixValues);
                mScaleFactor = matrixValues[BACKGROUND_PATTERN];
            }
            postInvalidate();
        }

        void unselectShapes() {
            contextFooter.setVisibility(INVISIBLE);
            shapeIndex = PATTERN_SENTINEL;
            Log.e(CreateCollageActivity.TAG, "unselectShapes");
            postInvalidate();
        }

        public void openFilterFragment() {
            selectFilterTextView.setVisibility(View.INVISIBLE);
            selectImageForAdj = false;
            if (shapeIndex >= 0) {
                fullEffectFragment.setBitmapWithParameter(bitmapList[shapeIndex], parameterList[shapeIndex]);
                setVisibilityOfFilterHorizontalListview(true);
            }
        }

        float getMatrixRotation(Matrix matrix) {
            matrix.getValues(values);
            return (float) Math.round(Math.atan2((double) values[INVALID_POINTER_ID], (double) values[BACKGROUND_PATTERN]) * 57.29577951308232d);
        }

        public void setBlurBitmap(int radius, boolean cascade) {
            if (blurBuilderNormal == null) {
                blurBuilderNormal = new ImageBlurNormal();
            }
            if (cascade) {
                backgroundMode = 2;
                if (!isScrapBook) {
                    seekbarSize.setProgress(seekbarSize.getMax());
                }
            } else {
                backgroundMode = 1;
            }

            Bitmap copyBitmap = bitmapList[0].copy(bitmapList[0].getConfig(), true);
            blurBitmap = NativeStackBlur.process(copyBitmap, radius);

            if (blurBitmap != null) {
                setBlurRect2((float) blurBitmap.getWidth(), (float) blurBitmap.getHeight());
            }
            postInvalidate();
        }

        void setBlurRect2(float btmwidth, float btmheight) {
            float w;
            float h;
            if ((mulY * btmwidth) / mulX < btmheight) {
                w = (float) ((int) btmwidth);
                h = (mulY * btmwidth) / mulX;
            } else {
                w = (((float) ((int) mulX)) * btmheight) / mulY;
                h = (float) ((int) btmheight);
            }
            int l = (int) ((btmwidth - w) / 2.0f);
            int t = (int) ((btmheight - h) / 2.0f);
            blurRectSrc.set(l, t, (int) (((float) l) + w), (int) (((float) t) + h));
        }
    }

    private void setVisibilityForSingleImage() {
        findViewById(R.id.seekbar_corner_container).setVisibility(View.GONE);
        findViewById(R.id.seekbar_space_container).setVisibility(View.GONE);
        findViewById(R.id.buttonBlur).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonDelete).setVisibility(View.GONE);
        findViewById(R.id.buttonSwap).setVisibility(View.GONE);
        if (!isScrapBook) {
            collageView.setCollageSize(collageView.sizeMatrix, 45);
            if (seekbarSize != null) {
                seekbarSize.setProgress(45);
            }
        }
        collageView.setBlurBitmap(collageView.blurRadius, false);
        if (!isScrapBook) {
            setSelectedTab(INDEX_COLLAGE_SPACE);
        }
    }

    void setSelectedTab(int index) {
        if (viewFlipper != null) {
            setTabBg(INDEX_COLLAGE);
            int displayedChild = viewFlipper.getDisplayedChild();
            if (displayedChild != INDEX_COLLAGE_BACKGROUND) {
                hideColorContainer();
            }
            if (index == 0) {
                if (displayedChild != 0) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                    viewFlipper.setDisplayedChild(INDEX_COLLAGE);
                } else {
                    return;
                }
            }
            if (index == INDEX_COLLAGE_BACKGROUND) {
                setTabBg(INDEX_COLLAGE_BACKGROUND);
                if (displayedChild != INDEX_COLLAGE_BACKGROUND) {
                    if (displayedChild == 0) {
                        viewFlipper.setInAnimation(slideRightIn);
                        viewFlipper.setOutAnimation(slideLeftOut);
                    } else {
                        viewFlipper.setInAnimation(slideLeftIn);
                        viewFlipper.setOutAnimation(slideRightOut);
                    }
                    viewFlipper.setDisplayedChild(1);
                } else {
                    return;
                }
            }
            if (index == INDEX_COLLAGE_BLUR) {
                setTabBg(INDEX_COLLAGE_BLUR);
                if (displayedChild != INDEX_COLLAGE_BLUR) {
                    if (displayedChild == 0) {
                        viewFlipper.setInAnimation(slideRightIn);
                        viewFlipper.setOutAnimation(slideLeftOut);
                    } else {
                        viewFlipper.setInAnimation(slideLeftIn);
                        viewFlipper.setOutAnimation(slideRightOut);
                    }
                    viewFlipper.setDisplayedChild(INDEX_COLLAGE_BLUR);
                } else {
                    return;
                }
            }
            if (index == INDEX_COLLAGE_SPACE) {
                setTabBg(INDEX_COLLAGE_SPACE);
                if (displayedChild != INDEX_COLLAGE_SPACE) {
                    if (displayedChild == 0 || displayedChild == INDEX_COLLAGE_BACKGROUND) {
                        viewFlipper.setInAnimation(slideRightIn);
                        viewFlipper.setOutAnimation(slideLeftOut);
                    } else {
                        viewFlipper.setInAnimation(slideLeftIn);
                        viewFlipper.setOutAnimation(slideRightOut);
                    }
                    viewFlipper.setDisplayedChild(INDEX_COLLAGE_SPACE);
                } else {
                    return;
                }
            }
            if (index == INDEX_COLLAGE_RATIO) {
                setTabBg(INDEX_COLLAGE_RATIO);
                if (displayedChild != INDEX_COLLAGE_RATIO) {
                    if (displayedChild == INDEX_COLLAGE_INVISIBLE_VIEW) {
                        viewFlipper.setInAnimation(slideLeftIn);
                        viewFlipper.setOutAnimation(slideRightOut);
                    } else {
                        viewFlipper.setInAnimation(slideRightIn);
                        viewFlipper.setOutAnimation(slideLeftOut);
                    }
                    viewFlipper.setDisplayedChild(INDEX_COLLAGE_RATIO);
                } else {
                    return;
                }
            }
            if (index == INDEX_COLLAGE_INVISIBLE_VIEW) {
                setTabBg(-1);
                if (displayedChild != INDEX_COLLAGE_INVISIBLE_VIEW) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                    viewFlipper.setDisplayedChild(INDEX_COLLAGE_INVISIBLE_VIEW);
                }
            }
        }
    }

    private void setTabBg(int index) {
        if (tabButtonList == null) {
            tabButtonList = new View[TAB_SIZE];
            tabButtonList[INDEX_COLLAGE] = findViewById(R.id.buttonCollageLayout);
            tabButtonList[INDEX_COLLAGE_SPACE] = findViewById(R.id.buttonSpace);
            tabButtonList[INDEX_COLLAGE_BLUR] = findViewById(R.id.buttonBlur);
            tabButtonList[INDEX_COLLAGE_BACKGROUND] = findViewById(R.id.buttonBackground);
            tabButtonList[INDEX_COLLAGE_RATIO] = findViewById(R.id.buttonRatio);
            tabButtonList[INDEX_COLLAGE_INVISIBLE_VIEW] = findViewById(R.id.buttonAdjustment);
        }
        for (int i = INDEX_COLLAGE; i < tabButtonList.length; i += INDEX_COLLAGE_BACKGROUND) {
            tabButtonList[i].setBackgroundResource(R.drawable.collage_footer_button);
        }
        if (index >= 0) {
            tabButtonList[index].setBackgroundResource(R.color.footer_button_color_pressed);
        }
    }

    void setVisibilityOfFilterHorizontalListview(boolean show) {
        if (show && fullEffectFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().show(fullEffectFragment).commit();
        }
        if (!show && fullEffectFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(fullEffectFragment).commit();
        }
        findViewById(R.id.collage_effect_fragment_container).bringToFront();
    }

    private void hideColorContainer() {
        if (colorContainer == null) {
            colorContainer = (LinearLayout) findViewById(R.id.color_container);
        }
        colorContainer.setVisibility(View.INVISIBLE);
    }

    void createDeleteDialog() {
        if (collageView.shapeLayoutList.get(INDEX_COLLAGE).shapeArr.length == INDEX_COLLAGE_BACKGROUND) {
            Toast msg = Toast.makeText(CreateCollageActivity.this, "You can't delete last image!", Toast.LENGTH_SHORT);
            msg.setGravity(Gravity.CENTER, msg.getXOffset() / INDEX_COLLAGE_SPACE, msg.getYOffset() / INDEX_COLLAGE_SPACE);
            msg.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCollageActivity.this);
        builder.setMessage("Do you want to delete it?").setCancelable(true).setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                collageView.deleteBitmap(collageView.shapeIndex, width, height);
            }
        }).setNegativeButton("No", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        saveImageAlert = builder.create();
        saveImageAlert.show();
    }

}
