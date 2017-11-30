package com.example.kstorozh.ft_hangouts.data;



import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.kstorozh.ft_hangouts.data.ContactContract.SQL_CREATE_ENTRIES;
import static com.example.kstorozh.ft_hangouts.data.ContactContract.SQL_DELETE_ENTRIES;

/**
 * Created by kstorozh on 11/30/17.
 */

public class ContactDBHealper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "contacts_info.db";

    public ContactDBHealper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
     db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) {
     db.execSQL(SQL_DELETE_ENTRIES);
     onCreate(db);
    }
}
