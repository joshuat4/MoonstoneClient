package com.moonstone.ezmaps_app.ezdirection;

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

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.contact.ChooseContactsActivity;

import java.util.ArrayList;

public class ShareImageDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Button shareCurrentImage;
    private Button shareAllImages;
    private Button cancelButton;

    private Bundle bundle;
    private ArrayList<String> currentImageUrlsList;
    private int currentCounter;
    private Intent currentIntent;

    private int REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_share_image, container, false);

        currentCounter = getArguments().getInt("counter");
        currentImageUrlsList = getArguments().getStringArrayList("imageUrlsList");

        Button shareCurrentImage = view.findViewById(R.id.shareCurrentImage);
        Button shareAllImages = view.findViewById(R.id.shareAllImages);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        shareAllImages.setText("Share All Images " + "(" + currentImageUrlsList.size() + ")");

        currentIntent = new Intent(getActivity(), ChooseContactsActivity.class);

        shareCurrentImage.setOnClickListener(this);
        shareAllImages.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }


    public int ALL_IMAGES = -1;
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.shareCurrentImage:
                this.dismiss();
                Log.d("ShareImageDialogFragment", "Share Current Image  ");
                currentIntent.putStringArrayListExtra("currentImageUrlsList", currentImageUrlsList);
                currentIntent.putExtra("fromChooseContacts", true);
                currentIntent.putExtra("currentCounter", currentCounter);
                startActivityForResult(currentIntent, REQUEST_CODE);
                break;

            case R.id.shareAllImages:
                this.dismiss();
                Log.d("ShareImageDialogFragment", "Share All Images ");
                currentIntent.putStringArrayListExtra("currentImageUrlsList", currentImageUrlsList);
                currentIntent.putExtra("fromChooseContacts", true);
                currentIntent.putExtra("currentCounter", ALL_IMAGES);
                startActivityForResult(currentIntent, REQUEST_CODE);
                break;

            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }



}