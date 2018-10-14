package com.moonstone.ezmaps_app.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moonstone.ezmaps_app.R;


import java.io.IOException;
import java.util.List;

public class ScanBarcodeActivity extends Activity {
    SurfaceView cameraPreview;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String TAG = "DEBUGSCANBARCODEACTIVITY";

    private Button exitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Scan Barcode","Exit");
                finish();
            }
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }



    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size()>0){
                    Intent intent = new Intent();
                    intent.putExtra("barcode", (Parcelable) barcodes.valueAt(0)); //get last barcode added to array.
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    String barcodeResult = barcodes.valueAt(0).displayValue;
                    System.out.println("FINDME" + barcodeResult);

                    addContact(barcodeResult);
                    finish();
                }
            }
        });
    }

    public void sendFriendRequest(String targetEmailInput){
        Log.d("DEBUG_SCANBARCODEACTIVITY", "sendFriendRequest: " + targetEmailInput);

        final String Uid = mAuth.getUid();
        Log.d(TAG, "findUid: " + targetEmailInput);
        final String targetEmail = targetEmailInput;

        final String[] targetUid = new String[1];
        targetUid[0]= null;

        //Start of search portion of method.
        Log.d(TAG, "findUid: This Uid "+ Uid);
        Task<QuerySnapshot> d = db.collection("users").get();
        d.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //Once list of users is retrieved,
                List<DocumentSnapshot> list = task.getResult().getDocuments(); //put into a list of users

                for (DocumentSnapshot doc : list) { //for each document in list,
                    if (!doc.getId().equals(Uid)) { //only check if not checking this user.
                        // String match.
                        String email = doc.get("email").toString();

                        if (compareContacts(targetEmail, email)) {
                            targetUid[0] = doc.getId();
                            Log.d(TAG, "onComplete: "+ targetUid[0]);
                            // If found, call the add method.
                            addContactFromUid(targetUid[0]);

                        }

                    }
                }
                Log.d(TAG, "onComplete1: "+ targetUid[0]);

            }
        });
    }



    public void addContact(String targetEmailInput){
        Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: " + targetEmailInput);

        final String Uid = mAuth.getUid();
        Log.d(TAG, "findUid: " + targetEmailInput);
        final String targetEmail = targetEmailInput;

        final String[] targetUid = new String[1];
        targetUid[0]= null;

        //Start of search portion of method.
        Log.d(TAG, "findUid: This Uid "+ Uid);
        Task<QuerySnapshot> d = db.collection("users").get();
        d.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //Once list of users is retrieved,
                List<DocumentSnapshot> list = task.getResult().getDocuments(); //put into a list of users

                for (DocumentSnapshot doc : list) { //for each document in list,
                    if (!doc.getId().equals(Uid)) { //only check if not checking this user.
                        // String match.
                        String email = doc.get("email").toString();

                        if (compareContacts(targetEmail, email)) {
                            targetUid[0] = doc.getId();
                            Log.d(TAG, "onComplete: "+ targetUid[0]);
                            // If found, call the add method.
                            addSelfToUid(targetUid[0]);
//                            addContactFromUid(targetUid[0]);

                        }

                    }
                }
                Log.d(TAG, "onComplete1: "+ targetUid[0]);

            }
        });
    }

    public void addSelfToUid(String targetUidInput){
        final String targetUid = targetUidInput;
        final String selfUid = mAuth.getUid();
        if(targetUid!=null){
            db.collection("users").document(targetUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("DEBUG_SCANBARCODEACTIVITY", "addSelfToUid = " + targetUid);
                    db.collection("users").document(targetUid).update("requests", FieldValue.arrayUnion(selfUid));
//                            update("requests", selfUid);
                    Log.d(TAG, "addSelfToUid: SUCCESSFULLY ADDED " + selfUid + " to " + targetUid );
                }
            });
        } else {
            Log.d(TAG, "addSelfToUid: FAILED");

        }


    }

    public boolean addContactFromUid(String targetUidInput) {
        final String targetUid = targetUidInput;
        Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: " + targetUid);

        final String Uid = mAuth.getUid();
        Log.d(TAG, "addContact: line162 " + targetUid);

        if (targetUid != null) {
            Log.d("DEBUG_SCANBARCODEACTIVITY", "targetUid = " + targetUid);

            //
            db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(Uid).update("contacts", FieldValue.arrayUnion(targetUid));
                    Log.d("FINDRECYCLER", "SUCCESSFULLY ADDED: " + targetUid);
                }
            });



        } else {
            Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: COULD NOT FIND CONTACT");
            return false;
        }
        return false;
    }




    private boolean compareContacts(String text, String against){

        if(against.toUpperCase().contains(text.toUpperCase())){

            Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " SUCCESS");

            return true;
        }


        Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " FAILED");

        return false;
    }

}
