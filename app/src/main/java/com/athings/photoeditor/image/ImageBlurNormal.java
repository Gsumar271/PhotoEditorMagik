package com.athings.photoeditor.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class ImageBlurNormal {

    int lastBlurRadius;
    Matrix matrixBlur;
    Paint paintBlur;

    public ImageBlurNormal() {
        lastBlurRadius = -1;
        paintBlur = new Paint(2);
        matrixBlur = new Matrix();
    }

    public static Bitmap createCroppedBitmap(Bitmap bitmap, int i, int j, int k, int l, boolean flag) {
        Bitmap bitmap1 = Bitmap.createBitmap(k, l, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        Paint paint = new Paint();
        paint.setFilterBitmap(flag);
        canvas.drawBitmap(bitmap, -i, -j, paint);
        return bitmap1;
    }
}
