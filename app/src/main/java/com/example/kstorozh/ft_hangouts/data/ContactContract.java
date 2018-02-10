package com.example.kstorozh.ft_hangouts.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kstorozh on 11/30/17.
 */

public class ContactContract {

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+ ContactEntry.TABLE_NAME + "(" + ContactEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ContactEntry.FIRST_NAME + " TEXT NOT NULL, "
            + ContactEntry.SECOND_NAME + " TEXT NOT NULL, " + ContactEntry.TELEPHONE_NUMBER + " INTEGER NOT NULL, " + ContactEntry.ICON_PATH + " TEXT NOT NULL);";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;

    /**Content provider staff */
    public static final String CONTENT_AUTHORITY = "com.example.kstorozh.ft_hangouts";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CONTACTS = "contacts";


    public static final class ContactEntry implements BaseColumns
    {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACTS);
        public static final String TABLE_NAME = "contacts";

        public static final String _ID = BaseColumns._ID;
        public static final String FIRST_NAME = "first_name";
        public static final String SECOND_NAME = "second_name";
        public static final String TELEPHONE_NUMBER  = "telephone_number";
        public static final String ICON_PATH = "icon_path";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;



    }
}
