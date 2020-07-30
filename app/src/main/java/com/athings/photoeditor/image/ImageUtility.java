package com.athings.photoeditor.image;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v8.renderscript.Allocation;
import android.util.Log;

import com.athings.photoeditor.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class ImageUtility {
    public static int SPLASH_TIME_OUT_LONG = 0;
    static int SPLASH_TIME_OUT_MAX = 0;
    public static int SPLASH_TIME_OUT_SHORT = 0;
    private static final String TAG = "SaveImage Utils";

    public static boolean getAmazonMarket(Context context) {
        int AMAZON_MARKET = 0;
        try {
            AMAZON_MARKET = context.getPackageManager().getApplicationInfo(context.getPackageName(), Allocation.USAGE_SHARED).metaData.getInt("amazon_market");
            if (AMAZON_MARKET < 0) {
                AMAZON_MARKET = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (AMAZON_MARKET == 1) {
            return true;
        }
        return false;
    }

    public static String getPrefferredDirectoryPathEx(Context mContext) {
        String directory = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(mContext.getResources().getString(R.string.directory)).toString();
        String prefDir = PreferenceManager.getDefaultSharedPreferences(mContext).getString("save_image_directory_custom", null);
        if (prefDir != null) {
            return new StringBuilder(String.valueOf(prefDir)).append(File.separator).toString();
        }
        return directory;
    }

    public static String getPrefferredDirectoryPath(Context mContext, boolean showErrorMessage, boolean getPrefDirectoryNoMatterWhat, boolean isAppCamera) {
        String directory;
        if (isAppCamera) {
            directory = new StringBuilder(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())).append(File.separator).append(mContext.getResources().getString(R.string.directory)).toString();
        } else {
            directory = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(mContext.getResources().getString(R.string.directory)).toString();
        }
        String prefDir = PreferenceManager.getDefaultSharedPreferences(mContext).getString("save_image_directory_custom", null);
        if (prefDir != null) {
            prefDir = new StringBuilder(String.valueOf(prefDir)).append(File.separator).toString();
            if (getPrefDirectoryNoMatterWhat) {
                return prefDir;
            }
            File dirFile = new File(prefDir);
            String finalDirectory = directory;
            if (dirFile.canRead() && dirFile.canWrite() && checkIfEACCES(prefDir)) {
                directory = prefDir;
            } else if (showErrorMessage) {
                //  ((Activity) mContext).runOnUiThread(new C05871(mContext, finalDirectory));
            }
            Log.e(TAG, "prefDir " + prefDir);
        }
        Log.e(TAG, "getPrefferredDirectoryPathEx " + getPrefferredDirectoryPathEx(mContext));
        Log.e(TAG, "getPrefferredDirectoryPath " + directory);
        return directory;
    }

    public static boolean checkIfEACCES(String dir) {
        IOException ex;
        Throwable th;
        boolean result = false;
        Writer writer = null;
        try {
            File f = new File(dir);
            String localPath = new StringBuilder(String.valueOf(dir)).append("mpp").toString();
            f.mkdirs();
            Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localPath), "utf-8"));
            try {
                writer2.write("Something");
                result = true;
                writer2.close();
                Log.e(TAG, "f.delete() = " + new File(localPath).delete());
                try {
                    writer2.close();
                    writer = writer2;
                } catch (Exception e) {
                    writer = writer2;
                }
            } catch (IOException e2) {
                ex = e2;
                writer = writer2;
                try {
                    Log.e(TAG, ex.toString());
                    try {
                        writer.close();
                    } catch (Exception e3) {
                    }
                    return result;
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        writer.close();
                    } catch (Exception e4) {
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                writer = writer2;
                writer.close();
            }
        } catch (IOException e5) {
            ex = e5;
            Log.e(TAG, ex.toString());
            return result;
        }
        return result;
    }

    static {
        SPLASH_TIME_OUT_SHORT = 150;
        SPLASH_TIME_OUT_LONG = 800;
        SPLASH_TIME_OUT_MAX = 1300;
    }
}
