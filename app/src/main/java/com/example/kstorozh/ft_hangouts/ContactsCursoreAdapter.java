package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kstorozh.ft_hangouts.data.ContactContract;

import java.io.File;
import java.io.IOException;

import static android.media.ThumbnailUtils.extractThumbnail;

/**
 * Created by kateryna on 22.09.18.
 */

public class ContactsCursoreAdapter extends CursorAdapter {
    private LruCache<String, Bitmap> mMemoryCache;



    public ContactsCursoreAdapter(Context context, Cursor c) {

        super(context, c, 0);
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

    private class ViewHolder{
        ImageView icon;
        TextView firstName;
        TextView secondName;
        TextView telephone;

        ViewHolder(View view)
        {
             icon = (ImageView) view.findViewById(R.id.ivImg);
             firstName = (TextView) view.findViewById(R.id.tvfirstname);
             secondName = (TextView) view.findViewById(R.id.tvsecondname);
             telephone = (TextView) view.findViewById(R.id.tvtelephone);
        }

    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        view.setTag(view);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
        
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String pathToIcon = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
        String fName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME));
        String sName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME));
        String tel = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));


        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.firstName.setText(fName);
        viewHolder.secondName.setText(sName);
        viewHolder.telephone.setText(tel);
        if (TextUtils.isEmpty(pathToIcon))
            viewHolder.icon.setImageResource(R.drawable.ic_launcher_foreground);
        else
        {
            //Log.d(EditActivity.class.getSimpleName(), "PAth not empty so we need to add image " + pathToIcon);
            setBitmap(viewHolder.icon, pathToIcon);

        }


    }



    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }


    public void setBitmap(ImageView icon, String pathToIcon) {
        Bitmap thumbnail = getThumbnail(pathToIcon); // try to fetch thumbnail
        if (thumbnail != null)
        {
            icon.setImageBitmap(thumbnail);
            //Log.d(EditActivity.class.getSimpleName(), "thumbnail has cashe");
            return;
        }
        File file = new File(pathToIcon);
        if (!file.exists())
            return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToIcon, options);
        int scaleFactor = 2;
        options.inSampleSize = scaleFactor;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathToIcon, options);
        if (bitmap == null)
        {
            Log.d(EditActivity.class.getSimpleName(), "Can not create bitmap file");
            return;
        }
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(pathToIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = ImageHelper.getRotation(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        thumbnail = extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getHeight());
        setThumbnail(pathToIcon, thumbnail);
        icon.setImageBitmap(bitmap);
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
