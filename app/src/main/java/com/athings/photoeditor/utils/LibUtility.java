package com.athings.photoeditor.utils;

import android.os.Debug;

import com.athings.photoeditor.R;

public class LibUtility {
    public static int MODE_MULTIPLY;
    public static int MODE_NONE;
    public static int MODE_OVERLAY;
    public static int MODE_SCREEN;
    public static int[] borderRes;
    public static int[] borderResThumb;
    public static int[] filterResThumb;
    public static int[] overlayDrawableList;
    public static int[] overlayResThumb;
    public static int[] textureModes;
    public static int[] textureRes;
    public static int[] textureResThumb;


    public interface ExcludeTabListener {
        void exclude();
    }

    public interface FooterVisibilityListener {
        void setVisibility();
    }

    static {
        borderRes = new int[]{-1, R.drawable.border_0, R.drawable.border_1, R.drawable.border_2, R.drawable.border_3, R.drawable.border_4, R.drawable.border_5, R.drawable.border_6, R.drawable.border_7, R.drawable.border_8, R.drawable.border_9, R.drawable.border_10, R.drawable.border_11, R.drawable.border_12, R.drawable.border_13, R.drawable.border_14, R.drawable.border_15, R.drawable.border_16, R.drawable.border_17, R.drawable.border_18, R.drawable.border_19, R.drawable.border_20, R.drawable.border_21, R.drawable.border_22, R.drawable.border_23, R.drawable.border_24, R.drawable.border_25, R.drawable.border_26, R.drawable.border_27, R.drawable.border_28, R.drawable.border_29, R.drawable.border_30, R.drawable.border_31, R.drawable.border_32, R.drawable.border_33, R.drawable.border_34, R.drawable.border_35, R.drawable.border_36, R.drawable.border_37, R.drawable.border_38, R.drawable.border_39, R.drawable.border_40, R.drawable.border_41, R.drawable.border_42, R.drawable.border_43, R.drawable.border_44};
        borderResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.border_0_thumb, R.drawable.border_1_thumb, R.drawable.border_2_thumb, R.drawable.border_3_thumb, R.drawable.border_4_thumb, R.drawable.border_5_thumb, R.drawable.border_6_thumb, R.drawable.border_7_thumb, R.drawable.border_8_thumb, R.drawable.border_9_thumb, R.drawable.border_10_thumb, R.drawable.border_11_thumb, R.drawable.border_12_thumb, R.drawable.border_13_thumb, R.drawable.border_14_thumb, R.drawable.border_15_thumb, R.drawable.border_16_thumb, R.drawable.border_17_thumb, R.drawable.border_18_thumb, R.drawable.border_19_thumb, R.drawable.border_20_thumb, R.drawable.border_21_thumb, R.drawable.border_22_thumb, R.drawable.border_23_thumb, R.drawable.border_24_thumb, R.drawable.border_25_thumb, R.drawable.border_26_thumb, R.drawable.border_27_thumb, R.drawable.border_28_thumb, R.drawable.border_29_thumb, R.drawable.border_30_thumb, R.drawable.border_31_thumb, R.drawable.border_32_thumb, R.drawable.border_33_thumb, R.drawable.border_34_thumb, R.drawable.border_35_thumb, R.drawable.border_36_thumb, R.drawable.border_37_thumb, R.drawable.border_38_thumb, R.drawable.border_39_thumb, R.drawable.border_40_thumb, R.drawable.border_41_thumb, R.drawable.border_42_thumb, R.drawable.border_43_thumb, R.drawable.border_44_thumb};
        overlayDrawableList = new int[]{-1, R.drawable.overlay_01, R.drawable.overlay_02, R.drawable.overlay_03, R.drawable.overlay_04, R.drawable.overlay_05, R.drawable.overlay_06, R.drawable.overlay_07, R.drawable.overlay_08, R.drawable.overlay_09, R.drawable.overlay_10, R.drawable.overlay_11, R.drawable.overlay_12, R.drawable.overlay_13, R.drawable.overlay_14, R.drawable.overlay_15, R.drawable.overlay_16, R.drawable.overlay_17, R.drawable.overlay_18, R.drawable.overlay_19, R.drawable.overlay_20, R.drawable.overlay_21, R.drawable.overlay_22, R.drawable.overlay_23, R.drawable.overlay_24, R.drawable.overlay_26, R.drawable.overlay_27, R.drawable.overlay_28};
        overlayResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.overlay_0_thumb, R.drawable.overlay_1_thumb, R.drawable.overlay_2_thumb, R.drawable.overlay_3_thumb, R.drawable.overlay_4_thumb, R.drawable.overlay_5_thumb, R.drawable.overlay_6_thumb, R.drawable.overlay_7_thumb, R.drawable.overlay_8_thumb, R.drawable.overlay_9_thumb, R.drawable.overlay_10_thumb, R.drawable.overlay_11_thumb, R.drawable.overlay_12_thumb, R.drawable.overlay_13_thumb, R.drawable.overlay_14_thumb, R.drawable.overlay_15_thumb, R.drawable.overlay_16_thumb, R.drawable.overlay_17_thumb, R.drawable.overlay_18_thumb, R.drawable.overlay_19_thumb, R.drawable.overlay_20_thumb, R.drawable.overlay_21_thumb, R.drawable.overlay_22_thumb, R.drawable.overlay_23_thumb, R.drawable.overlay_24_thumb, R.drawable.overlay_25_thumb, R.drawable.overlay_26_thumb};
        textureRes = new int[]{-1, R.drawable.texture_01, R.drawable.texture_03, R.drawable.texture_04, R.drawable.texture_22, R.drawable.texture_05, R.drawable.texture_06, R.drawable.texture_07, R.drawable.texture_08, R.drawable.texture_09, R.drawable.texture_10, R.drawable.texture_26, R.drawable.texture_11, R.drawable.texture_12, R.drawable.texture_13, R.drawable.texture_23, R.drawable.texture_14, R.drawable.texture_15, R.drawable.texture_16, R.drawable.texture_18, R.drawable.texture_19, R.drawable.texture_21, R.drawable.texture_02, R.drawable.texture_24};
        textureResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.texture_0_thumb, R.drawable.texture_1_thumb, R.drawable.texture_2_thumb, R.drawable.texture_3_thumb, R.drawable.texture_4_thumb, R.drawable.texture_5_thumb, R.drawable.texture_6_thumb, R.drawable.texture_7_thumb, R.drawable.texture_8_thumb, R.drawable.texture_9_thumb, R.drawable.texture_10_thumb, R.drawable.texture_11_thumb, R.drawable.texture_12_thumb, R.drawable.texture_13_thumb, R.drawable.texture_14_thumb, R.drawable.texture_15_thumb, R.drawable.texture_16_thumb, R.drawable.texture_17_thumb, R.drawable.texture_18_thumb, R.drawable.texture_19_thumb, R.drawable.texture_20_thumb, R.drawable.texture_21_thumb, R.drawable.texture_22_thumb};
        MODE_NONE = -1;
        MODE_SCREEN = 3;
        MODE_MULTIPLY = 1;
        MODE_OVERLAY = 2;
        textureModes = new int[]{MODE_NONE, MODE_OVERLAY, MODE_SCREEN, MODE_OVERLAY, MODE_OVERLAY, MODE_SCREEN, MODE_SCREEN, MODE_OVERLAY, MODE_OVERLAY, MODE_OVERLAY, MODE_OVERLAY, MODE_OVERLAY, MODE_SCREEN, MODE_SCREEN, MODE_SCREEN, MODE_OVERLAY, MODE_SCREEN, MODE_SCREEN, MODE_SCREEN, MODE_OVERLAY, MODE_MULTIPLY, MODE_MULTIPLY, MODE_SCREEN, MODE_OVERLAY};
        filterResThumb = new int[]{R.drawable.effect_0_thumb, R.drawable.effect_1_thumb, R.drawable.effect_2_thumb, R.drawable.effect_3_thumb, R.drawable.effect_4_thumb, R.drawable.effect_5_thumb, R.drawable.effect_6_thumb, R.drawable.effect_7_thumb, R.drawable.effect_8_thumb, R.drawable.effect_9_thumb, R.drawable.effect_10_thumb, R.drawable.effect_11_thumb, R.drawable.effect_12_thumb, R.drawable.effect_13_thumb, R.drawable.effect_14_thumb, R.drawable.effect_15_thumb, R.drawable.effect_16_thumb, R.drawable.effect_17_thumb, R.drawable.effect_18_thumb, R.drawable.effect_19_thumb, R.drawable.effect_20_thumb, R.drawable.effect_21_thumb, R.drawable.effect_22_thumb};
    }

    public static double getLeftSizeOfMemory() {
        double totalSize = (double) Runtime.getRuntime().maxMemory();
        double heapAllocated = (double) Runtime.getRuntime().totalMemory();
        return (totalSize - (heapAllocated - (double) Runtime.getRuntime().freeMemory())) - (double) Debug.getNativeHeapAllocatedSize();
    }
}
