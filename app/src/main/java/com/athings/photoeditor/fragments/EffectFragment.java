package com.athings.photoeditor.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.athings.photoeditor.R;
import com.athings.photoeditor.adapter.MyRecyclerViewAdapter;
import com.athings.photoeditor.utils.MyAsyncTask;
import com.athings.photoeditor.utils.Parameter;
import com.athings.photoeditor.widget.SeekBarHint;
import com.athings.photoeditor.utils.LibUtility;
import com.commit451.nativestackblur.NativeStackBlur;

public class EffectFragment extends Fragment {
    public static final int INDEX_BLUR = 10;
    public static final int INDEX_BRIGHTNESS = 4;
    public static final int INDEX_CONTRAST = 6;
    public static final int INDEX_FRAME = 1;
    public static final int INDEX_FX = 0;
    public static final int INDEX_LIGHT = 2;
    public static final int INDEX_SATURATION = 7;
    public static final int INDEX_SHARPEN = 9;
    public static final int INDEX_TEXTURE = 3;
    public static final int INDEX_TINT = 8;
    public static final int INDEX_WARMTH = 5;
    public static final int TAB_SIZE = 14;
    static final String TAG = "EffectFragment";
    private static String[] filterBitmapTitle = new String[]{"None", "Gray", "Sepia", "Joey", "Sancia", "Blair", "Sura", "Tara", "Summer", "Penny", "Cuddy", "Cameron", "Lemon", "Tanya", "Lorelai", "Quinn", "Izabella", "Amber", "Cersei", "Debra", "Ellen", "Gabrielle", "Arya"};
    public Paint grayPaint;
    public Paint sepiaPaint;
    public Paint polaroidPaint;
    public Paint invertPaint;
    public Paint scrimPaint;
    public Paint abcPaint;
    public Paint abc1Paint;
    public Paint abc2Paint;
    public Paint abc3Paint;
    public Paint abc4Paint;
    public Paint purplePaint;
    public Paint yellowPaint;
    public Paint cyanPaint;
    public Paint bwPaint;
    public Paint oldtimesPaint;
    public Paint coldlifePaint;
    public Paint sepiumPaint;
    public Paint milkPaint;
    public Paint limePaint;
    public Paint peachyPaint;
    public Paint lightenPaint;
    public Paint darkenPaint;
    Activity activity;
    Button buttonAdjustmentLabel;
    int bitmapHeight;
    BitmapReady bitmapReadyListener;
    Bitmap bitmapTiltBlur;
    int bitmapWidth;
    MyRecyclerViewAdapter borderAdapter;
    MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener borderIndexChangedListener = null;
    Context context;
    LibUtility.ExcludeTabListener excludeTabListener;
    MyRecyclerViewAdapter filterAdapter;
    Bitmap filterBitmap;
    LibUtility.FooterVisibilityListener footerListener;
    FilterAndAdjustmentTask ft;
    FullEffectFragment.HideHeaderListener hideHeaderListener;
    boolean inFilterAndAdjustment = false;
    MyRecyclerViewAdapter overlayAdapter;
    Parameter parameterBackUp = new Parameter();
    ;
    public Parameter parameterGlobal;
    SeekBar seekbarAdjustment;
    Rect seekbarHintTextBounds = new Rect();
    ;
    LayoutParams seekbarHintTextLayoutParams;
    int selectedTab = 0;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private Bitmap sourceBitmap;
    Button[] tabButtonList;
    TextView textHint;
    MyRecyclerViewAdapter textureAdapter;
    int thumbSize;
    ViewFlipper viewFlipper;
    private ViewSwitcher viewSwitcher;
    OnSeekBarChangeListener mySeekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            if (textHint == null) {
                textHint = (TextView) getView().findViewById(R.id.seekbar_hint);
            }
            if (seekbarHintTextLayoutParams == null) {
                seekbarHintTextLayoutParams = (LayoutParams) textHint.getLayoutParams();
            }
            Rect thumbRect = ((SeekBarHint) seekBar).getSeekBarThumb().getBounds();
            textHint.setText(String.valueOf(progress));
            textHint.getPaint().getTextBounds(textHint.getText().toString(), INDEX_FX, textHint.getText().length(), seekbarHintTextBounds);
            seekbarHintTextLayoutParams.setMargins(thumbRect.centerX() - (seekbarHintTextBounds.width() / INDEX_LIGHT), INDEX_FX, EffectFragment.INDEX_FX, EffectFragment.INDEX_FX);
            textHint.setLayoutParams(seekbarHintTextLayoutParams);
            if (parameterGlobal.seekBarMode == 0) {
                parameterGlobal.setBrightness(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_FRAME) {
                parameterGlobal.setContrast(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_LIGHT) {
                parameterGlobal.setTemperature(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_TEXTURE) {
                parameterGlobal.setSaturation(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_BRIGHTNESS) {
                parameterGlobal.setTint(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_WARMTH) {
                parameterGlobal.setSharpen(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_CONTRAST) {
                parameterGlobal.setBlur(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_SATURATION) {
                parameterGlobal.setHighlight(progress);
            } else if (parameterGlobal.seekBarMode == INDEX_TINT) {
                parameterGlobal.setShadow(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (textHint == null) {
                textHint = (TextView) getView().findViewById(R.id.seekbar_hint);
            }
            textHint.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (textHint == null) {
                textHint = (TextView) getView().findViewById(R.id.seekbar_hint);
            }
            textHint.setVisibility(View.INVISIBLE);
            callBack();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thumbSize = (int) getResources().getDimension(R.dimen.lib_thumb_save_size);
        return inflater.inflate(R.layout.horizontal_fragment_effect, container, false);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(getString(R.string.effect_parameter_bundle_name), parameterGlobal);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            parameterGlobal = savedInstanceState.getParcelable(getString(R.string.effect_parameter_bundle_name));
        } else if (getArguments() != null) {
            parameterGlobal = getArguments().getParcelable(getString(R.string.effect_parameter_bundle_name));
        }
        if (parameterGlobal == null) {
            parameterGlobal = new Parameter();
        }
        context = getActivity();
        activity = getActivity();
        initPaints();
        initAdapters();
        viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.viewswitcher);
        Log.e(TAG, "viewSwitcher getDisplayedChild" + viewSwitcher.getDisplayedChild());
        viewFlipper = (ViewFlipper) getView().findViewById(R.id.control_container);
        slideLeftIn = AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
        slideLeftOut = AnimationUtils.loadAnimation(activity, R.anim.slide_out_left);
        slideRightIn = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
        slideRightOut = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
        buttonAdjustmentLabel = (Button) getView().findViewById(R.id.buttonAdjustmentLabel);
        setSelectedTab(selectedTab);
        viewSwitcher.setDisplayedChild(INDEX_FRAME);
        setTabBg(selectedTab);
        if (excludeTabListener != null) {
            excludeTabListener.exclude();
        }
        if (footerListener != null) {
            footerListener.setVisibility();
        }
        seekbarAdjustment = (SeekBar) getView().findViewById(R.id.seekbarAdjustment);
        seekbarAdjustment.setOnSeekBarChangeListener(mySeekBarListener);
    }

    public void initPaints() {

        sepiaPaint = new Paint();
        ColorMatrix sepiaMatrix = new ColorMatrix();
        sepiaMatrix.set(new float[]{0.393f, 0.769f, 0.189f, 0.0f, 0.0f, 0.349f, 0.686f, 0.168f, 0.0f, 0.0f, 0.272f, 0.534f, 0.131f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        sepiaPaint.setColorFilter(new ColorMatrixColorFilter(sepiaMatrix));

        grayPaint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0f);
        grayPaint.setColorFilter(new ColorMatrixColorFilter(cm));

        invertPaint = new Paint();
        ColorMatrix invertMatrix = new ColorMatrix();
        invertMatrix.set(new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0}));
        invertPaint.setColorFilter(new ColorMatrixColorFilter(invertMatrix));

        polaroidPaint = new Paint();
        ColorMatrix polaroidMatrix = new ColorMatrix();
        polaroidMatrix.set(new ColorMatrix(new float[]{
                2, 0, 0, 0, -130,
                0, 2, 0, 0, -130,
                0, 0, 2, 0, -130,
                0, 0, 0, 1, 0}));
        polaroidPaint.setColorFilter(new ColorMatrixColorFilter(polaroidMatrix));

        scrimPaint = new Paint();
        ColorMatrix scrimMatrix = new ColorMatrix();
        scrimMatrix.set(new ColorMatrix(new float[]{
                5, 0, 0, 0, -254,
                0, 5, 0, 0, -254,
                0, 0, 5, 0, -254,
                0, 0, 0, 1, 0}));
        scrimPaint.setColorFilter(new ColorMatrixColorFilter(scrimMatrix));

        abcPaint = new Paint();
        ColorMatrix abcMatrix = new ColorMatrix();
        abcMatrix.set(new ColorMatrix(new float[]{
                0, 0, 1, 0, 0,
                0, 1, 0, 0, 0,
                1, 0, 0, 0, 0,
                0, 0, 0, 1, 0}));
        abcPaint.setColorFilter(new ColorMatrixColorFilter(abcMatrix));

        abc1Paint = new Paint();
        ColorMatrix abc1Matrix = new ColorMatrix();
        abc1Matrix.set(new ColorMatrix(new float[]{
                -0.36f, 1.691f, -0.32f, 0, 0,
                0.325f, 0.398f, 0.275f, 0, 0,
                0.79f, 0.796f, -0.76f, 0, 0,
                0, 0, 0, 1, 0}));
        abc1Paint.setColorFilter(new ColorMatrixColorFilter(abc1Matrix));

        abc2Paint = new Paint();
        ColorMatrix abc2Matrix = new ColorMatrix();
        abc2Matrix.set(new ColorMatrix(new float[]{
                -0.41f, 0.539f, -0.873f, 0, 0,
                0.452f, 0.666f, -0.11f, 0, 0,
                -0.3f, 1.71f, -0.4f, 0, 0,
                0, 0, 0, 1, 0}));
        abc2Paint.setColorFilter(new ColorMatrixColorFilter(abc2Matrix));

        abc3Paint = new Paint();
        ColorMatrix abc3Matrix = new ColorMatrix();
        abc3Matrix.set(new ColorMatrix(new float[]{
                3.074f, -1.82f, -0.24f, 0, 50.8f,
                -0.92f, 2.171f, -0.24f, 0, 50.8f,
                -0.92f, -1.82f, 3.754f, 0, 50.8f,
                0, 0, 0, 1, 0}));
        abc3Paint.setColorFilter(new ColorMatrixColorFilter(abc3Matrix));

        abc4Paint = new Paint();
        ColorMatrix abc4Matrix = new ColorMatrix();
        abc4Matrix.set(new ColorMatrix(new float[]{
                0.14f, 0.45f, 0.05f, 0, 0,
                0.12f, 0.39f, 0.04f, 0, 0,
                0.08f, 0.28f, 0.03f, 0, 0,
                0, 0, 0, 1, 0}));
        abc4Paint.setColorFilter(new ColorMatrixColorFilter(abc4Matrix));

        purplePaint = new Paint();
        ColorMatrix purpleMatrix = new ColorMatrix();
        purpleMatrix.set(new ColorMatrix(new float[]{
                1, -0.2f, 0, 0, 0,
                0, 1, 0, -0.1f, 0,
                0, 1.2f, 1, 0.1f, 0,
                0, 0, 1.7f, 1, 0}));
        purplePaint.setColorFilter(new ColorMatrixColorFilter(purpleMatrix));


        yellowPaint = new Paint();
        ColorMatrix yellowMatrix = new ColorMatrix();
        yellowMatrix.set(new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                -0.2f, 1, 0.3f, 0.1f, 0,
                -3, 0, 1, 0, 0,
                0, 0, 0, 1, 0}));
        yellowPaint.setColorFilter(new ColorMatrixColorFilter(yellowMatrix));

        cyanPaint = new Paint();
        ColorMatrix cyanMatrix = new ColorMatrix();
        cyanMatrix.set(new ColorMatrix(new float[]{
                1, 0, 0, 1.9f, -2.2f,
                0, 1, 0, 0.0f, 0.3f,
                3, 0, 1, 0, 0.5f,
                0, 0, 0, 1, 0.2f}));
        cyanPaint.setColorFilter(new ColorMatrixColorFilter(cyanMatrix));

        bwPaint = new Paint();
        ColorMatrix bwMatrix = new ColorMatrix();
        bwMatrix.set(new ColorMatrix(new float[]{
                0, 1, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 1, 0, 1, 0}));
        bwPaint.setColorFilter(new ColorMatrixColorFilter(bwMatrix));


        oldtimesPaint = new Paint();
        ColorMatrix oldtimesMatrix = new ColorMatrix();
        oldtimesMatrix.set(new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                -0.4f, 1.3f, -0.4f, 0.2f, -0.1f,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0}));
        oldtimesPaint.setColorFilter(new ColorMatrixColorFilter(oldtimesMatrix));

        coldlifePaint = new Paint();
        ColorMatrix coldlifeMatrix = new ColorMatrix();
        coldlifeMatrix.set(new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                -0.2f, 0.2f, 0.1f, 0.4f, 0,
                0, 0, 0, 1, 0}));
        coldlifePaint.setColorFilter(new ColorMatrixColorFilter(coldlifeMatrix));

