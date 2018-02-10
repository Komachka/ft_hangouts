package com.example.kstorozh.ft_hangouts.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kstorozh on 11/30/17.
 */

public class MyContactProvider  extends ContentProvider {

    /** Database helper object */
    private ContactDBHealper contactDBHealper;
    public static final String LOG_TAG = MyContactProvider.class.getSimpleName();

    private static final int ALL_CONTACTS = 100;
    private static final int CONTACT_ID = 101;

    private static final UriMatcher sUriMAtcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMAtcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS, ALL_CONTACTS);
        sUriMAtcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS + "/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate() {

        Log.i(LOG_TAG, "logs");
        contactDBHealper = new ContactDBHealper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = contactDBHealper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMAtcher.match(uri);
        switch (match) {
            case ALL_CONTACTS:
                //
                cursor = db.query(ContactContract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CONTACT_ID:
                //For one contact
                selection = ContactContract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ContactContract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);
                break;
            default:
                throw  new IllegalArgumentException("Cannot query unknown URI " + uri);
                
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMAtcher.match(uri);
        switch (match)
        {
            case ALL_CONTACTS:
                return insertNewContact(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }


    }

    private Uri insertNewContact(Uri uri, ContentValues contentValues)  {

        boolean validData = true;
        String name = contentValues.getAsString(ContactContract.ContactEntry.FIRST_NAME);
        if (name == null) {
            validData = false;
            Log.e(LOG_TAG, "First name is incorect");
            //throw new IllegalArgumentException("Contact requires a First name");
        }
        name = contentValues.getAsString(ContactContract.ContactEntry.SECOND_NAME);
        if (name == null) {
            validData = false;
            Log.e(LOG_TAG, "Second name is incorect");
            //throw new RuntimeException("Contact requires a Second name");
        }
        String number = contentValues.getAsString(ContactContract.ContactEntry.TELEPHONE_NUMBER);
        Log.e(LOG_TAG, "Telefon valid = " + String.valueOf(isTelephonValid(number)));
        if (number == null)
            validData = false;
        if (!isTelephonValid(number))
            validData = false;
        Log.e(LOG_TAG, "Data valid = " + String.valueOf(validData));
        if (validData) {
            SQLiteDatabase db = contactDBHealper.getWritableDatabase();
            long id = db.insert(ContactContract.ContactEntry.TABLE_NAME, null, contentValues);
            if (id == -1) {

                Log.e(LOG_TAG, "Falled to insert row for " + uri);
                return null;
            }
            Log.v(LOG_TAG, "New row id = " + id);
            return ContentUris.withAppendedId(uri, id);
        }
        return null;
    }

    public static boolean isTelephonValid(String number)
    {
        String regexex  = "380[0-9]{9}";
        Pattern pattern = Pattern.compile(regexex);
        Matcher matcher = pattern.matcher(number);
        if (matcher.matches())
        {
            Log.i(LOG_TAG, "number " + number + " is valid");
            return true;
        }
        Log.e(LOG_TAG, "number " + number + " is invalid");
        return false;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMAtcher.match(uri);
        switch (match) {
            case ALL_CONTACTS:
                return updateContact(uri, contentValues, selection, selectionArgs);
                break;
            case CONTACT_ID:
                selection = ContactContract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return update
        }
        return 0;
    }
}
