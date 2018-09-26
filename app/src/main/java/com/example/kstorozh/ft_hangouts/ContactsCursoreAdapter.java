package com.example.kstorozh.ft_hangouts;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kstorozh.ft_hangouts.data.ContactContract;

/**
 * Created by kateryna on 22.09.18.
 */

public class ContactsCursoreAdapter extends CursorAdapter {
    ImageHelper imageHelper;



    public ContactsCursoreAdapter(Context context, Cursor c) {

        super(context, c, 0);
        imageHelper = new ImageHelper(context);
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

        /*ImageView icon = (ImageView) view.findViewById(R.id.ivImg);
        TextView firstName = (TextView) view.findViewById(R.id.tvfirstname);
        TextView secondName = (TextView) view.findViewById(R.id.tvsecondname);
        TextView telephone = (TextView) view.findViewById(R.id.tvtelephone);*/

        String pathToIcon = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
        String fName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME));
        String sName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME));
        String tel = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));


        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.firstName.setText(fName);
        viewHolder.secondName.setText(sName);
        viewHolder.telephone.setText(tel);
        if (TextUtils.isEmpty(pathToIcon))
            viewHolder.icon.setImageResource(R.drawable.ic_launcher_background);
        else
        {
            imageHelper.setReducedImageSize(viewHolder.icon, pathToIcon);
            }


    }


    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }
}
