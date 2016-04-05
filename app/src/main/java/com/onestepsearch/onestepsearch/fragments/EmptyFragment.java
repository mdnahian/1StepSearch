package com.onestepsearch.onestepsearch.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onestepsearch.onestepsearch.R;

/**
 * Created by mdislam on 4/5/16.
 */
public class EmptyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.empty_fragment, container, false);

        return rootView;
    }

}
