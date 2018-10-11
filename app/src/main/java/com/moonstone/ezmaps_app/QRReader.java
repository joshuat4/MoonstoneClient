package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
//import com.google.android.gms.internal.

public class QRReader extends Activity {
//    BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this) .setBarcodeFormats(Barcode.QR_CODE) .build();
    TextView barcodeResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("QRREADER CREATED");
        Log.d("DEBUG_QR", "QRReader CREATED");

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_qr_code_scanner);
        barcodeResult = (TextView)findViewById(R.id.barcode_result);
    }
    public void scanBarcode(View v){
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("DEBUG_QR", "onActivityResult: ");
        if(requestCode == 0){
            if (resultCode == CommonStatusCodes.SUCCESS){
                if(data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    System.out.println("SUCCESSFUL QR");
                    Log.d("DEBUG_QR", "onActivityResult: " + barcode.displayValue);
                    barcodeResult.setText("Barcode value: " + barcode.displayValue);
                } else {
                    barcodeResult.setText("No barcode found");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
