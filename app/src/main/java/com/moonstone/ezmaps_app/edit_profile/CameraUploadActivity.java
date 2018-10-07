package com.moonstone.ezmaps_app.edit_profile;

import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

import com.squareup.picasso.Picasso;

public class CameraUploadActivity extends UploadActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openCamera();

    }

    // Open Camera App
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }

    }

    // Display Image on Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When the activity takes a picture, the app does returns data.getData() == null
        // A way to circumvent this is by opening up the Camera roll after taking a picture
        if(data.getData() == null){
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (pickImageIntent.resolveActivity(getPackageManager()) != null)
                Log.d("CAMERAUPLOAD", "DATA == NULL, new INTENT");
                startActivityForResult(pickImageIntent, CAMERA_REQUEST_CODE);
        }

        Log.d("CAMERAUPLOAD", "RES CODE " + resultCode + "/ " + RESULT_OK);
        Log.d("CAMERAUPLOAD", "REQ CODE  " + requestCode + "/ " + CAMERA_REQUEST_CODE);
        Log.d("CAMERAUPLOAD", "DATA " + data);
        Log.d("CAMERAUPLOAD", "DATA.getDATA() " + data.getData());


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            mImageUri = data.getData();

            Log.d("CAMERAUPLOAD", "DATA " + mImageUri);

            Picasso.get().load(mImageUri).into(mImageView);


        }

    }


}
