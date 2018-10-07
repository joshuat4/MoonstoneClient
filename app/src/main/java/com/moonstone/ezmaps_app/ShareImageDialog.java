package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class ShareImageDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private Button shareCurrentImage;
    private Button shareAllImages;
    private Button cancelButton;

    private Bundle savedInstanceState;
    private ArrayList<String> currentImageUrlsList;
    private int currentCounter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_share_image, container, false);

        this.savedInstanceState = savedInstanceState;
        currentImageUrlsList = savedInstanceState.getStringArrayList("imageUrlsList");
        currentCounter= savedInstanceState.getInt("counter");

        Log.d("ShareImageDialog", "Share Current Image ONCREATEVIEW");
        Log.d("ShareImageDialog", "Image List" + currentImageUrlsList);
        Log.d("ShareImageDialog", "Counter" + currentCounter);

        Button shareCurrentImage = view.findViewById(R.id.shareCurrentImage);
        Button shareAllImages = view.findViewById(R.id.shareAllImages);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        shareCurrentImage.setOnClickListener(this);
        shareAllImages.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }

    public static int REQUEST_CODE_Current_Image = 1;
    public static int REQUEST_CODE_All_Image = 2;
    private static final int RESULT_OK = -1;

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.shareCurrentImage:
                this.dismiss();
                currentImageUrlsList = savedInstanceState.getStringArrayList("imageUrlsList");
                currentCounter= savedInstanceState.getInt("counter");

                Log.d("ShareImageDialog", "Share Current Image BUTTON CLICK");
                Log.d("ShareImageDialog", "Image List" + currentImageUrlsList);
                Log.d("ShareImageDialog", "Counter" + currentCounter);

                /*Intent shareCurrentImage = new Intent(getActivity(), ChooseContacts.class);
                shareCurrentImage.putExtra("images", currentImageUrlsList);
                shareCurrentImage.putExtra("currentImage", currentCounter);
                startActivityForResult(shareCurrentImage, REQUEST_CODE_Current_Image);*/

                break;

            case R.id.shareAllImages:
                this.dismiss();
                currentImageUrlsList = savedInstanceState.getStringArrayList("imageUrlsList");
                currentCounter= savedInstanceState.getInt("counter");

                Log.d("ShareImageDialog", "Share Current Image BUTTON CLICK");
                Log.d("ShareImageDialog", "Image List" + currentImageUrlsList);
                Log.d("ShareImageDialog", "Counter" + currentCounter);




                /*Intent shareAllImages = new Intent(getActivity(), ChooseContacts.class);
                shareAllImages.putExtra("images", currentImageUrlsList);
                shareAllImages.putExtra("currentImage", currentCounter);
                startActivityForResult(shareAllImages, REQUEST_CODE_All_Image);
*/
                break;

            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }



}