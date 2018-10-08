package com.example.kstorozh.ft_hangouts;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kstorozh.ft_hangouts.data.SMSResiver;

import java.util.ArrayList;

public class ReadSMS extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = ReadSMS.class.getSimpleName();


    private static ReadSMS inst;
    ArrayList<String> smsMessageList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    String telephonNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        telephonNumber = getIntent().getStringExtra("TELEPHONE");
        if (telephonNumber != null)
        {
            Log.d(EditActivity.class.getSimpleName(), "sms from " + telephonNumber);
        }
        else
        {
            Log.e(LOG_TAG, "Something strange happened");
            finish();
        }
        setContentView(R.layout.activity_read_sms);
        //smsMessageList.add("");
        smsListView = (ListView) findViewById(R.id.SMSList);
        View emptyView = findViewById(R.id.empty_view_sms);
        smsListView.setEmptyView(emptyView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessageList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        //refreshSmsInbox();

        getLoaderManager().initLoader(0,null,this);
    }

    private void refreshSmsInbox() {
        Uri uri = Uri.parse("content://sms/inbox");
        String selection = "address = ?";
        String[] selectionArgs = null;
        try {
            selectionArgs = new String[]{telephonNumber};
        }
        catch (ParseException ex)
        {
            Log.d(LOG_TAG, "Parse excaption");
            return;
        }
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(uri, null, selection,selectionArgs, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

    //caled from smsResiver
    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }






    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }



/*    private boolean IsSMSPermissionGranted()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestREadAndSendSmsPermission()
    {
        Log.d(LOG_TAG, "In requestREadAndSendSmsPermission")  ;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS))
        {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(LOG_TAG, "In onRequestPermissionsResult")  ;
        switch (requestCode)
        {
            case MainFTActivity.SMS_PERMISSION_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    refreshSmsInbox();
            }

                else
                {
                    finish();
                }
            }
        }

    }
    public static ReadSMS instance() {
        return inst;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        try {
            String[] smsMessages = smsMessageList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri uri = Uri.parse("content://sms/inbox");
        String selection = "address = ?";
        String[] selectionArgs = null;
        try {
            selectionArgs = new String[]{telephonNumber};
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG, "Parse excaption");
        }
        if (selectionArgs != null) {
            return new CursorLoader(getApplicationContext(), uri, null, selection, selectionArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor smsInboxCursor) {


        if (smsInboxCursor != null && smsInboxCursor.moveToNext())
        {
            int indexBody = smsInboxCursor.getColumnIndex("body");
            int indexAddress = smsInboxCursor.getColumnIndex("address");
            if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
            arrayAdapter.clear();
            do {

                String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                        "\n" + smsInboxCursor.getString(indexBody) + "\n";
                arrayAdapter.add(str);


            } while (smsInboxCursor.moveToNext());

            //arrayAdapter.add(smsMessageList);
            arrayAdapter.notifyDataSetChanged();

        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arrayAdapter.clear();
    }
}
