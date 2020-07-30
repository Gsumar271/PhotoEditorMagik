package com.athings.photoeditor.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.athings.photoeditor.R;
import com.athings.photoeditor.adapter.MyGridAdapter;
import com.athings.photoeditor.utils.AlbumItem;
import com.athings.photoeditor.utils.Constants;
import com.athings.photoeditor.utils.GalleryImageUtility;
import com.athings.photoeditor.utils.GridViewItem;
import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class SelectImageFragment extends Fragment implements OnItemClickListener {
    int COLLAGE_IMAGE_LIMIT_MAX = 9;
    public static int MAX_SCRAPBOOK = 9;
    int COLLAGE_IMAGE_LIMIT_MIN = 0;
    public static int MAX_COLLAGE = 9;
    private static final String TAG = "GalleryActivity";
    Activity activity;
    AdView adWhirlLayout;
    MyGridAdapter adapter;
    List<AlbumItem> albumList;
    boolean collageSingleMode = false;
    Context context;
    LinearLayout footer;
    GalleryListener galleryListener;
    GridView gridView;
    TextView headerText;
    boolean isOnBucket = true;
    public boolean isScrapBook = false;
    int selectedBucketId;
    List<Long> selectedImageIdList = new ArrayList<>();
    List<Integer> selectedImageOrientationList = new ArrayList<>();
    TextView maxTv, deleteAllTv, removeAllTv, nextTv;
    Animation slideInLeft;
    boolean isShape = false;
    Parcelable gridState;
    ImageView imageBack;

    AdView mAdView;
    com.facebook.ads.AdView adView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_select_image, container, false);

        LinearLayout linearAdsBanner = (LinearLayout) fragmentView.findViewById(R.id.linearAds);

        if (isOnline() && Constants.ADS_STATUS) {
            linearAdsBanner.setVisibility(View.VISIBLE);

            if (Constants.ADS_TYPE.equals("admob")) {
                try {
                    addBannnerAds(fragmentView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Constants.ADS_TYPE.equals("facebook")) {
                try {
                    addFBBannnerAds(fragmentView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            linearAdsBanner.setVisibility(View.GONE);
        }


        footer = fragmentView.findViewById(R.id.selected_image_linear);
        headerText = fragmentView.findViewById(R.id.textView_header);
        imageBack = fragmentView.findViewById(R.id.imageBack);
        imageBack.setOnClickListener(onClickListener);
        maxTv = fragmentView.findViewById(R.id.gallery_max);
        nextTv = fragmentView.findViewById(R.id.gallery_next);
        nextTv.setOnClickListener(onClickListener);
        maxTv.setText(String.format(getString(R.string.gallery_lib_max), getLimitMax()));
        deleteAllTv = fragmentView.findViewById(R.id.gallery_delete_all);
        removeAllTv = fragmentView.findViewById(R.id.gallery_remove_all);
        slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        deleteAllTv.setOnClickListener(onClickListener);
        removeAllTv.setOnClickListener(onClickListener);
        deleteAllTv.setText("(" + footer.getChildCount() + ")");
        return fragmentView;
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    void addBannnerAds(View v) {
        LinearLayout linearAdsBanner = v.findViewById(R.id.linearAds);

        if (isOnline()) {

            linearAdsBanner.setVisibility(View.VISIBLE);
            if (linearAdsBanner.getChildCount() <= 0) {
                mAdView = new AdView(getActivity());
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

    void addFBBannnerAds(View v) {
        LinearLayout linearAdsBanner = v.findViewById(R.id.linearAds);

        if (isOnline()) {

            linearAdsBanner.setVisibility(View.VISIBLE);
            if (linearAdsBanner.getChildCount() <= 0) {
                // Instantiate an AdView view
                adView = new com.facebook.ads.AdView(getActivity(), Constants.ADS_FACEBOOK_BANNER_ID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                // Add the ad view to your activity layout
                AdSettings.addTestDevice("e6976e2040b914dcfc9ad146b3cf9731");
                linearAdsBanner.addView(adView);

                // Request to load an ad
                adView.loadAd();
            }
        } else {
            linearAdsBanner.setVisibility(View.GONE);
        }
    }

    public void setIsShape(boolean isShape) {
        this.isShape = isShape;
    }

    void remoAll() {
        if (footer != null) {
            footer.removeAllViews();
            if (selectedImageIdList != null && selectedImageIdList.size() > 0) {
                for (int i = 0; i < selectedImageIdList.size(); i++) {
                    Point index = findItemById(selectedImageIdList.get(i));
                    if (index != null) {
                        GridViewItem gridViewItem = albumList.get(index.x).gridItems.get(index.y);
                        gridViewItem.selectedItemCount--;
                        int value = albumList.get(index.x).gridItems.get(index.y).selectedItemCount;
                        if (albumList.get(index.x).gridItems == adapter.items && gridView.getFirstVisiblePosition() <= index.y && index.y <= this.gridView.getLastVisiblePosition() && this.gridView.getChildAt(index.y) != null) {
                            TextView text = gridView.getChildAt(index.y).findViewById(R.id.textViewSelectedItemCount);
                            text.setText("" + value);
                            if (value <= 0 && text.getVisibility() == View.VISIBLE) {
                                text.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
            if (selectedImageIdList != null) {
                selectedImageIdList.clear();
            }
            selectedImageOrientationList.clear();
            deleteAllTv.setText("(" + footer.getChildCount() + ")");
            getView().findViewById(R.id.gallery_remove_all).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.gallery_max).setVisibility(View.VISIBLE);
            deleteAllTv.setVisibility(View.VISIBLE);
        }
    }

    public int getLimitMax() {
        return COLLAGE_IMAGE_LIMIT_MAX;
    }


    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.imageBack) {
                backtrace();
            }
            if (id == R.id.imageView_delete) {
                View parent = (View) view.getParent();
                if (parent != null && parent.getParent() != null) {
                    int location = ((ViewGroup) parent.getParent()).indexOfChild(parent);
                    footer.removeView(parent);
                    deleteAllTv.setText("(" + footer.getChildCount() + ")");
                    long imageid = selectedImageIdList.remove(location);
                    selectedImageOrientationList.remove(location);
                    Point index = findItemById(imageid);
                    if (index != null) {
                        GridViewItem gridViewItem = albumList.get(index.x).gridItems.get(index.y);
                        gridViewItem.selectedItemCount--;
                        int value = albumList.get(index.x).gridItems.get(index.y).selectedItemCount;
                        if (albumList.get(index.x).gridItems == adapter.items && gridView.getFirstVisiblePosition() <= index.y && index.y <= gridView.getLastVisiblePosition() && gridView.getChildAt(index.y) != null) {
                            TextView text = (TextView) gridView.getChildAt(index.y).findViewById(R.id.textViewSelectedItemCount);
                            text.setText("" + value);
                            if (value <= 0 && text.getVisibility() == View.VISIBLE) {
                                text.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                } else {
                    return;
                }
            }
            if (id == R.id.gallery_delete_all) {
                if (footer != null && footer.getChildCount() != 0) {
                    removeAllTv.setVisibility(View.VISIBLE);
                    maxTv.setVisibility(View.INVISIBLE);
                    deleteAllTv.setVisibility(View.INVISIBLE);
                    removeAllTv.startAnimation(slideInLeft);

                }
            }
            if (id == R.id.gallery_remove_all) {
                remoAll();
            }
            if (id == R.id.gallery_next) {
                photosSelectFinished();
            }
        }
    };


    public void setGalleryListener(GalleryListener l) {
        galleryListener = l;
    }

    public void setIsScrapbook(boolean isScrapbook) {
        isScrapBook = isScrapbook;
        setLimitMax(MAX_SCRAPBOOK);
        if (selectedImageIdList != null && selectedImageIdList.size() > COLLAGE_IMAGE_LIMIT_MAX) {
            remoAll();
        } else if (this.footer != null && footer.getChildCount() > COLLAGE_IMAGE_LIMIT_MAX) {
            remoAll();
        }
    }

    public void setLimitMax(int max) {
        COLLAGE_IMAGE_LIMIT_MAX = max;
        if (maxTv != null) {
            maxTv.setText(String.format(getString(R.string.gallery_lib_max), new Object[]{Integer.valueOf(COLLAGE_IMAGE_LIMIT_MAX)}));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (gridView != null) {
            try {
                gridState = gridView.onSaveInstanceState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logGalleryFolders();
        updateListForSelection();
        setGridAdapter();
        if (!isOnBucket && albumList != null && selectedBucketId >= 0 && selectedBucketId < albumList.size()) {
            adapter.items = albumList.get(selectedBucketId).gridItems;
            if (gridView != null) {
                gridView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (gridState != null) {
                            Log.d(SelectImageFragment.TAG, "trying to restore listview state..");
                            gridView.onRestoreInstanceState(gridState);
                        }
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
    }

    void updateListForSelection() {
        if ((selectedImageIdList != null) && (!selectedImageIdList.isEmpty()))
            for (int i = 0; i < selectedImageIdList.size(); i++) {
                Point localPoint = findItemById(selectedImageIdList.get(i));
                if (localPoint != null) {
                    GridViewItem localGridViewItem = albumList.get(localPoint.x).gridItems.get(localPoint.y);
                    localGridViewItem.selectedItemCount = (1 + localGridViewItem.selectedItemCount);
                }
            }
    }

    public void setCollageSingleMode(boolean mode) {
        collageSingleMode = mode;
        if (mode) {
            if (selectedImageIdList != null) {
                for (int i = selectedImageIdList.size() - 1; i >= 0; i--) {
                    Point index = findItemById(selectedImageIdList.remove(i));
                    if (index != null) {
                        GridViewItem gridViewItem = albumList.get(index.x).gridItems.get(index.y);
                        gridViewItem.selectedItemCount--;
                        int value = albumList.get(index.x).gridItems.get(index.y).selectedItemCount;
                        if (albumList.get(index.x).gridItems == adapter.items && gridView.getFirstVisiblePosition() <= index.y && index.y <= gridView.getLastVisiblePosition() && gridView.getChildAt(index.y) != null) {
                            TextView text = gridView.getChildAt(index.y).findViewById(R.id.textViewSelectedItemCount);
                            text.setText(value);
                            if (value <= 0 && text.getVisibility() == View.VISIBLE) {
                                text.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
            if (selectedImageOrientationList != null) {
                selectedImageOrientationList.clear();
            }
            if (footer != null) {
                footer.removeAllViews();
            }
            if (deleteAllTv != null) {
                deleteAllTv.setText("(0)");
            }
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
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
    }

    @Override
    public void onDestroyView() {
        if (adWhirlLayout != null) {
            adWhirlLayout.removeAllViews();
            adWhirlLayout.destroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logGalleryFolders();
        setGridAdapter();
    }

    private void setGridAdapter() {
        gridView = getView().findViewById(R.id.gridView);
        adapter = new MyGridAdapter(context, (albumList.get(albumList.size() - 1)).gridItems, gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    private List<GridViewItem> createGridItemsOnClick(int position) {
        List<GridViewItem> items = new ArrayList<>();
        AlbumItem album = albumList.get(position);
        List<Long> imageIdList = album.imageIdList;
        List<Integer> orientList = album.orientationList;
        for (int i = 0; i < imageIdList.size(); i++) {
            items.add(new GridViewItem(activity, "", "", false, imageIdList.get(i), orientList.get(i)));
        }
        return items;
    }

    private boolean logGalleryFolders() {
        albumList = new ArrayList<>();
        List<Integer> bucketIdList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "bucket_display_name", "bucket_id", "_id", "orientation"}, "1) GROUP BY 1,(2", null, "date_modified DESC");
        List<GridViewItem> items;
        int i;
        if (cur == null || !cur.moveToFirst()) {
            items = new ArrayList<>();
            for (i = 0; i < albumList.size(); i++) {
                items.add(new GridViewItem(activity, albumList.get(i).name, "" + albumList.get(i).imageIdList.size(), true, albumList.get(i).imageIdForThumb, albumList.get(i).orientationList.get(0)));
            }
            albumList.add(new AlbumItem());
            albumList.get(albumList.size() - 1).gridItems = items;
            for (i = 0; i < this.albumList.size() - 1; i++) {
                albumList.get(i).gridItems = createGridItemsOnClick(i);
            }
            return true;
        }
        int bucketColumn = cur.getColumnIndex("bucket_display_name");
        int bucketId = cur.getColumnIndex("bucket_id");
        int imageId = cur.getColumnIndex("_id");
        int orientationColumnIndex = cur.getColumnIndex("orientation");
        do {
            AlbumItem album = new AlbumItem();
            int id = cur.getInt(bucketId);
            album.ID = id;
            if (bucketIdList.contains(id)) {
                AlbumItem albumFromList = this.albumList.get(bucketIdList.indexOf(album.ID));
                albumFromList.imageIdList.add(cur.getLong(imageId));
                albumFromList.orientationList.add(cur.getInt(orientationColumnIndex));
            } else {
                String bucket = cur.getString(bucketColumn);
                bucketIdList.add(id);
                album.name = bucket;
                album.imageIdForThumb = cur.getLong(imageId);
                album.imageIdList.add(album.imageIdForThumb);
                this.albumList.add(album);
                album.orientationList.add(cur.getInt(orientationColumnIndex));
            }
        } while (cur.moveToNext());
        items = new ArrayList<>();
        for (i = 0; i < this.albumList.size(); i++) {
            items.add(new GridViewItem(this.activity, this.albumList.get(i).name, "" + this.albumList.get(i).imageIdList.size(), true, this.albumList.get(i).imageIdForThumb, this.albumList.get(i).orientationList.get(0)));
        }
        albumList.add(new AlbumItem());
        albumList.get(albumList.size() - 1).gridItems = items;
        for (i = 0; i < this.albumList.size() - 1; i++) {
            albumList.get(i).gridItems = createGridItemsOnClick(i);
        }
        return true;
    }

    public boolean onBackPressed() {
        return backtrace();
    }

    boolean backtrace() {

        if (isOnBucket) {
            if (galleryListener != null) {
                galleryListener.onGalleryCancel();
            }
            return true;
        }
        gridView.setNumColumns(2);
        adapter.items = albumList.get(albumList.size() - 1).gridItems;
        adapter.notifyDataSetChanged();
        gridView.smoothScrollToPosition(0);
        isOnBucket = true;
        headerText.setText(getString(R.string.gallery_select_an_album));
        return false;
    }

    public void onItemClick(AdapterView<?> adapterView, View arg1, int location, long arg3) {

        if (this.isOnBucket) {
            this.gridView.setNumColumns(3);
            this.adapter.items = this.albumList.get(location).gridItems;
            this.adapter.notifyDataSetChanged();
            this.gridView.smoothScrollToPosition(0);
            this.isOnBucket = false;
            this.selectedBucketId = location;
            this.headerText.setText(this.albumList.get(location).name);
        } else if (this.footer.getChildCount() >= this.COLLAGE_IMAGE_LIMIT_MAX) {
            Toast msg = Toast.makeText(this.context, String.format(getString(R.string.gallery_no_more), this.COLLAGE_IMAGE_LIMIT_MAX), Toast.LENGTH_LONG);
            msg.setGravity(17, msg.getXOffset() / 2, msg.getYOffset() / 2);
            msg.show();
        } else {
            View retval = LayoutInflater.from(context).inflate(R.layout.footer_item, null);
            retval.findViewById(R.id.imageView_delete).setOnClickListener(onClickListener);
            ImageView im = (ImageView) retval.findViewById(R.id.imageView);
            if (selectedBucketId >= 0 && selectedBucketId < albumList.size() && location >= 0 && location < albumList.get(selectedBucketId).imageIdList.size()) {
                long id = this.albumList.get(selectedBucketId).imageIdList.get(location);
                selectedImageIdList.add(id);
                selectedImageOrientationList.add(albumList.get(this.selectedBucketId).orientationList.get(location));
                Bitmap bm = GalleryImageUtility.getThumbnailBitmap(context, id, albumList.get(selectedBucketId).orientationList.get(location));
                if (bm != null) {
                    im.setImageBitmap(bm);
                }
                footer.addView(retval);
                deleteAllTv.setText("(" + footer.getChildCount() + ")");
                GridViewItem gridViewItem = adapter.items.get(location);
                gridViewItem.selectedItemCount++;
                TextView text = (TextView) arg1.findViewById(R.id.textViewSelectedItemCount);
                text.setText("" + adapter.items.get(location).selectedItemCount);
                if (text.getVisibility() == View.INVISIBLE) {
                    text.setVisibility(View.VISIBLE);
                }
                if (collageSingleMode) {
                    photosSelectFinished();
                    collageSingleMode = false;
                }
            }
        }

    }

    Point findItemById(long id) {
        for (int i = 0; i < albumList.size() - 1; i++) {
            List<GridViewItem> list = albumList.get(i).gridItems;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).imageIdForThumb == id) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    public int getLimitMin() {
        return COLLAGE_IMAGE_LIMIT_MIN;
    }

    void photosSelectFinished() {
        int size = selectedImageIdList.size();
        if (size <= COLLAGE_IMAGE_LIMIT_MIN) {
            Toast msg = Toast.makeText(context, String.format(getString(R.string.gallery_select_one), getLimitMin() + 1), Toast.LENGTH_LONG);
            msg.setGravity(17, msg.getXOffset() / 2, msg.getYOffset() / 2);
            msg.show();
            return;
        }
        int i;
        long[] arrr = new long[size];
        for (i = 0; i < size; i++) {
            arrr[i] = this.selectedImageIdList.get(i);
        }
        int[] orientationArr = new int[size];
        for (i = 0; i < size; i++) {
            orientationArr[i] = selectedImageOrientationList.get(i);
        }
        if (galleryListener != null) {
            galleryListener.onGalleryOkImageArray(arrr, orientationArr, isScrapBook, isShape);
            return;
        }
        try {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface GalleryListener {
        void onGalleryCancel();

        void onGalleryOkImageArray(long[] jArr, int[] iArr, boolean x, boolean y);

        void onGalleryOkImageArrayRemoveFragment(long[] jArt, int[] iArr, boolean x, boolean y);

        void onGalleryOkSingleImage(long j, int i, boolean x, boolean y);
    }
}
