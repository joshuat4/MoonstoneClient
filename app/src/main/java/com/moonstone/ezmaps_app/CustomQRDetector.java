package com.moonstone.ezmaps_app;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class CustomQRDetector extends Detector {
    public SparseArray<Barcode> detect(Frame frame) {
        return null;
    }

    public boolean isOperational() {
        return super.isOperational();
    }
}
