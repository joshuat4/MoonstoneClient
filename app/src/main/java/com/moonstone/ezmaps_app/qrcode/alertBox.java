package com.moonstone.ezmaps_app.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.moonstone.ezmaps_app.R;


public class alertBox extends DialogFragment {

    Context mContext;
    private static boolean state = false;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_MaterialComponents_Light_DarkActionBar);
        // Use the Builder class for convenient dialog construction

        //create the dialog box with the required buttons and text.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Friend request sent!");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // after clicking okay..
                dismiss();
                state = true;
                getActivity().finish();

            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public boolean getState(){
        return state;
    }


}