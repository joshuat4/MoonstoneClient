package com.moonstone.ezmaps_app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Picasso;

public class ImageSendingActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private ImageView mImageView;
    private Button cancelButton;
    private Button uploadButton;
    private ProgressBar mProgressBar;

    private static final int MY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_upload);
        cancelButton = findViewById(R.id.cancelButton);
        uploadButton = findViewById(R.id.uploadButton);
        mImageView = findViewById(R.id.imageView);
        mProgressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if there is an upload happening currently, prevent Spamming
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ImageSendingActivity.this, "UploadActivity in progress", Toast.LENGTH_SHORT).show();

                } else {
                    uploadFile();

                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_REQUEST_CODE);

        }else{
            openCamera();
        }

    }

    // Open Camera App
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            Picasso.get().load(mImageUri).into(mImageView);

        }

    }

    public void setReturnSuccess(String imageUrl){
        returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        returnIntent.putExtra("imageUrl", imageUrl);
        finish();
    }

    public void setReturnFailure(){
        returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
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

                            // Delay progress bar since it's too fast (5000 = 5sec)
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable () {
                                @Override
                                public void run(){
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            // Get the download URL of the Image from Firebase Storage
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("ImageSendingActivity", "Download Url received: " + mImageUri);
                                    setReturnSuccess(mImageUri.toString());

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    Log.d("ImageSendingActivity", "Download Url NOT received");
                                    setReturnFailure();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ImageSendingActivity", "Download Url NOT received");
                            setReturnFailure();


                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Update the progress bar
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);

                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}