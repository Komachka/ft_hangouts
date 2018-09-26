package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import static android.media.ThumbnailUtils.extractThumbnail;

public class ImageHelper {
    Context context;
    private LruCache<String, Bitmap> mMemoryCache;


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
        Bitmap thumbnail = getThumbnail(mCurrentPhotoPath); // try to fetch thumbnail
        if (thumbnail != null)
        {
            imageView.setImageBitmap(thumbnail);
            Log.d(EditActivity.class.getSimpleName(), "thumbnail has cashe");
            return;
        }

        int targetImageViewWidth = imageView.getWidth();
        int targetImageViewHeight = imageView.getHeight();

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
        options.inSampleSize = scaleFactor;
        options.inJustDecodeBounds = false;

        Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        thumbnail = extractThumbnail(photoReduceSizeBitmap, targetImageViewWidth, targetImageViewHeight);
        photoReduceSizeBitmap.recycle();
        setThumbnail(mCurrentPhotoPath, thumbnail);
        imageView.setImageBitmap(thumbnail);
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




}