        sepiumPaint = new Paint();
        ColorMatrix sepiumMatrix = new ColorMatrix();
        sepiumMatrix.set(new ColorMatrix(new float[]{
                1.3f, -0.3f, 1.1f, 0, 0,
                0, 1.3f, 0.2f, 0, 0,
                0, 0, 0.8f, 0.2f, 0,
                0, 0, 0, 1, 0}));
        sepiumPaint.setColorFilter(new ColorMatrixColorFilter(sepiumMatrix));

        milkPaint = new Paint();
        ColorMatrix milkMatrix = new ColorMatrix();
        milkMatrix.set(new ColorMatrix(new float[]{
                0, 1, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0.6f, 1, 0, 0,
                0, 0, 0, 1, 0}));
        milkPaint.setColorFilter(new ColorMatrixColorFilter(milkMatrix));

        limePaint = new Paint();
        ColorMatrix limeMatrix = new ColorMatrix();
        limeMatrix.set(new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                0, 2, 0, 0, 0,
                0, 0, 0, 0.5f, 0,
                0, 0, 0, 1, 0}));
        limePaint.setColorFilter(new ColorMatrixColorFilter(limeMatrix));

        peachyPaint = new Paint();
        ColorMatrix peachyMatrix = new ColorMatrix();
        peachyMatrix.set(new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                0, 0.5f, 0, 0, 0,
                0, 0, 0, 0.5f, 0,
                0, 0, 0, 1, 0}));
        peachyPaint.setColorFilter(new ColorMatrixColorFilter(peachyMatrix));

        lightenPaint = new Paint();
        ColorMatrix lightenMatrix = new ColorMatrix();
        lightenMatrix.set(new ColorMatrix(new float[]{
                1.5f, 0, 0, 0, 0,
                0, 1.5f, 0, 0, 0,
                0, 0, 1.5f, 0, 0,
                0, 0, 0, 1, 0}));
        lightenPaint.setColorFilter(new ColorMatrixColorFilter(lightenMatrix));

        darkenPaint = new Paint();
        ColorMatrix darkenMatrix = new ColorMatrix();
        darkenMatrix.set(new ColorMatrix(new float[]{
                .5f, 0, 0, 0, 0,
                0, .5f, 0, 0, 0,
                0, 0, .5f, 0, 0,
                0, 0, 0, 1, 0}));
        darkenPaint.setColorFilter(new ColorMatrixColorFilter(darkenMatrix));

    }

    private void initAdapters() {

        MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener borderL;
        MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener c09632 = new MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener() {
            @Override
            public void onIndexChanged(int i) {
                applyChangesOnBitmap();
            }
        };
        if (borderIndexChangedListener != null) {
            borderL = borderIndexChangedListener;
        } else {
            borderL = c09632;
        }
        borderAdapter = new MyRecyclerViewAdapter(LibUtility.borderResThumb, borderL, R.color.bg, R.color.footer_button_color_pressed, 100);
        borderAdapter.setSelectedIndexChangedListener(new MyRecyclerViewAdapter.SelectedIndexChangedListener() {
            @Override
            public void selectedIndexChanged(int i) {
                Log.e(EffectFragment.TAG, "selectedIndexChanged " + i);
                parameterGlobal.selectedBorderIndex = i;
            }
        });
        textureAdapter = new MyRecyclerViewAdapter(LibUtility.textureResThumb, new MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener() {
            @Override
            public void onIndexChanged(int i) {
                applyChangesOnBitmap();
            }
        }, R.color.bg, R.color.footer_button_color_pressed, 100);
        textureAdapter.setSelectedIndexChangedListener(new MyRecyclerViewAdapter.SelectedIndexChangedListener() {
            @Override
            public void selectedIndexChanged(int i) {
                parameterGlobal.selectedTextureIndex = i;
            }
        });
        overlayAdapter = new MyRecyclerViewAdapter(LibUtility.overlayResThumb, new MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener() {
            @Override
            public void onIndexChanged(int i) {
                applyChangesOnBitmap();
            }
        }, R.color.bg, R.color.footer_button_color_pressed, 100);
        overlayAdapter.setSelectedIndexChangedListener(new MyRecyclerViewAdapter.SelectedIndexChangedListener() {
            @Override
            public void selectedIndexChanged(int i) {
                parameterGlobal.selectedOverlayIndex = i;
            }
        });
        filterAdapter = new MyRecyclerViewAdapter(LibUtility.filterResThumb, new MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener() {
            @Override
            public void onIndexChanged(int i) {
                applyChangesOnBitmap();
            }
        }, R.color.bg, R.color.footer_button_color_pressed, 100);
        filterAdapter.setSelectedIndexChangedListener(new MyRecyclerViewAdapter.SelectedIndexChangedListener() {
            @Override
            public void selectedIndexChanged(int i) {
                parameterGlobal.selectedFilterIndex = i;
            }
        });
        RecyclerView recyclerViewBorder = (RecyclerView) getView().findViewById(R.id.recyclerViewBorder);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewBorder.setLayoutManager(linearLayoutManager);
        recyclerViewBorder.setAdapter(borderAdapter);
        recyclerViewBorder.setItemAnimator(new DefaultItemAnimator());
        RecyclerView recyclerViewTexture = (RecyclerView) getView().findViewById(R.id.recyclerViewTexture);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTexture.setLayoutManager(linearLayoutManager);
        recyclerViewTexture.setAdapter(textureAdapter);
        recyclerViewTexture.setItemAnimator(new DefaultItemAnimator());
        RecyclerView recyclerViewOverlay = (RecyclerView) getView().findViewById(R.id.recyclerViewOverlay);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(INDEX_FX);
        recyclerViewOverlay.setLayoutManager(linearLayoutManager);
        recyclerViewOverlay.setAdapter(overlayAdapter);
        recyclerViewOverlay.setItemAnimator(new DefaultItemAnimator());
        RecyclerView recyclerViewFilter = (RecyclerView) getView().findViewById(R.id.recyclerViewFilter);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(INDEX_FX);
        recyclerViewFilter.setLayoutManager(linearLayoutManager);
        recyclerViewFilter.setAdapter(filterAdapter);
        recyclerViewFilter.setItemAnimator(new DefaultItemAnimator());
        textureAdapter.setSelectedView(parameterGlobal.selectedTextureIndex);
        borderAdapter.setSelectedView(parameterGlobal.selectedBorderIndex);
        overlayAdapter.setSelectedView(parameterGlobal.selectedOverlayIndex);
        if (parameterGlobal.selectedFilterIndex >= filterAdapter.getItemCount()) {
            parameterGlobal.selectedFilterIndex = INDEX_FX;
        }
        filterAdapter.setSelectedView(parameterGlobal.selectedFilterIndex);
    }

    private void setTabBg(int index) {
        if (tabButtonList == null) {
            tabButtonList = new Button[TAB_SIZE];
            tabButtonList[INDEX_FX] = (Button) getView().findViewById(R.id.buttonFX);
            tabButtonList[INDEX_FRAME] = (Button) getView().findViewById(R.id.buttonFrame);
            tabButtonList[INDEX_LIGHT] = (Button) getView().findViewById(R.id.buttonLight);
            tabButtonList[INDEX_TEXTURE] = (Button) getView().findViewById(R.id.buttonTexture);
            tabButtonList[INDEX_BLUR] = (Button) getView().findViewById(R.id.buttonBlur);
        }
        if (index >= 0) {
            buttonAdjustmentLabel.setText(tabButtonList[index].getText());
        }
    }

    void setSelectedTab(int index) {
        viewSwitcher.setDisplayedChild(INDEX_FX);
        int displayedChild = viewFlipper.getDisplayedChild();
        if (index == 0) {
            setTabBg(INDEX_FX);
            if (displayedChild != 0) {
                viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideRightOut);
                viewFlipper.setDisplayedChild(INDEX_FX);
            } else {
                return;
            }
        }
        if (index == INDEX_FRAME) {
            setTabBg(INDEX_FRAME);
            if (displayedChild != INDEX_FRAME) {
                if (displayedChild == 0) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                } else {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                }
                viewFlipper.setDisplayedChild(INDEX_FRAME);
            } else {
                return;
            }
        }
        if (index == INDEX_LIGHT) {
            setTabBg(INDEX_LIGHT);
            if (displayedChild != INDEX_LIGHT) {
                if (displayedChild == INDEX_TEXTURE || displayedChild == INDEX_BRIGHTNESS) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                } else {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                }
                viewFlipper.setDisplayedChild(INDEX_LIGHT);
            } else {
                return;
            }
        }
        if (index == INDEX_TEXTURE) {
            setTabBg(INDEX_TEXTURE);
            if (displayedChild != INDEX_TEXTURE) {
                if (displayedChild == INDEX_BRIGHTNESS) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                } else {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                }
                viewFlipper.setDisplayedChild(INDEX_TEXTURE);
            } else {
                return;
            }
        }
        if (index == INDEX_BRIGHTNESS || index == INDEX_CONTRAST || index == INDEX_SATURATION || index == INDEX_WARMTH || index == INDEX_TINT || index == INDEX_SHARPEN || index == INDEX_BLUR) {
            setTabBg(index);
            if (displayedChild != INDEX_BRIGHTNESS) {
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.setDisplayedChild(INDEX_BRIGHTNESS);
            }
        }
    }

    public interface BitmapReady {
        void onBitmapReady(Bitmap bitmap);
    }

    class FilterAndAdjustmentTask extends MyAsyncTask<Void, Void, Void> {
        int lastBlurRadius;
        Matrix matrixBlur;
        Paint paintBlur;
        ProgressDialog progressDialog;

        FilterAndAdjustmentTask() {
            matrixBlur = new Matrix();
            paintBlur = new Paint(EffectFragment.INDEX_LIGHT);
            lastBlurRadius = -1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inFilterAndAdjustment = true;
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Void doInBackground(Void... arg0) {
            if (isAdded()) {
                if (filterBitmap == null) {
                    filterBitmap = sourceBitmap.copy(Config.ARGB_8888, true);
                } else {
                    new Canvas(filterBitmap).drawBitmap(sourceBitmap, 0.0f, 0.0f, new Paint());
                }
                Canvas cvs = new Canvas(filterBitmap);
                cvs.drawBitmap(sourceBitmap, 0.0f, 0.0f, new Paint());
                if (parameterGlobal.blur > 0) {
                    if (android.os.Build.VERSION.SDK_INT > 17) {
                        Bitmap copyBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
                        filterBitmap = NativeStackBlur.process(copyBitmap, parameterGlobal.blur);
                    }
                }
                if (isAdded()) {
                    pipeline(filterBitmap);
                } else {
                    cancel(true);
                    inFilterAndAdjustment = false;
                }
            } else {
                inFilterAndAdjustment = false;
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            inFilterAndAdjustment = false;
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isAdded()) {
                bitmapReadyListener.onBitmapReady(filterBitmap);
            }
        }

        void pipeline(Bitmap btmPipe) {

            if (parameterGlobal.selectedFilterIndex <= 22) {
                setFilter(parameterGlobal.selectedFilterIndex, btmPipe);
            }
            Bitmap overlayBitmap = getOverlayBitmap(parameterGlobal.selectedOverlayIndex);
            if (!(overlayBitmap == null || overlayBitmap.isRecycled())) {
                if (VERSION.SDK_INT > EffectFragment.INDEX_BLUR) {
                    applyOverlay11(overlayBitmap, btmPipe, isOverlayScreenMode(parameterGlobal.selectedOverlayIndex));
                } else if (EffectFragment.isOverlayScreenMode(overlayAdapter.getSelectedIndex()) == 0) {
                } else {
                    applyOverlay11(overlayBitmap, btmPipe, isOverlayScreenMode(parameterGlobal.selectedOverlayIndex));
                }
            }
            filterMultiply(btmPipe, parameterGlobal.selectedTextureIndex, false);
            if (borderIndexChangedListener == null) {
                setBorder(btmPipe, parameterGlobal.selectedBorderIndex, false);
            }
            Canvas cvs = new Canvas(btmPipe);
            if (parameterGlobal.selectedFilterIndex < 22) {
                cvs.drawBitmap(btmPipe, 0, 0, new Paint());
            }
        }
    }

    public void setBitmapReadyListener(BitmapReady listener) {
        bitmapReadyListener = listener;
    }

    public void setHideHeaderListener(FullEffectFragment.HideHeaderListener l) {
        hideHeaderListener = l;
    }

    public boolean showToolBar() {
        if (viewSwitcher.getDisplayedChild() != 0) {
            return false;
        }
        cancelViewSwitcher();
        viewSwitcher.setDisplayedChild(INDEX_FRAME);
        return true;
    }


    public void onAttach(Activity act) {
        super.onAttach(act);
        context = getActivity();
        activity = getActivity();
    }


    public void setBorderIndexChangedListener(MyRecyclerViewAdapter.RecyclerAdapterIndexChangedListener l) {
        borderIndexChangedListener = l;
    }


    public void setSelectedTabIndex(int index) {
        if (index >= 0 && index < TAB_SIZE) {
            selectedTab = index;
            if (getView() != null) {
                setSelectedTab(index);
            }
        }
    }

    public int getSelectedTabIndex() {
        if (viewFlipper != null) {
            return viewFlipper.getDisplayedChild();
        }
        return -1;
    }

    void setSeekBarProgress() {
        int progress = 50;
        if (this.parameterGlobal.seekBarMode == 0) {
            progress = parameterGlobal.getBrightProgress();
        } else if (parameterGlobal.seekBarMode == INDEX_FRAME) {
            progress = parameterGlobal.getContrastProgress();
        } else if (parameterGlobal.seekBarMode == INDEX_LIGHT) {
            progress = parameterGlobal.getTemperatureProgress();
        } else if (parameterGlobal.seekBarMode == INDEX_TEXTURE) {
            progress = parameterGlobal.saturation;
        } else if (parameterGlobal.seekBarMode == INDEX_BRIGHTNESS) {
            progress = parameterGlobal.getTintProgressValue();
        } else if (parameterGlobal.seekBarMode == INDEX_WARMTH) {
            progress = parameterGlobal.getSharpenValue();
        } else if (parameterGlobal.seekBarMode == INDEX_CONTRAST) {
            progress = parameterGlobal.getBlurValue();
        } else if (parameterGlobal.seekBarMode == INDEX_SATURATION) {
            progress = parameterGlobal.getHighlightValue();
        } else if (parameterGlobal.seekBarMode == INDEX_TINT) {
            progress = parameterGlobal.getShadowValue();
        }
        seekbarAdjustment.setProgress(progress);
    }

    public void callBack() {
        execQueue();
    }

    public void setBitmap(Bitmap btm) {
        sourceBitmap = btm;
        bitmapWidth = sourceBitmap.getWidth();
        bitmapHeight = sourceBitmap.getHeight();
        filterBitmap = null;
    }

    public void setBitmapAndResetBlur(Bitmap btm) {
        setBitmap(btm);
        Log.e(TAG, "setBitmapAndResetBlur setBitmapAndResetBlur");
        if (!(bitmapTiltBlur == null || bitmapTiltBlur.isRecycled())) {
            bitmapTiltBlur.recycle();
        }
        bitmapTiltBlur = null;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    static int getBorderMode(int index) {
        return INDEX_FX;
    }

    public synchronized void setBorder(Bitmap btm, int index, boolean isThumb) {
        if (isAdded() && index != 0) {
            if (LibUtility.borderRes.length > index) {
                Bitmap borderBitmap;
                Paint paint = new Paint(INDEX_FRAME);
                if (getBorderMode(index) == INDEX_FRAME) {
                    paint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
                }
                Matrix borderMatrix = new Matrix();
                if (isThumb) {
                    borderBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.borderResThumb[index]);
                } else {
                    borderBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.borderRes[index]);
                }
                float wScale = ((float) btm.getWidth()) / ((float) borderBitmap.getWidth());
                float hScale = ((float) btm.getHeight()) / ((float) borderBitmap.getHeight());
                borderMatrix.reset();
                Canvas cvs = new Canvas(btm);
                borderMatrix.postScale(wScale, hScale);
                cvs.drawBitmap(borderBitmap, borderMatrix, paint);
                if (!(borderBitmap == null || btm == borderBitmap)) {
                    borderBitmap.recycle();
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    public void filterMultiply(Bitmap btm, int index, boolean isThumb) {
        if (index != 0 && isAdded()) {
            Bitmap textureBitmap;
            Paint paint = new Paint(INDEX_FRAME);
            Mode mode = Mode.SCREEN;
            if (LibUtility.textureModes[index] == LibUtility.MODE_MULTIPLY) {
                mode = Mode.MULTIPLY;
            } else if (LibUtility.textureModes[index] == LibUtility.MODE_OVERLAY && VERSION.SDK_INT > INDEX_BLUR) {
                mode = Mode.OVERLAY;
            } else if (LibUtility.textureModes[index] == LibUtility.MODE_OVERLAY && VERSION.SDK_INT <= INDEX_BLUR) {
                mode = Mode.MULTIPLY;
            }
            paint.setXfermode(new PorterDuffXfermode(mode));
            Matrix borderMatrix = new Matrix();
            if (isThumb) {
                textureBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.textureResThumb[index]);
            } else {
                Options o2 = new Options();
                if (LibUtility.getLeftSizeOfMemory() > 1.024E7d) {
                    o2.inSampleSize = INDEX_FRAME;
                } else {
                    o2.inSampleSize = INDEX_LIGHT;
                }
                textureBitmap = BitmapFactory.decodeResource(getResources(), LibUtility.textureRes[index], o2);
            }
            float wScale = ((float) btm.getWidth()) / ((float) textureBitmap.getWidth());
            float hScale = ((float) btm.getHeight()) / ((float) textureBitmap.getHeight());
            borderMatrix.reset();
            Canvas cvs = new Canvas(btm);
            borderMatrix.postScale(wScale, hScale);
            cvs.drawBitmap(textureBitmap, borderMatrix, paint);
            if (textureBitmap != null && btm != textureBitmap) {
                textureBitmap.recycle();
            }
        }
    }

    Bitmap getOverlayBitmap(int index) {
        Bitmap bitmap = null;
        if (isAdded()) {
            Options opts = new Options();
            opts.inPreferredConfig = Config.ARGB_8888;
            if (LibUtility.getLeftSizeOfMemory() > 1.024E7d) {
                opts.inSampleSize = INDEX_FRAME;
            } else {
                opts.inSampleSize = INDEX_LIGHT;
            }
            if (index > 0 && index < LibUtility.overlayDrawableList.length) {
                Bitmap temp;
                bitmap = BitmapFactory.decodeResource(getResources(), LibUtility.overlayDrawableList[index], opts);
                if (bitmap.getConfig() != Config.ARGB_8888) {
                    temp = bitmap;
                    bitmap = bitmap.copy(Config.ARGB_8888, false);
                    if (bitmap != temp) {
                        temp.recycle();
                    }
                }
                int overlayWidth = bitmap.getWidth();
                int overlayHeight = bitmap.getHeight();
                if ((this.bitmapHeight > this.bitmapWidth && overlayHeight < overlayWidth) || (this.bitmapHeight < this.bitmapWidth && overlayHeight > overlayWidth)) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90.0f);
                    temp = bitmap;
                    bitmap = Bitmap.createBitmap(temp, INDEX_FX, INDEX_FX, temp.getWidth(), temp.getHeight(), matrix, true);
                    if (bitmap != temp) {
                        temp.recycle();
                    }
                }
            }
        }
        return bitmap;
    }

    static int isOverlayScreenMode(int index) {
        if (index == INDEX_LIGHT) {
            return INDEX_LIGHT;
        }
        return INDEX_FRAME;
    }

    public void myClickHandler(int id) {
        if (id != R.id.buttonCancel) {
            parameterBackUp.set(parameterGlobal);
        }
        if (id == R.id.buttonFX) {
            setSelectedTab(INDEX_FX);
        } else if (id == R.id.buttonFrame) {
            setSelectedTab(INDEX_FRAME);
        } else if (id == R.id.buttonLight) {
            setSelectedTab(INDEX_LIGHT);
        } else if (id == R.id.buttonTexture) {
            setSelectedTab(INDEX_TEXTURE);
        } else if (id == R.id.buttonReset) {
            resetParameters();
        } else if (id == R.id.buttonBlur) {
            setSelectedTab(INDEX_BLUR);
            parameterGlobal.seekBarMode = INDEX_CONTRAST;
            setSeekBarProgress();
        } else if (id == R.id.buttonCancel) {
            cancelViewSwitcher();
            this.viewSwitcher.setDisplayedChild(INDEX_FRAME);
        } else if (id == R.id.buttonOk) {
            this.viewSwitcher.setDisplayedChild(INDEX_FRAME);
        }
    }

    @SuppressLint({"NewApi"})
    public void applyOverlay11(Bitmap overlay, Bitmap btm, int screenMode) {
        Paint paint = new Paint(INDEX_FRAME);
        paint.setFilterBitmap(true);
        Mode mode = Mode.SCREEN;
        if (screenMode == 0) {
            mode = Mode.OVERLAY;
        }
        PorterDuffXfermode porterMode = new PorterDuffXfermode(mode);
        if (screenMode == INDEX_LIGHT) {
            porterMode = null;
        }
        paint.setXfermode(porterMode);
        Matrix borderMatrix = new Matrix();
        float wScale = ((float) btm.getWidth()) / ((float) overlay.getWidth());
        float hScale = ((float) btm.getHeight()) / ((float) overlay.getHeight());
        borderMatrix.reset();
        Canvas cvs = new Canvas(btm);
        borderMatrix.postScale(wScale, hScale);
        cvs.drawBitmap(overlay, borderMatrix, paint);

    }

    private void cancelViewSwitcher() {
        Log.e(TAG, "parameterGlobal borderAdapter index " + this.parameterGlobal.selectedBorderIndex);
        Log.e(TAG, "parameterBackUp index " + this.parameterBackUp.selectedBorderIndex);
        Log.e(TAG, "borderAdapter index " + this.borderAdapter.getSelectedIndex());
        if (this.parameterGlobal.isParameterReallyChanged(this.parameterBackUp)) {
            this.parameterGlobal.set(this.parameterBackUp);
            this.textureAdapter.setSelectedView(this.parameterGlobal.selectedTextureIndex);
            this.borderAdapter.setSelectedView(this.parameterGlobal.selectedBorderIndex);
            if (this.borderIndexChangedListener != null) {
                this.borderIndexChangedListener.onIndexChanged(this.parameterGlobal.selectedBorderIndex);
            }
            Log.e(TAG, "borderAdapter index " + this.borderAdapter.getSelectedIndex());
            this.overlayAdapter.setSelectedView(this.parameterGlobal.selectedOverlayIndex);
            if (this.parameterGlobal.selectedFilterIndex >= this.filterAdapter.getItemCount()) {
                this.parameterGlobal.selectedFilterIndex = INDEX_FX;
            }
            this.filterAdapter.setSelectedView(this.parameterGlobal.selectedFilterIndex);
            execQueue();
        }
    }

    void resetParameters() {
        this.parameterGlobal.reset();
        setAdjustmentSeekbarProgress();
    }

    public void setParameters(Parameter parameter) {
        this.parameterGlobal.set(parameter);
        setAdjustmentSeekbarProgress();
    }

    void resetParametersWithoutChange() {
        this.parameterGlobal.reset();
        setSelectedIndexes();
        setSeekBarProgress();
    }

    void setAdjustmentSeekbarProgress() {
        setSeekBarProgress();
        setSelectedIndexes();
        execQueue();
    }

    void setSelectedIndexes() {
        this.textureAdapter.setSelectedView(this.parameterGlobal.selectedTextureIndex);
        this.borderAdapter.setSelectedView(this.parameterGlobal.selectedBorderIndex);
        this.overlayAdapter.setSelectedView(this.parameterGlobal.selectedOverlayIndex);
        this.filterAdapter.setSelectedView(this.parameterGlobal.selectedFilterIndex);
    }

    void applyChangesOnBitmap() {
        parameterGlobal.selectedFilterIndex = filterAdapter.getSelectedIndex();
        parameterGlobal.selectedBorderIndex = borderAdapter.getSelectedIndex();
        parameterGlobal.selectedTextureIndex = textureAdapter.getSelectedIndex();
        parameterGlobal.selectedOverlayIndex = overlayAdapter.getSelectedIndex();
        execQueue();
    }

    public void execQueue() {
        if (this.ft == null || this.ft.getStatus() != MyAsyncTask.Status.RUNNING) {
            this.ft = new FilterAndAdjustmentTask();
            try {
                this.ft.execute();
            } catch (Exception e) {
            }
        }
    }

    public void setFilter(int index, Bitmap btm) {
        if (index >= filterBitmapTitle.length) {
            index = INDEX_FX;
        }
        index--;
        if (VERSION.SDK_INT != INDEX_SATURATION && index != -1) {
            if (index == 0) {
                new Canvas(btm).drawBitmap(btm, 0, 0, grayPaint);
            } else if (index == 1) {
                new Canvas(btm).drawBitmap(btm, 0, 0, sepiaPaint);
            } else if (index == 2) {
                new Canvas(btm).drawBitmap(btm, 0, 0, purplePaint);
            } else if (index == 3) {
                new Canvas(btm).drawBitmap(btm, 0, 0, yellowPaint);
            } else if (index == 4) {
                new Canvas(btm).drawBitmap(btm, 0, 0, milkPaint);
            } else if (index == 5) {
                new Canvas(btm).drawBitmap(btm, 0, 0, coldlifePaint);
            } else if (index == 6) {
                new Canvas(btm).drawBitmap(btm, 0, 0, bwPaint);
            } else if (index == 7) {
                new Canvas(btm).drawBitmap(btm, 0, 0, limePaint);
            } else if (index == 8) {
                new Canvas(btm).drawBitmap(btm, 0, 0, sepiumPaint);
            } else if (index == 9) {
                new Canvas(btm).drawBitmap(btm, 0, 0, oldtimesPaint);
            } else if (index == 10) {
                new Canvas(btm).drawBitmap(btm, 0, 0, cyanPaint);
            } else if (index == 11) {
                new Canvas(btm).drawBitmap(btm, 0, 0, polaroidPaint);
            } else if (index == 12) {
                new Canvas(btm).drawBitmap(btm, 0, 0, invertPaint);
            } else if (index == 13) {
                new Canvas(btm).drawBitmap(btm, 0, 0, abc1Paint);
            } else if (index == 14) {
                new Canvas(btm).drawBitmap(btm, 0, 0, abc4Paint);
            } else if (index == 15) {
                new Canvas(btm).drawBitmap(btm, 0, 0, lightenPaint);
            } else if (index == 16) {
                new Canvas(btm).drawBitmap(btm, 0, 0, abc3Paint);
            } else if (index == 17) {
                new Canvas(btm).drawBitmap(btm, 0, 0, scrimPaint);
            } else if (index == 18) {
                new Canvas(btm).drawBitmap(btm, 0, 0, abc2Paint);
            } else if (index == 19) {
                new Canvas(btm).drawBitmap(btm, 0, 0, darkenPaint);
            } else if (index == 20) {
                new Canvas(btm).drawBitmap(btm, 0, 0, abcPaint);
            } else if (index == 21) {
                new Canvas(btm).drawBitmap(btm, 0, 0, peachyPaint);
            }
        }
    }
}
