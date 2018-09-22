package com.example.kstorozh.ft_hangouts;



import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import com.example.kstorozh.ft_hangouts.data.ContactDBHealper;
import com.example.kstorozh.ft_hangouts.data.ContactContract;


public class MainFTActivity extends AppCompatActivity {

    private static final int CM_DELETE_ID = 1;
    private ListView myListView;  // the ListActivity's ListView
    public ContactsCursoreAdapter contactsCursoreAdapter;

    Cursor cursor;

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
        displayDataBaseInfoInList();



        //displayDatabaseInfo();
    }




    private void displayDataBaseInfoInList()
    {
        String[] projection = {
                ContactContract.ContactEntry._ID,
                ContactContract.ContactEntry.FIRST_NAME,
                ContactContract.ContactEntry.SECOND_NAME,
                ContactContract.ContactEntry.TELEPHONE_NUMBER};
        String selection  = null;
        String []selectionArgs = null;

        Cursor cursor  = getContentResolver().query(ContactContract.ContactEntry.CONTENT_URI, projection, null, null, null, null);



        //String [] headers = new  String[] {ContactContract.ContactEntry.FIRST_NAME, ContactContract.ContactEntry.TELEPHONE_NUMBER};
        //int [] to = new int[] {R.id.tvText1, R.id.tvText2};

        Log.d("MyContactProvider", "Cursor count is" + String.valueOf(cursor.getCount()));


        contactsCursoreAdapter = new ContactsCursoreAdapter(this, cursor);
        //myAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, headers, to);

        Log.d("MyContactProvider", "Adapter created");

        myListView = (ListView) findViewById(R.id.myList);
        //myListView.setAdapter(myAdapter);
        myListView.setAdapter(contactsCursoreAdapter);
        // добавляем контекстное меню к списку
        registerForContextMenu(myListView);

        View emptyView = findViewById(R.id.empty_view);
        myListView.setEmptyView(emptyView);

    }

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


        cursor  = getContentResolver().query(ContactContract.ContactEntry.CONTENT_URI, projection, null, null, null, null);







        //String [] headers = new  String[] {ContactContract.ContactEntry.FIRST_NAME, ContactContract.ContactEntry.TELEPHONE_NUMBER};
        //int [] to = new int[] {R.id.tvText1, R.id.tvText2};

        Log.d("MyContactProvider", "Cursor count is" + String.valueOf(cursor.getCount()));
        //myAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, headers, to);






        Log.d("MyContactProvider", "Adapter created");

        //myListView = (ListView) findViewById(R.id.myList);
        //myListView.setAdapter(myAdapter);
        //Toast.makeText(getApplication(), "Adapter seted", Toast.LENGTH_LONG).show();

        int columIndexId = cursor.getColumnIndex(ContactContract.ContactEntry._ID);
        int coloumIndexFN = cursor.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME);
        int coloumIndexSN = cursor.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME);
        int coloumIndexT = cursor.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER);





       // TextView displayView = (TextView) findViewById(R.id.tv_sql);
       // StringBuilder stringBuilder = new StringBuilder("Number of rows in pets database table: " + cursor.getCount() + "\n");




        try {

            /*while (cursor.moveToNext())
            {
                int currentID = cursor.getInt(columIndexId);
                stringBuilder.append(cursor.getInt(columIndexId) + " " + cursor.getString(coloumIndexFN) + " " + cursor.getString(coloumIndexSN) + " " + cursor.getString(coloumIndexT) + "\n");
            }
            displayView.setText(stringBuilder);
*/



            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
        //displayView.setText(stringBuilder);
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
        displayDataBaseInfoInList();
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
                displayDataBaseInfoInList();
                return true;
            case R.id.action_delete_all_data:
                int howMuchWasRemuved = getContentResolver().delete(ContactContract.ContactEntry.CONTENT_URI,null,null);
                Toast.makeText(getApplicationContext(), "howMuchWasRemuved " + howMuchWasRemuved, Toast.LENGTH_LONG).show();
                displayDataBaseInfoInList();
                return true;
        }




        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД

            Uri delUri = ContentUris.withAppendedId(ContactContract.ContactEntry.CONTENT_URI, acmi.id);

            int howMuchwasDelited  = getContentResolver().delete(delUri, null, null);
            Toast.makeText(getApplicationContext(), "how much was delited " + howMuchwasDelited, Toast.LENGTH_LONG).show();
            contactsCursoreAdapter.notifyDataSetChanged();
            displayDataBaseInfoInList();
            ///db.delRec(acmi.id);
            // обновляем курсор

            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //db.close();
    }
















}
