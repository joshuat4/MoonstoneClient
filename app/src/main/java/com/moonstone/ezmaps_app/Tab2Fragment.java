package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.vision.text.Text;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;

public class Tab2Fragment extends Fragment {
    private ImageButton button;
    private EditText source;
    private ImageView image;


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_two, container, false);

            image = (ImageView) view.findViewById(R.id.image);
            Picasso.get()
                    .load("https://source.unsplash.com/WLUHO9A_xik/1600x900")
                    .into(image);
            image.setColorFilter(ContextCompat.getColor(getContext(), R.color.tblack));

            source = (EditText) view.findViewById(R.id.searchBar);
            button = (ImageButton) view.findViewById(R.id.searchButton);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Tab2Fragment.this.getActivity(), ezdirection.class);
                    String destination = source.getText().toString().trim();
                    intent.putExtra("destination", destination);
                    startActivity(intent);

                }
            });




        return view;


    }

    @Override
    public void onDetach() {
        super.onDetach();

        //hide keyboard when any fragment of this class has been detached
        // showSoftwareKeyboard(false);
    }

    protected void showSoftwareKeyboard(boolean showKeyboard){
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
