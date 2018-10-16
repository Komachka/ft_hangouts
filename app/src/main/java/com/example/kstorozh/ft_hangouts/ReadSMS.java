package com.example.kstorozh.ft_hangouts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    SharedPreferences sharedPreferences;
    Toolbar toolbar;



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

        //toolbar = (Toolbar) findViewById(R.id.toolbar);

        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(telephonNumber);


        setContentView(R.layout.activity_read_sms);
        smsListView = (ListView) findViewById(R.id.SMSList);
        View emptyView = findViewById(R.id.empty_view_sms);
        smsListView.setEmptyView(emptyView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessageList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener((AdapterView.OnItemClickListener) this);


        changeButtonColor();




        getLoaderManager().initLoader(0,null,this);
    }

    @SuppressLint("ResourceType")
    private void changeButtonColor() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO check why parsing from first time is incorect
        @SuppressLint("ResourceType") String toolbarColour = sharedPreferences.getString(PrefActivity.SHAR_KEY, getResources().getString(R.color.defTulbar));
        Log.d(EditActivity.class.getSimpleName(),"colour from shered pref " +  toolbarColour);
        int color;
        try {
            color = Color.parseColor("#" + toolbarColour);

        }
        catch (Exception e)
        {
            Log.d(EditActivity.class.getSimpleName(), "Parsing was faild");
            color = Color.parseColor(getResources().getString(R.color.defTulbar));
        }
        Button button = findViewById(R.id.button_sent);
        button.setBackgroundColor(color);
        ActionBar toolbar =  getSupportActionBar();
        toolbar.setBackgroundDrawable(new ColorDrawable(color));
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

            arrayAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arrayAdapter.clear();
    }

    public void sentSMS(View view) {
        EditText et = (EditText) findViewById(R.id.edit_sms);
        String body = et.getText().toString();
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", telephonNumber, null));
        smsIntent.putExtra("sms_body", body);
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        }
    }
}
