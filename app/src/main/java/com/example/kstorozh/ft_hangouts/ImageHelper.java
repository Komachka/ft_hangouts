package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

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


    /*
    Context context;



    public ImageHelper(Context context) {
        this.context = context;
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        final int cachSize = maxMemory/8;

        mMemoryCache = new LruCache<String, Bitmap>(cachSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
    }

    void setReducedImageSize(ImageView imageView, String mCurrentPhotoPath)
    {
        File f = new File(mCurrentPhotoPath);
        if (!f.exists() || f.isDirectory())
        {
            Log.d(EditActivity.class.getSimpleName(), "File is not exists " + mCurrentPhotoPath);
        }
        Bitmap thumbnail = getThumbnail(mCurrentPhotoPath); // try to fetch thumbnail
        if (thumbnail != null)
        {
            imageView.setImageBitmap(thumbnail);
            Log.d(EditActivity.class.getSimpleName(), "thumbnail has cashe");
            return;
        }


        Log.d(EditActivity.class.getSimpleName(), "thumbnail has no cashe yet " + mCurrentPhotoPath);
        int targetImageViewWidth = imageView.getWidth();
        int targetImageViewHeight = imageView.getHeight();

        *//*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        int cameraImageWidth = options.outWidth;
        int cameraImageHeight = options.outHeight;

        if (targetImageViewHeight == 0)
            targetImageViewHeight = 100;
        if (targetImageViewWidth == 0)
            targetImageViewWidth = 100;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        //options.inSampleSize = scaleFactor;
        options.inSampleSize = 2;

        options.inJustDecodeBounds = false;
*//*


        //Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        //Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        //options = new BitmapFactory.Options();
        //options.inSampleSize = 2;



        Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        //int hn = (int)(photoReduceSizeBitmap.getHeight() * (512.0/photoReduceSizeBitmap.getWidth()));
        //photoReduceSizeBitmap = Bitmap.createScaledBitmap(photoReduceSizeBitmap, 512, hn, true);

        //Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);


        if (photoReduceSizeBitmap == null) {
            Toast.makeText(context, "Bitmap is null " + mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            Log.d(EditActivity.class.getSimpleName(), "Bitmap is null " + mCurrentPhotoPath);
            return;
            //photoReduceSizeBitmap = rotateImage(photoReduceSizeBitmap, mCurrentPhotoPath);
        }
        //thumbnail = extractThumbnail(photoReduceSizeBitmap, targetImageViewWidth, targetImageViewHeight);
        //photoReduceSizeBitmap.recycle();
        //setThumbnail(mCurrentPhotoPath, thumbnail);
        imageView.setImageBitmap(thumbnail);
    }

    private Bitmap rotateImage(Bitmap bitmap, String path)  {
        if (bitmap != null) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                default:
            }
            Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return rotateBitmap;
        }
        return null;
    }


    private void setThumbnail(String key, Bitmap b) {
        if (getThumbnail(key)== null)
        {
            mMemoryCache.put(key, b);
        }
    }

    private Bitmap getThumbnail(String key) {
        return mMemoryCache.get(key);
    }




    public void setBitmap(ImageView icon, String pathToIcon, Context context) {
        Bitmap thumbnail = getThumbnail(pathToIcon); // try to fetch thumbnail
        if (thumbnail != null)
        {
            icon.setImageBitmap(thumbnail);
            Log.d(EditActivity.class.getSimpleName(), "thumbnail has cashe");
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToIcon, options);
        int cameraImageWidth = options.outWidth;
        int cameraImageHeight = options.outHeight;
        int scaleFactor = 2;
        options.inSampleSize = scaleFactor;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathToIcon, options);
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(pathToIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
            default:
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        thumbnail = extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getHeight());
        setThumbnail(pathToIcon, thumbnail);
        icon.setImageBitmap(bitmap);
    }



*/

}
