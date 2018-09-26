package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kateryna on 23.09.18.
 */

public class Helper {

    static int defaultView = R.drawable.ic_launcher_foreground;
    Context context;

    public Helper(Context context) {
        this.context = context;
    }

    public  Bitmap bitmapFromPath(String mCurrentPhotoPath)
    {
        if (mCurrentPhotoPath.equals(""))
        {

            return BitmapFactory.decodeResource(context.getResources(), defaultView);
        }

        Bitmap takenImage = BitmapFactory.decodeFile(mCurrentPhotoPath);
        Bitmap rotatedBitmap = null;
        try {
            rotatedBitmap = makebitmapRotation(mCurrentPhotoPath, takenImage);
        }
        catch (Exception ex)
        {
            rotatedBitmap = takenImage;
        }
        return rotatedBitmap;
    }

    private  Bitmap makebitmapRotation(String mCurrentPhotoPath, Bitmap takenImage) {
        Bitmap rotatedBitmap = null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);


        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(takenImage, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(takenImage, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(takenImage, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = takenImage;
        }
        return rotatedBitmap;

    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }








}
