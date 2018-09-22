package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by kateryna on 23.09.18.
 */

public class Helper {
    public static Bitmap bitmapFromPath(Context context, String mCurrentPhotoPath)
    {
        if (mCurrentPhotoPath.equals(""))
        {

            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
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

    private static Bitmap makebitmapRotation(String mCurrentPhotoPath, Bitmap takenImage) {
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
