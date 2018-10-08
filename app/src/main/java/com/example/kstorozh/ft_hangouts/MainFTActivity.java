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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
    private static final int CM_SMS_SEND_ID = 3;
    private static final int CM_SMS_READ_ID = 4;

    String[] PERMISSIONS = {android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_SMS,
            Manifest.permission.CALL_PHONE
    };

    private static final int REQUEST= 112;

    private ListView myListView;
    public ContactsCursoreAdapter contactsCursoreAdapter;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ft);


        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST );
        }
        else {
            Log.d(LOG_TAG, "Application is already has permissions");
        }
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

    private boolean hasPermissions() {

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTulbarColour();
    }

    @SuppressLint("ResourceType")
    private void setTulbarColour() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO check why parsing from first time is incorect
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
        menu.add(1, CM_CALL_ID, 1, R.string.call_to_contact);
        menu.add(3, CM_SMS_SEND_ID, 3, R.string.sendSms);
        menu.add(4, CM_SMS_READ_ID, 4, R.string.getSms);
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
        if (item.getItemId() == CM_SMS_SEND_ID || item.getItemId() == CM_CALL_ID || item.getItemId() == CM_SMS_READ_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Uri getInfoUri = ContentUris.withAppendedId(ContactContract.ContactEntry.CONTENT_URI, acmi.id);
            String[] projection = {
                    ContactContract.ContactEntry._ID,
                    ContactContract.ContactEntry.FIRST_NAME,
                    ContactContract.ContactEntry.SECOND_NAME,
                    ContactContract.ContactEntry.TELEPHONE_NUMBER};
            Cursor cursor = getContentResolver().query(getInfoUri, projection, null, null, null);
            String fName = null;
            String sName = null;
            String tel = null;


            if (cursor != null && cursor.moveToNext()) {
                fName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME));
                sName = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME));
                tel = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));


                switch (item.getItemId()) {
                    case (CM_SMS_SEND_ID) : {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", tel, null));
                        smsIntent.putExtra("sms_body", "Hello " + fName + " " + sName);
                        if (smsIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(smsIntent);
                        }
                        break;
                    }
                    case (CM_CALL_ID):{
                        Intent telIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                        if (telIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(telIntent);
                        }
                        break;
                    }
                    case (CM_SMS_READ_ID) : {
                        Intent intent = new Intent(MainFTActivity.this, ReadSMS.class);
                        intent.putExtra("sms_body", "Hello " + fName + " " + sName);
                        intent.putExtra("TELEPHONE", tel);
                        startActivity(intent);
                        break; }
                }
            }
            else {
                Log.d(LOG_TAG, "Cursor is empty"); }
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





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode)
        {
            case REQUEST : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");

                }
            }

        }

    }


}
