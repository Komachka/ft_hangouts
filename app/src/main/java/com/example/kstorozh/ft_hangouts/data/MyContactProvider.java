package com.example.kstorozh.ft_hangouts.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

        Log.i(LOG_TAG, "Content provider is created");
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
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match  = sUriMAtcher.match(uri);
        switch (match)
        {
            case ALL_CONTACTS:
                return ContactContract.ContactEntry.CONTENT_LIST_TYPE;
            case CONTACT_ID:
                return ContactContract.ContactEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
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
        Log.d(LOG_TAG, "telephone_number " + number);
        Log.d(LOG_TAG, "Telefon valid = " + String.valueOf(isTelephonValid(number)));
        if (number == null)
            validData = false;
        if (!isTelephonValid(number))
            validData = false;
        Log.e(LOG_TAG, "Data valid = " + String.valueOf(validData));

        String photo = contentValues.getAsString(ContactContract.ContactEntry.ICON_PATH);
        Log.d(LOG_TAG, "photopath = " + photo);
        if (photo == null)
            contentValues.put(ContactContract.ContactEntry.ICON_PATH, "");
        if (validData) {
            SQLiteDatabase db = contactDBHealper.getWritableDatabase();
            long id = 0;
            try {
                id = db.insert(ContactContract.ContactEntry.TABLE_NAME, null, contentValues);
            }
            catch (SQLiteConstraintException ex)
            {
                Log.e(LOG_TAG, "Inserting to a table fauled" + ex.getMessage());
                id = -1;
            }
            if (id == -1) {

                Log.e(LOG_TAG, "Falled to insert row for " + uri);
                return null;
            }
            Log.v(LOG_TAG, "New row id = " + id);

            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }
        return null;
    }

    public static boolean isTelephonValid(String number)
    {
        String regexex  = "[+0-9]+";
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
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = contactDBHealper.getWritableDatabase();
        int maych = sUriMAtcher.match(uri);
        int rowDelited = 0;
        switch (maych)
        {
            case ALL_CONTACTS:
                rowDelited = db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                selection = ContactContract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDelited = db.delete(ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDelited != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDelited;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMAtcher.match(uri);
        int rowUpdated = 0;
        switch (match) {
            case ALL_CONTACTS:
                rowUpdated =  updateContact(uri, contentValues, selection, selectionArgs);
                break;
            case CONTACT_ID:
                selection = ContactContract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated =  updateContact(uri, contentValues, selection, selectionArgs);
                break;
            default:
               throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    private int updateContact(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        boolean validData = true;
        if (contentValues.size() == 0) {
            return 0;
        }
        if (contentValues.containsKey(ContactContract.ContactEntry.FIRST_NAME)) {
            String name = contentValues.getAsString(ContactContract.ContactEntry.FIRST_NAME);
            if (name == null) {
                validData = false;
            }
        }
        if (contentValues.containsKey(ContactContract.ContactEntry.SECOND_NAME)) {
            String name = contentValues.getAsString(ContactContract.ContactEntry.SECOND_NAME);
            if (name == null) {
                validData = false;
            }
        }
        if (contentValues.containsKey(ContactContract.ContactEntry.TELEPHONE_NUMBER))
        {
            String name = contentValues.getAsString(ContactContract.ContactEntry.TELEPHONE_NUMBER);
            if (name == null) {
                validData = false;
            }
        }
        if (contentValues.containsKey(ContactContract.ContactEntry.ICON_PATH))
        {
            String name = contentValues.getAsString(ContactContract.ContactEntry.ICON_PATH);
            if (name == null) {
                selection = ContactContract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                Cursor cursor = query(uri, new String[]{ContactContract.ContactEntry.ICON_PATH},selection,selectionArgs,null);
                if (cursor != null && cursor.moveToNext())
                {
                    name = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
                    if (name != null)
                        contentValues.put(ContactContract.ContactEntry.ICON_PATH, name);
                    else
                        contentValues.put(ContactContract.ContactEntry.ICON_PATH, "");
                }
                else
                    validData= false;

            }
        }
        if (validData) {
            SQLiteDatabase database = contactDBHealper.getWritableDatabase();
            return database.update(ContactContract.ContactEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        }

        return 0;
    }
}
