package com.athings.photoeditor.utils;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.athings.photoeditor.activities.CreateCollageActivity;
import com.athings.photoeditor.fragments.SelectImageFragment;
import com.google.android.gms.ads.InterstitialAd;

public class CollageHelper {
    protected static final String TAG = "CollageHelper";

    public static SelectImageFragment getGalleryFragment(FragmentActivity activity) {
        return (SelectImageFragment) activity.getSupportFragmentManager().findFragmentByTag("myFragmentTag");
    }

    public static SelectImageFragment addGalleryFragment(FragmentActivity activity, int gallery_fragment_container, InterstitialAd interstitial, boolean showInter, View view) {
        FragmentManager fm = activity.getSupportFragmentManager();
        SelectImageFragment galleryFragment = (SelectImageFragment) fm.findFragmentByTag("myFragmentTag");
        if (galleryFragment == null) {
            galleryFragment = new SelectImageFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(gallery_fragment_container, galleryFragment, "myFragmentTag");
            ft.commitAllowingStateLoss();
            galleryFragment.setGalleryListener(createGalleryListener(activity, galleryFragment, interstitial, showInter, view));
            activity.findViewById(gallery_fragment_container).bringToFront();
            return galleryFragment;
        }
        activity.getSupportFragmentManager().beginTransaction().show(galleryFragment).commitAllowingStateLoss();
        return galleryFragment;
    }

    public static SelectImageFragment.GalleryListener createGalleryListener(final FragmentActivity activity, final SelectImageFragment galleryFragment, final InterstitialAd interstitial, final boolean showInter, final View view) {
        return new SelectImageFragment.GalleryListener() {
            @Override
            public void onGalleryCancel() {
                if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
                    view.setVisibility(View.VISIBLE);
                }
                activity.getSupportFragmentManager().beginTransaction().hide(galleryFragment).commitAllowingStateLoss();
            }

            @Override
            public void onGalleryOkImageArray(long[] jArr, int[] iArr, boolean x, boolean y) {
                if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
                    view.setVisibility(View.VISIBLE);
                }
                Intent localIntent = new Intent(activity, CreateCollageActivity.class);
                localIntent.putExtra("photo_id_list", jArr);
                localIntent.putExtra("photo_orientation_list", iArr);
                localIntent.putExtra("is_scrap_book", x);
                localIntent.putExtra("is_shape", y);
                activity.startActivity(localIntent);
            }

            @Override
            public void onGalleryOkImageArrayRemoveFragment(long[] jArt, int[] iArr, boolean x, boolean y) {

            }

            @Override
            public void onGalleryOkSingleImage(long j, int i, boolean x, boolean y) {

            }
        };
    }
}
