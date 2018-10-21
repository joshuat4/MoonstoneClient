package com.moonstone.ezmaps_app.ezchat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.moonstone.ezmaps_app.R;
import com.squareup.picasso.Picasso;


/* Image Sending from Chat Activity */
public class ImageSendingActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    public Uri mImageUri;
    public ImageView mImageView;
    private StorageTask mUploadTask;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private Button cancelButton;
    private Button uploadButton;
    private ProgressBar mProgressBar;

    private Toolbar toolbar;
    private ActionBar actionbar;
    private static final int PICK_IMAGE_REQUEST = 1;


    private static final int MY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_image_upload);
        cancelButton = findViewById(R.id.cancelButton);
        uploadButton = findViewById(R.id.uploadButton);
        mImageView = findViewById(R.id.imageView);
        mProgressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.close_dark);
        actionbar.setTitle("Image Sending");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

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

        openFileChooser();


    }

    //Choose Image from Folder
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    Intent returnIntent;

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

    // When image is successful
    public void setReturnSuccess(String imageUrl){
        returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        returnIntent.putExtra("imageUrl", imageUrl);
        finish();
    }


    // When image returned is a failure
    public void setReturnFailure(){
        returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    // get the file extension of the URi
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

    // Upload file from android gallery to Firebase Storage
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
                                    Log.d("ImageSendingActivity", "Download Url received: " + uri);
                                    setReturnSuccess(uri.toString());

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