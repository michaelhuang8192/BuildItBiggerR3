package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Context mContext;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();

        Button button = (Button)root.findViewById(R.id.button_tell_joke);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)mContext).loadJokes();
            }
        });

        return root;
    }
}
