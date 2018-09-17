package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.vision.text.Text;

import org.json.JSONObject;

import butterknife.BindView;

public class Tab2Fragment extends Fragment {
    @BindView(R.id.searchButton) Button searchButton;


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_two, container, false);

            Button button = (Button) view.findViewById(R.id.searchButton);
            final EditText source = (EditText) view.findViewById(R.id.searchBar);
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
        showSoftwareKeyboard(false);
    }

    protected void showSoftwareKeyboard(boolean showKeyboard){
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
