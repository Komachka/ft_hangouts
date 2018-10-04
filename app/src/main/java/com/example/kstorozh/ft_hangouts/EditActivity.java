package com.example.kstorozh.ft_hangouts;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
//import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kstorozh.ft_hangouts.data.ContactContract;
import com.example.kstorozh.ft_hangouts.data.ContactDBHealper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.media.ThumbnailUtils.extractThumbnail;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //camera
    static final int REQUEST_IMAGE_CAPTURE = 1;
    List<String> allPhotosWhichWasMadeFromCamera = new ArrayList<>();
    String mCurrentPhotoPath;


    private EditText edit_first_name;
    private EditText edit_second_name;
    private EditText edit_telephone_number;
    ImageView imageView;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    //for intent
    Uri currentContactUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String toolbarColour = sharedPreferences.getString(PrefActivity.SHAR_KEY, "FFFFFF");
        Log.d(EditActivity.class.getSimpleName(),"colour from shered pref " +  toolbarColour);
        int color = Color.parseColor("#"+toolbarColour);
        toolbar.setBackgroundColor(color);




        invalidateOptionsMenu();

        edit_first_name = (EditText)findViewById(R.id.edit_first_name);
        edit_second_name = (EditText)findViewById(R.id.edit_second_name);
        edit_telephone_number = (EditText)findViewById(R.id.edit_telephon_number);
        imageView = (ImageView) findViewById(R.id.brouse_image);

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

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String toolbarColour = sharedPreferences.getString(PrefActivity.SHAR_KEY, "FFFFFF");
        Log.d(EditActivity.class.getSimpleName(),"colour from shered pref " +  toolbarColour);
        int color = Color.parseColor("#"+toolbarColour);
        toolbar.setBackgroundColor(color);

    }

    //save to db
    private boolean saveContact()
    {
        String first_name = edit_first_name.getText().toString().trim();
        String second_name = edit_second_name.getText().toString().trim();
        String telephone_number = edit_telephone_number.getText().toString().trim();
        Log.d(EditActivity.class.getSimpleName(), "telephone_number " + telephone_number);
        if (TextUtils.isEmpty(first_name) || TextUtils.isEmpty(second_name) || TextUtils.isEmpty(telephone_number))
        {
            return false;
        }

        if (mCurrentPhotoPath == null)
            mCurrentPhotoPath = "";

        Log.d(EditActivity.class.getSimpleName(), "path to image " + mCurrentPhotoPath);

        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.FIRST_NAME, first_name);
        values.put(ContactContract.ContactEntry.SECOND_NAME, second_name);
        values.put(ContactContract.ContactEntry.TELEPHONE_NUMBER, telephone_number);
        values.put(ContactContract.ContactEntry.ICON_PATH, mCurrentPhotoPath);

        Uri insertUri = null;
        if (currentContactUri == null) {
             insertUri = getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);
            return true;
        }
        else
        {

            int updatedRows = getContentResolver().update(currentContactUri,values,null,null);


        }
        return true;
    }

    private String getImagePathToSave() {

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Log.d(EditActivity.class.getSimpleName(), "!!!!!!!!!!!!!!!!!! storageDir  = "+ storageDir.getAbsolutePath());
        for (File f: storageDir.listFiles()) {
            //Log.d(EditActivity.class.getSimpleName(), "path   = "+ f.getAbsolutePath());

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
                    finish();
                }
                break;
            case R.id.action_options:
                startActivity(new Intent(this, PrefActivity.class));
                break;
            default:
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
            String pathToIcon = data.getString(data.getColumnIndex(ContactContract.ContactEntry.ICON_PATH));
            String fName = data.getString(data.getColumnIndex(ContactContract.ContactEntry.FIRST_NAME));
            String sName = data.getString(data.getColumnIndex(ContactContract.ContactEntry.SECOND_NAME));
            String tel = data.getString(data.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER));


            Log.d(EditActivity.class.getSimpleName(), "tel " + data.getString(data.getColumnIndex(ContactContract.ContactEntry.TELEPHONE_NUMBER)));
            edit_first_name.setText(fName);
            edit_second_name.setText(sName);
            edit_telephone_number.setText(tel);

            if (TextUtils.isEmpty(pathToIcon)) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                mCurrentPhotoPath = "";
            }
            else
            {
                setBitmap(imageView, pathToIcon);
                mCurrentPhotoPath = pathToIcon;
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
        menu.findItem(R.id.action_delete_all_data).setVisible(false);
        menu.findItem(R.id.action_insert_data).setVisible(false);
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
            setBitmap(imageView, mCurrentPhotoPath);
        }
    }

    public void setBitmap(ImageView icon, String pathToIcon) {
        File file = new File(pathToIcon);
        if (!file.exists())
            return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToIcon, options);
        int scaleFactor = 2;
        options.inSampleSize = scaleFactor;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathToIcon, options);
        if (bitmap == null)
        {
            Log.d(EditActivity.class.getSimpleName(), "Can not create bitmap file");
            return;
        }
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(pathToIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = ImageHelper.getRotation(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        icon.setImageBitmap(bitmap);
    }

}
