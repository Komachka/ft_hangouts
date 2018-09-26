package com.example.kstorozh.ft_hangouts;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
//import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //camera
    static final int REQUEST_IMAGE_CAPTURE = 1;
    List<String> allPhotosWhichWasMadeFromCamera = new ArrayList<>();
    String mCurrentPhotoPath;
    ImageHelper imageHelper = new ImageHelper(this);

    private EditText edit_first_name;
    private EditText edit_second_name;
    private EditText edit_telephone_number;
    ImageView imageView;


    //SharedPreferences sharedPref;


    //for intent
    Uri currentContactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();

        edit_first_name = (EditText)findViewById(R.id.edit_first_name);
        edit_second_name = (EditText)findViewById(R.id.edit_second_name);
        edit_telephone_number = (EditText)findViewById(R.id.edit_telephon_number);
        imageView = (ImageView) findViewById(R.id.brouse_image);


        //store tmp photo from camera in sharedPreference file
        String defVal = "";
        //mCurrentPhotoPath = sharedPref.getString("mCurrentPhotoPath",defVal);


        //setup image
     /*   if (TextUtils.isEmpty(mCurrentPhotoPath))
        {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
            Log.d(EditActivity.class.getSimpleName(), "!!!!!!!!!!!!!!!!!! mCurrentPhotoPath empty  = "+ mCurrentPhotoPath);
        }
        else
        {
            imageView.setImageBitmap(Helper.bitmapFromPath(this,mCurrentPhotoPath));
        }*/


        currentContactUri = getIntent().getData();
        if (currentContactUri != null)
        {
            getSupportActionBar().setTitle("Edit contact");
            Log.d(EditActivity.class.getSimpleName(), "Edit pet " + currentContactUri.toString());
        }
        else
        {
            getSupportActionBar().setTitle("Add new contact");
        }
        getLoaderManager().initLoader(0,null,this);


    }

    //save to db
    private boolean saveContact()
    {
        String first_name = edit_first_name.getText().toString().trim();
        String second_name = edit_second_name.getText().toString().trim();
        String telephone_number = edit_telephone_number.getText().toString().trim();
        if (TextUtils.isEmpty(first_name) || TextUtils.isEmpty(second_name) || TextUtils.isEmpty(telephone_number))
        {
            Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        mCurrentPhotoPath = getImagePathToSave();

        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.FIRST_NAME, first_name);
        values.put(ContactContract.ContactEntry.SECOND_NAME, second_name);
        values.put(ContactContract.ContactEntry.TELEPHONE_NUMBER, telephone_number);
        values.put(ContactContract.ContactEntry.ICON_PATH, mCurrentPhotoPath);

        Uri insertUri = null;
        if (currentContactUri == null) {
             insertUri = getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);
            if (insertUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving contact", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "Contact saved with row id: " + insertUri, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else
        {

            int updatedRows = getContentResolver().update(currentContactUri,values,null,null);
            Toast.makeText(this, updatedRows + " row update", Toast.LENGTH_LONG).show();


            /*Cursor cursor = getContentResolver().query(currentContactUri,new String[]{ContactContract.ContactEntry.ICON_PATH},null,null,null);
            if (cursor != null && cursor.moveToNext())
            {
                String path = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
                if(!path.equals(mCurrentPhotoPath))
                {
                    File file = new File(path);
                    boolean deleted = file.delete();
                    Toast.makeText(this, "Old file " + path + "was deleted ? = " + deleted, Toast.LENGTH_LONG).show();
                }
            }*/
        }
        return true;
    }

    private String getImagePathToSave() {

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Log.d(EditActivity.class.getSimpleName(), "!!!!!!!!!!!!!!!!!! storageDir  = "+ storageDir.getAbsolutePath());
        for (File f: storageDir.listFiles()) {
            Log.d(EditActivity.class.getSimpleName(), "path   = "+ f.getAbsolutePath());

        }



        return mCurrentPhotoPath;

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

                    //удаляем из шерд преференсис линку про файл который нужно отображать
                    //SharedPreferences.Editor editor = sharedPref.edit();
                    //editor.putString("mCurrentPhotoPath", "");
                   // editor.commit();
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

            if (TextUtils.isEmpty(pathToIcon)) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                Log.d(EditActivity.class.getSimpleName(), "!!!!!!!!!!!!!!!!!! mCurrentPhotoPath  = "+ mCurrentPhotoPath);
            }
            else
            {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToIcon);
                imageView.setImageBitmap(bitmap);

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
                startActivityForResult(takePictueIntent, REQUEST_IMAGE_CAPTURE);

            }

        }
    }

    private File creteImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d(EditActivity.class.getSimpleName(), "Storage path is "  + storageDir);

        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(EditActivity.class.getSimpleName(), "Storage path is "  + storageDir);

        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            //Bitmap photoCaptireBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            //imageView.setImageBitmap(photoCaptireBitmap);
            imageHelper.setReducedImageSize(imageView, mCurrentPhotoPath);
        }
    }

/*    void setReducedImageSize(ImageView view, String imagePath)
    {
        int targetImageViewWidth = imageView.getWidth();
        int targetImageViewHeight = imageView.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        int cameraImageWidth = options.outWidth;
        int cameraImageHeight = options.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        options.inSampleSize = scaleFactor;
        options.inJustDecodeBounds = false;

        Bitmap photoReduceSizeBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        imageView.setImageBitmap(photoReduceSizeBitmap);
    }*/


}
