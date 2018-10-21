package com.moonstone.ezmaps_app.ezprofile;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import com.squareup.picasso.Picasso;

//image upload activity is the activity that is started up to select pictures to get them ready
//for upload.
public class ImageUploadActivity extends UploadActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openFileChooser();

    }

    //Choose Image from Folder
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Display Image on Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Log.d("IMAGEUPLOAD", "IMAGE UPLOAD");
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);

        }else{
            finish();
        }
    }


}
