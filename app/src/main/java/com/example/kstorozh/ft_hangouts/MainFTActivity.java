package com.example.kstorozh.ft_hangouts;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.kstorozh.ft_hangouts.data.ContactContract;


public class MainFTActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

   public static final String LOG_TAG = MainFTActivity.class.getSimpleName();


    private static final int CM_DELETE_ID = 1;
    private static final int CM_CALL_ID = 2;
    private static final int CM_SMS_ID = 3;
    private static final int CM_SMS_READ_ID = 4;


    private static final int MY_PERMISSIONS_REQUEST_CALL = 10;

    private ListView myListView;
    public ContactsCursoreAdapter contactsCursoreAdapter;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ft);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        invalidateOptionsMenu();
        setTulbarColour();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainFTActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        if (!IsTelephonePermissionGranted())
            requestTelephoneCallPermission();

        myListView = (ListView) findViewById(R.id.myList);
        // добавляем контекстное меню к списку
        View emptyView = findViewById(R.id.empty_view);
        myListView.setEmptyView(emptyView);

        contactsCursoreAdapter = new ContactsCursoreAdapter(this, null);
        myListView.setAdapter(contactsCursoreAdapter);

        getLoaderManager().initLoader(0, null, this);


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainFTActivity.this, EditActivity.class);
                Uri uri = Uri.withAppendedPath(ContactContract.ContactEntry.CONTENT_URI, String.valueOf(id));
                intent.setData(uri);
                startActivity(intent);

            }
        });
        registerForContextMenu(myListView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setTulbarColour();
    }

    @SuppressLint("ResourceType")
    private void setTulbarColour() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String toolbarColour = sharedPreferences.getString(PrefActivity.SHAR_KEY, getResources().getString(R.color.defTulbar));
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
        toolbar.setBackgroundColor(color);

    }


    private boolean insertContact() {

        // Gets the data repository in write mode


// Create a new map of values, where column names are the keys
        String first_name = "Katya";
        String second_name = "Sorozh";
        int telephone_number = 5555555;

        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.FIRST_NAME, first_name);
        values.put(ContactContract.ContactEntry.SECOND_NAME, second_name);
        values.put(ContactContract.ContactEntry.TELEPHONE_NUMBER, telephone_number);
        values.put(ContactContract.ContactEntry.ICON_PATH, "");

        getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int _id = item.getItemId();

        switch (_id) {
            case R.id.action_insert_data:
                insertContact();
                return true;
            case R.id.action_delete_all_data:
                deliteAll();
                return true;
            case R.id.action_options:
                startActivity(new Intent(this, PrefActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deliteAll() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Are you shure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int howMuchWasRemuved = getContentResolver().delete(ContactContract.ContactEntry.CONTENT_URI, null, null);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });


        AlertDialog dialog=builder.create();
        dialog.show();

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(1, CM_CALL_ID, 1, "Call to contact");
        menu.add(3, CM_SMS_ID, 3, "Sent sms to contact");
        menu.add(4, CM_SMS_READ_ID, 4, "Read sms");
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            Uri delUri = ContentUris.withAppendedId(ContactContract.ContactEntry.CONTENT_URI, acmi.id);
            int howMuchwasDelited = getContentResolver().delete(delUri, null, null);
            return true;
        }
        if (item.getItemId() == CM_SMS_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Uri getInfoUri = ContentUris.withAppendedId(ContactContract.ContactEntry.CONTENT_URI, acmi.id);
            String[] projection = {
                    ContactContract.ContactEntry._ID,
                    ContactContract.ContactEntry.FIRST_NAME,
                    ContactContract.ContactEntry.SECOND_NAME,
                    ContactContract.ContactEntry.TELEPHONE_NUMBER};
            Cursor cursor = getContentResolver().query(getInfoUri, projection, null, null, null);
            if (cursor != null && cursor.moveToNext()) {

                String fName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME));
                String sName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME));
                String tel = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", tel, null));
                smsIntent.putExtra("sms_body", "Hello " + fName + " " + sName);
                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                }
            }
        }
        if (item.getItemId() == CM_CALL_ID || item.getItemId() == CM_SMS_READ_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Uri getInfoUri = ContentUris.withAppendedId(ContactContract.ContactEntry.CONTENT_URI, acmi.id);
            String[] projection = {
                    ContactContract.ContactEntry._ID,
                    ContactContract.ContactEntry.TELEPHONE_NUMBER};
            Cursor cursor = getContentResolver().query(getInfoUri, projection, null, null, null);
            String tel = null;
            if (cursor != null && cursor.moveToNext()) {
                tel = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));
            }
            if (item.getItemId() == CM_CALL_ID) {
                Intent telIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                if (telIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(telIntent);
                }
            }
            if (item.getItemId() == CM_SMS_READ_ID)
            {
                Intent intent = new Intent(MainFTActivity.this, ReadSMS.class);
                intent.putExtra("TELEPHONE", tel);
                startActivity(intent);


            }
        }

        return super.onContextItemSelected(item);
    }



    //methods for work with cursor loader in background
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ContactContract.ContactEntry._ID,
                ContactContract.ContactEntry.FIRST_NAME,
                ContactContract.ContactEntry.SECOND_NAME,
                ContactContract.ContactEntry.TELEPHONE_NUMBER,
                ContactContract.ContactEntry.ICON_PATH};
        String selection  = null;
        String []selectionArgs = null;
        return new CursorLoader(this, ContactContract.ContactEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(MainFTActivity.class.getSimpleName(), "onLoadFinished");
        contactsCursoreAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactsCursoreAdapter.swapCursor(null);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setVisible(false);
        return true;
    }




    private boolean IsTelephonePermissionGranted()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestTelephoneCallPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            Log.d(EditActivity.class.getSimpleName(), "activity shouldShowRequestPermissionRationale");
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(LOG_TAG, "In onRequestPermissionsResult")  ;
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CALL: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission is OK", Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(this, "Can not make a call becouse of no permissions", Toast.LENGTH_LONG).show();
                }
            }
        }

    }








}
