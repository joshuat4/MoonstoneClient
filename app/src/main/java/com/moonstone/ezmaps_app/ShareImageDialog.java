package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ShareImageDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private Button shareCurrentImage;
    private Button shareAllImages;
    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_share_image, container, false);

        Button shareCurrentImage = view.findViewById(R.id.shareCurrentImage);
        Button shareAllImages = view.findViewById(R.id.shareAllImages);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        shareCurrentImage.setOnClickListener(this);
        shareAllImages.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.shareCurrentImage:
                this.dismiss();
                startActivity(new Intent(getActivity(), ChooseContacts.class));
                break;

            case R.id.shareAllImages:
                this.dismiss();
                startActivity(new Intent(getActivity(), ChooseContacts.class));
                break;

            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }



}