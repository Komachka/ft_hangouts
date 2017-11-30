package com.example.kstorozh.ft_hangouts;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.kstorozh.ft_hangouts.data.ContactContract;
import com.example.kstorozh.ft_hangouts.data.ContactDBHealper;

import java.text.ParseException;

public class EditActivity extends AppCompatActivity {

    private EditText edit_first_name;
    private EditText edit_second_name;
    private EditText edit_telephone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edit_first_name = (EditText)findViewById(R.id.edit_first_name);
        edit_second_name = (EditText)findViewById(R.id.edit_second_name);
        edit_telephone_number = (EditText)findViewById(R.id.edit_telephon_number);


//

    }




    private boolean insertContact()
    {

        // Gets the data repository in write mode
        ContactDBHealper mDbHelper = new ContactDBHealper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        String first_name = edit_first_name.getText().toString().trim();
        String second_name = edit_second_name.getText().toString().trim();
        int telephone_number;
        try {
            telephone_number = Integer.parseInt(edit_telephone_number.getText().toString().trim());
        }
        catch (Exception e)
        {
            telephone_number = 0; // need to figure how to change it
        }


        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.FIRST_NAME, first_name);
        values.put(ContactContract.ContactEntry.SECOND_NAME, second_name);
        values.put(ContactContract.ContactEntry.TELEPHONE_NUMBER, telephone_number);
        values.put(ContactContract.ContactEntry.ICON_PATH, "");

// Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                ContactContract.ContactEntry.TABLE_NAME,
                null,
                values);

        Log.v(MainFTActivity.class.toString(), "New row id = " + newRowId);

        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }


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

        switch (_id)
        {
            case R.id.action_safe:
                insertContact();
                finish();
                return true;
        }




        return super.onOptionsItemSelected(item);
    }

}
