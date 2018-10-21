package com.moonstone.ezmaps_app.ezprofile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.view.View.OnClickListener;

import com.moonstone.ezmaps_app.R;


//this class is the fragment that appears when a user clicks on edit profile pic
public class UploadDialogFragment extends BottomSheetDialogFragment implements OnClickListener {

    private Button _choosePhote;
    private Button _takePhoto;
    private Button _cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_image, container, false);

        Button _choosePhoto = view.findViewById(R.id.choosePhoto);
        Button _cancelButton = view.findViewById(R.id.cancelButton);


        _choosePhoto.setOnClickListener(this);
        _cancelButton.setOnClickListener(this);

        return view;
    }


    //the fragment has only two buttons choose photos or cancel.
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.choosePhoto:
                this.dismiss();
                startActivity(new Intent(getActivity(), ImageUploadActivity.class));
                break;


            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }



}
