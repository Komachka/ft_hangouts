package com.example.kstorozh.ft_hangouts;



import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.kstorozh.ft_hangouts.data.ContactDBHealper;
import com.example.kstorozh.ft_hangouts.data.ContactContract;


public class MainFTActivity extends AppCompatActivity {

    private ContactDBHealper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ft);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainFTActivity.this, EditActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDbHelper = new ContactDBHealper(this);
        displayDatabaseInfo();
    }

    /*private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        Log.v(MainFTActivity.class.toString(), "display info");
        mDbHelper = new ContactDBHealper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();



         String[] projection = {ContactContract.ContactEntry._ID, ContactContract.ContactEntry.FIRST_NAME, ContactContract.ContactEntry.SECOND_NAME, ContactContract.ContactEntry.TELEPHONE_NUMBER};
         //String[] projection = null;
         //String selection  = ContactContract.ContactEntry._ID + "=?";
         String selection  = null;
         //String []selectionArgs = new String[]{"1"};
         String []selectionArgs = null;


        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        // cursor = db.rawQuery("SELECT * FROM " + ContactContract.ContactEntry.TABLE_NAME, null);


        Cursor cursor = db.query(ContactContract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        //cursor.moveToFirst();

        int columIndexId = cursor.getColumnIndex(ContactContract.ContactEntry._ID);
        int coloumIndexFN = cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME);
        int coloumIndexSN = cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME);
        int coloumIndexT = cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER);

        TextView displayView = (TextView) findViewById(R.id.tv_sql);
        StringBuilder stringBuilder = new StringBuilder("Number of rows in pets database table: " + cursor.getCount() + "\n");




        try {

         while (cursor.moveToNext())
         {
             int currentID = cursor.getInt(columIndexId);
             stringBuilder.append(cursor.getInt(columIndexId) + " " + cursor.getString(coloumIndexFN) + " " + cursor.getString(coloumIndexSN) + " " + cursor.getString(coloumIndexT) + "\n");
         }




            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
        displayView.setText(stringBuilder);
    }*/


    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        Log.v(MainFTActivity.class.toString(), "display info");

        String[] projection = {
                ContactContract.ContactEntry._ID,
                ContactContract.ContactEntry.FIRST_NAME,
                ContactContract.ContactEntry.SECOND_NAME,
                ContactContract.ContactEntry.TELEPHONE_NUMBER};
        String selection  = null;
        String []selectionArgs = null;


//        Cursor cursor = db.query(
//                ContactContract.ContactEntry.TABLE_NAME,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null);
//        //cursor.moveToFirst();

        Cursor cursor  = getContentResolver().query(ContactContract.ContactEntry.CONTENT_URI, projection, null, null, null, null);
        int columIndexId = cursor.getColumnIndex(ContactContract.ContactEntry._ID);
        int coloumIndexFN = cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME);
        int coloumIndexSN = cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME);
        int coloumIndexT = cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER);

        TextView displayView = (TextView) findViewById(R.id.tv_sql);
        StringBuilder stringBuilder = new StringBuilder("Number of rows in pets database table: " + cursor.getCount() + "\n");




        try {

            while (cursor.moveToNext())
            {
                int currentID = cursor.getInt(columIndexId);
                stringBuilder.append(cursor.getInt(columIndexId) + " " + cursor.getString(coloumIndexFN) + " " + cursor.getString(coloumIndexSN) + " " + cursor.getString(coloumIndexT) + "\n");
            }




            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
        displayView.setText(stringBuilder);
    }
    private boolean insertContact()
    {

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
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int _id = item.getItemId();

        switch (_id)
        {
            case R.id.action_insert_data:
                insertContact();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_data:
                return true;
        }




        return super.onOptionsItemSelected(item);
    }
}
