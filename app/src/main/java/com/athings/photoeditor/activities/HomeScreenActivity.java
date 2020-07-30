package com.athings.photoeditor.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.athings.photoeditor.R;
import com.athings.photoeditor.bitmap.BitmapResizer;
import com.athings.photoeditor.utils.CollageHelper;
import com.athings.photoeditor.fragments.SelectImageFragment;
import com.athings.photoeditor.image.ImageLoader;
import com.athings.photoeditor.utils.Utility;
import com.athings.photoeditor.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    String IMAGE_DIRECTORY_NAME = "Photo Editor";

    int REQUEST_MIRROR = 3;

    int PERMISSION_COLLAGE_EDITOR = 11;
    int PERMISSION_SINGLE_EDITOR = 22;
    int PERMISSION_SCRAPBOOK_EDITOR = 33;
    int PERMISSION_CAMERA_EDITOR = 44;
    int PERMISSION_MIRROR_EDITOR = 55;

    LinearLayout linearCollage, linearSingleEditor, linearScrapbook, linearCamera, linearMirror, linearRateus;
    RelativeLayout mainLayout;
    SelectImageFragment galleryFragment;
    ImageLoader imageLoader;
    ImageView imageRateUs, imageShareApp, imageMoreApps;
    AdView mAdView;
    com.facebook.ads.AdView adView;
    LinearLayout linearAdsBanner;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_screen_activity);

        //intitialize MobileAds
        MobileAds.initialize(this, getString(R.string.admob_appid));

        linearAdsBanner = findViewById(R.id.linearAds);

        if (isOnline() && Constants.ADS_STATUS) {
            if (Constants.ADS_TYPE.equals("admob")) {
                try {
                    addBannnerAds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Constants.ADS_TYPE.equals("facebook")) {
                try {
                    addFBBannnerAds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        findViewbyIds();
        imageLoader = new ImageLoader(this);
        imageLoader.setListener(new ImageLoader.ImageLoaded() {
            @Override
            public void callFileSizeAlertDialogBuilder() {
                fileSizeAlertDialogBuilder();
            }
        });
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    void addBannnerAds() {
        if (isOnline()) {
            if (linearAdsBanner.getChildCount() <= 0) {
                mAdView = new AdView(HomeScreenActivity.this);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(Constants.ADS_ADMOB_BANNER_ID);
                AdRequest adRequest = new AdRequest.Builder().build();
               //AdRequest adRequest = new AdRequest.Builder().addTestDevice("F4AAD2F439EAD42C27D50B321FCFD141").build();

                linearAdsBanner.addView(mAdView);
                mAdView.loadAd(adRequest);
                mAdView.setAdListener(  new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    super.onAdFailedToLoad(errorCode);
                }

            });
            }
        }
    }

    void addFBBannnerAds() {
        if (isOnline()) {
            if (linearAdsBanner.getChildCount() <= 0) {
                // Instantiate an AdView view
                adView = new com.facebook.ads.AdView(this, Constants.ADS_FACEBOOK_BANNER_ID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                // Add the ad view to your activity layout
                linearAdsBanner.addView(adView);

                // Request to load an ad
                adView.loadAd();
            }
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

    private void findViewbyIds() {
        mainLayout = findViewById(R.id.mainLayout);
        linearSingleEditor = findViewById(R.id.linearSingleEditor);
        linearCamera = findViewById(R.id.linearCamera);
        linearCollage = findViewById(R.id.linearCollage);
        linearMirror = findViewById(R.id.linearMirror);
        linearScrapbook = findViewById(R.id.linearScrapbook);
        linearRateus = findViewById(R.id.linearRateus);

        imageRateUs = findViewById(R.id.imageRateUs);
        imageShareApp = findViewById(R.id.imageShareApp);
        imageMoreApps = findViewById(R.id.imageMoreApps);

        linearSingleEditor.setOnClickListener(this);
        linearCamera.setOnClickListener(this);
        linearCollage.setOnClickListener(this);
        linearMirror.setOnClickListener(this);
        linearScrapbook.setOnClickListener(this);
        linearRateus.setOnClickListener(this);
        imageRateUs.setOnClickListener(this);
        imageMoreApps.setOnClickListener(this);
        imageShareApp.setOnClickListener(this);
    }

    private void fileSizeAlertDialogBuilder() {
        Point p = BitmapResizer.decodeFileSize(new File(imageLoader.selectedImagePath), Utility.maxSizeForDimension(this, 1, 1500.0f));
        if (p == null || p.x != -1) {
            startShaderActivity();
        } else {
            startShaderActivity();
        }
    }

    private void startShaderActivity() {
        int maxSize = Utility.maxSizeForDimension(this, 1, 1500.0f);
        Intent shaderIntent = new Intent(getApplicationContext(), MirrorImageActivity.class);
        shaderIntent.putExtra("selectedImagePath", this.imageLoader.selectedImagePath);
        shaderIntent.putExtra("isSession", false);
        shaderIntent.putExtra("MAX_SIZE", maxSize);
        Utility.logFreeMemory(this);
        startActivity(shaderIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_COLLAGE_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                openCollage(false, false, false);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_SINGLE_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                openCollage(true, false, false);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_SCRAPBOOK_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                openCollage(false, true, false);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_MIRROR_EDITOR) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_MIRROR);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Intent localIntent = new Intent(this, CreateCollageActivity.class);
                    System.out.println("CAMERA IMAGE PATH" + imagePath);
                    localIntent.putExtra("selected_image_path", imagePath);
                    startActivity(localIntent);
//                    Intent localIntent = new Intent(activity, CreateCollageActivity.class);
//                    localIntent.putExtra("photo_id_list", jArr);
//                    localIntent.putExtra("photo_orientation_list", iArr);
//                    localIntent.putExtra("is_scrap_book", x);
//                    localIntent.putExtra("is_shape", y);
//                    activity.startActivity(localIntent);
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }
            } else if (resultCode == RESULT_OK && requestCode == REQUEST_MIRROR) {
                try {
                    imageLoader.getImageFromIntent(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomeScreenActivity.this, "" + getString(R.string.error_img_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageRateUs) {
            try {
                Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException e) {
                Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }
        } else if (v.getId() == R.id.imageMoreApps) {
            Intent inMoreapp = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=FunRanch+Systems"));
            if (isAvailable(inMoreapp)) {
                startActivity(inMoreapp);
            } else {
                Toast.makeText(HomeScreenActivity.this, "There is no app availalbe for this task", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.imageShareApp) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " - Let me recommend you this application! - https://play.google.com/store/apps/details?id=" + getPackageName());
            sendIntent.setType("text/plain");
            if (isAvailable(sendIntent)) {
                startActivity(sendIntent);
            } else {
                Toast.makeText(HomeScreenActivity.this, "There is no app availalbe for this task", Toast.LENGTH_SHORT).show();
            }
        }

        if (linearCollage == v) {
            if (Build.VERSION.SDK_INT < 23) {
                openCollage(false, false, false);
            } else {
                if (checkAndRequestCollagePermissions()) {
                    //If you have already permitted the permission
                    openCollage(false, false, false);
                }
            }
        }
        if (linearSingleEditor == v) {

            if (Build.VERSION.SDK_INT < 23) {
                openCollage(true, false, false);
            } else {
                if (checkAndRequestSinglePermissions()) {
                    //If you have already permitted the permission
                    openCollage(true, false, false);
                }
            }
        }
        if (linearScrapbook == v) {
            if (Build.VERSION.SDK_INT < 23) {
                openCollage(false, true, false);
            } else {
                if (checkAndRequestScrapbookPermissions()) {
                    //If you have already permitted the permission
                    openCollage(false, true, false);
                }
            }
        }
        if (linearCamera == v) {
            if (Build.VERSION.SDK_INT < 23) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            } else {
                if (checkAndRequestCameraPermissions()) {
                    //If you have already permitted the permission
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                }
            }
        }
        if (linearMirror == v) {
            if (Build.VERSION.SDK_INT < 23) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_MIRROR);
            } else {
                if (checkAndRequestMirrorPermissions()) {
                    //If you have already permitted the permission
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_MIRROR);
                }
            }
        }
        if (linearRateus == v) {
            try {
                Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException e) {
                Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }
        }
    }

    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public void openCollage(boolean isblur, boolean isScrapBook, boolean isShape) {

        galleryFragment = CollageHelper.addGalleryFragment(this, R.id.gallery_fragment_container, null, true, null);
        galleryFragment.setCollageSingleMode(isblur);
        galleryFragment.setIsScrapbook(isScrapBook);
        galleryFragment.setIsShape(isShape);
        if (!isScrapBook) {
            galleryFragment.setLimitMax(SelectImageFragment.MAX_COLLAGE);
        }
    }

    @Override
    public void onBackPressed() {
        SelectImageFragment localGalleryFragment = CollageHelper.getGalleryFragment(this);
        if ((localGalleryFragment != null) && (localGalleryFragment.isVisible())) {
            localGalleryFragment.onBackPressed();
            return;
        }
        finish();
    }

    public Uri setImageUri() {

        File f = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY_NAME);
        if (!f.exists()) {
            f.mkdir();
        }
        String fileName = "IMG_" + new Date().getTime() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY_NAME, fileName);
//        Uri imgUri = Uri.fromFile(file);
        Uri imgUri = FileProvider.getUriForFile(HomeScreenActivity.this, getString(R.string.file_provider_authority), file);
        imagePath = file.getAbsolutePath();
        return imgUri;
    }

    private boolean checkAndRequestCollagePermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_COLLAGE_EDITOR);
            return false;
        }
        return true;
    }

    private boolean checkAndRequestSinglePermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_SINGLE_EDITOR);
            return false;
        }
        return true;
    }

    private boolean checkAndRequestScrapbookPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_SCRAPBOOK_EDITOR);
            return false;
        }
        return true;
    }

    private boolean checkAndRequestCameraPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_CAMERA_EDITOR);
            return false;
        }
        return true;
    }

    private boolean checkAndRequestMirrorPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_MIRROR_EDITOR);
            return false;
        }
        return true;
    }
}
