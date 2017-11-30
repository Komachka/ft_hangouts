package com.example.kstorozh.ft_hangouts.data;

import android.provider.BaseColumns;

/**
 * Created by kstorozh on 11/30/17.
 */

public class ContactContract {

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+ ContactEntry.TABLE_NAME + "(" + ContactEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ContactEntry.FIRST_NAME + " TEXT NOT NULL, "
            + ContactEntry.SECOND_NAME + " TEXT NOT NULL, " + ContactEntry.TELEPHONE_NUMBER + " INTEGER NOT NULL, " + ContactEntry.ICON_PATH + " TEXT NOT NULL);";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;

    public ContactContract() {
    }


    public static final class ContactEntry implements BaseColumns
    {

        public static final String TABLE_NAME = "contacts";

        public static final String _ID = BaseColumns._ID;
        public static final String FIRST_NAME = "first_name";
        public static final String SECOND_NAME = "second_name";
        public static final String TELEPHONE_NUMBER  = "telephone_number";
        public static final String ICON_PATH = "icon_path";


    }
}
