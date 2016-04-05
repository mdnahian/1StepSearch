package com.onestepsearch.onestepsearch.fragments.searchfragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onestepsearch.onestepsearch.R;


/**
 * Created by mdislam on 3/10/16.
 */
public class EmptyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_fragment, container, false);
    }
}
