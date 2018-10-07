package com.moonstone.ezmaps_app.contact;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.moonstone.ezmaps_app.edit_profile.UploadActivity;
import com.squareup.picasso.Picasso;

public class ImageSendingActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

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

    Intent returnIntent;

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

        Log.d("ImageSendingActivity", "RES CODE " + resultCode + "/ " + RESULT_OK);
        Log.d("ImageSendingActivity", "REQ CODE  " + requestCode + "/ " + CAMERA_REQUEST_CODE);
        Log.d("ImageSendingActivity", "DATA " + data);
        Log.d("ImageSendingActivity", "DATA.getDATA() " + data.getData());


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            mImageUri = data.getData();

            Log.d("ImageSendingActivity", "DATA " + mImageUri);

            uploadFile();

            returnIntent = new Intent();
            if(mImageUri != null){
                Log.d("ImageSendingActivity", "Return Success");
                setResult(RESULT_OK, returnIntent);

            }else{
                setResult(RESULT_CANCELED);
                Log.d("ImageSendingActivity", "Return Failure");
            }

        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadFile() {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        if (mImageUri != null) {

            // Set the chosen file (image) a unique name
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Get the download URL of the Image from Firebase Storage
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    returnIntent.putExtra("image", mImageUri);

                                    Log.d("ImageSendingActivity", "Download Url received");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Log.d("ImageSendingActivity", "Download Url NOT received");
                                }
                            });

                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}