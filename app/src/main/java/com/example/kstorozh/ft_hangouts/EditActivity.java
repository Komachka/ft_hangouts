package com.example.kstorozh.ft_hangouts;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kstorozh.ft_hangouts.data.ContactContract;
import com.example.kstorozh.ft_hangouts.data.ContactDBHealper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //camera
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;


    private EditText edit_first_name;
    private EditText edit_second_name;
    private EditText edit_telephone_number;
    ImageView imageView;
    SharedPreferences sharedPref;


    //for intent
    Uri currentContactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();

        edit_first_name = (EditText)findViewById(R.id.edit_first_name);
        edit_second_name = (EditText)findViewById(R.id.edit_second_name);
        edit_telephone_number = (EditText)findViewById(R.id.edit_telephon_number);
        imageView = (ImageView) findViewById(R.id.brouse_image);


        //store tmp photo from camera
        String defVal = "";
        mCurrentPhotoPath = sharedPref.getString("mCurrentPhotoPath",defVal);
        if (TextUtils.isEmpty(mCurrentPhotoPath)) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
            Log.d(EditActivity.class.getSimpleName(), "!!!!!!!!!!!!!!!!!! mCurrentPhotoPath  = "+ mCurrentPhotoPath);
        }
        else
        {
            imageView.setImageBitmap(Helper.bitmapFromPath(this,mCurrentPhotoPath));
        }



        currentContactUri = getIntent().getData();
        if (currentContactUri != null)
        {
            getSupportActionBar().setTitle("Edit pet");

            Log.d(EditActivity.class.getSimpleName(), "Edit pet " + currentContactUri.toString());
        }
        else
        {
            getSupportActionBar().setTitle("Add new pet");
        }
        getLoaderManager().initLoader(0,null,this);


    }


    private boolean saveContact()
    {

        // Create a new map of values, where column names are the keys
        String first_name = edit_first_name.getText().toString().trim();
        String second_name = edit_second_name.getText().toString().trim();
        String telephone_number = edit_telephone_number.getText().toString().trim();
        if (TextUtils.isEmpty(first_name) || TextUtils.isEmpty(second_name) || TextUtils.isEmpty(telephone_number))
        {
            Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.FIRST_NAME, first_name);
        values.put(ContactContract.ContactEntry.SECOND_NAME, second_name);
        values.put(ContactContract.ContactEntry.TELEPHONE_NUMBER, telephone_number);
        values.put(ContactContract.ContactEntry.ICON_PATH, mCurrentPhotoPath);

        Uri uri = null;
        if (currentContactUri == null) {
             uri = getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);
            if (uri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "Pet saved with row id: " + uri, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else
        {

            int updatedRows = getContentResolver().update(currentContactUri,values,null,null);
            Toast.makeText(this, updatedRows + " row update", Toast.LENGTH_LONG).show();
            Cursor cursor = getContentResolver().query(currentContactUri,new String[]{ContactContract.ContactEntry.ICON_PATH},null,null,null);
            if (cursor != null && cursor.moveToNext())
            {
                String path = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
                if(!path.equals(mCurrentPhotoPath))
                {
                    File file = new File(path);
                    boolean deleted = file.delete();
                    Toast.makeText(this, "Old file " + path + "was deleted ? = " + deleted, Toast.LENGTH_LONG).show();
                }
            }
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
            case R.id.action_save:
                if(saveContact()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("mCurrentPhotoPath", "");
                    editor.commit();
                    finish();
                }
        }




        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ContactContract.ContactEntry._ID,
                ContactContract.ContactEntry.FIRST_NAME,
                ContactContract.ContactEntry.SECOND_NAME,
                ContactContract.ContactEntry.TELEPHONE_NUMBER,
                ContactContract.ContactEntry.ICON_PATH
        };
        String selection  = null;
        String []selectionArgs = null;

        if (currentContactUri != null)
            return new CursorLoader(getApplicationContext(), currentContactUri, projection,selection,selectionArgs,null);
        else
            return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data != null && data.moveToNext()) {
            Toast.makeText(this, "data.getCount() " + data.getCount(), Toast.LENGTH_LONG).show();
            String pathToIcon = data.getString(data.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
            String fName = data.getString(data.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME));
            String sName = data.getString(data.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME));
            String tel = data.getString(data.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));

                //icon.setImageResource();
            edit_first_name.setText(fName);
            edit_second_name.setText(sName);
            edit_telephone_number.setText(tel);

            if (TextUtils.isEmpty(mCurrentPhotoPath)) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                Log.d(EditActivity.class.getSimpleName(), "!!!!!!!!!!!!!!!!!! mCurrentPhotoPath  = "+ mCurrentPhotoPath);
            }
            else
            {
                imageView.setImageBitmap(Helper.bitmapFromPath(this,mCurrentPhotoPath));
            }

        }

    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        edit_first_name.setText("");
        edit_second_name.setText("");
        edit_telephone_number.setText("");
        imageView.setImageResource(R.drawable.ic_launcher_foreground);

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
            MenuItem menuItem = menu.findItem(R.id.action_delete_all_data);
            menuItem.setVisible(false);
            return true;
    }


    public void chooseImage(View view)
    {
        Intent takePictueIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try
        {
            photoFile = creteImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
            if(takePictueIntent.resolveActivity(getPackageManager()) != null)
            {
                takePictueIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //takePictueIntent.setData(photoURI);
                startActivityForResult(takePictueIntent, REQUEST_IMAGE_CAPTURE);

            }

        }
    }

    private File creteImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {

            imageView.setImageBitmap(Helper.bitmapFromPath(this,mCurrentPhotoPath));

            //save img
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("mCurrentPhotoPath", mCurrentPhotoPath);
            editor.commit();

        }
    }


}
