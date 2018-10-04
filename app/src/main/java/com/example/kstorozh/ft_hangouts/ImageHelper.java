package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;


import java.io.File;
import java.io.IOException;

import static android.media.ThumbnailUtils.extractThumbnail;

public class ImageHelper {

    public static Matrix getRotation(int orientation)
    {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
        }
        return matrix;
    }


}
