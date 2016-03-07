package com.mdislam.onestep;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mdislam on 3/3/16.
 */
public class HistoryFragment extends Fragment {

    private ListView historyList;
    private ArrayList<String> searches;
    private TextView numSearches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);

        historyList = (ListView) rootView.findViewById(R.id.history_list);
        searches = ((ParentActivity) getActivity()).getSearches();
        Collections.reverse(searches);
        numSearches = (TextView) rootView.findViewById(R.id.num_searches);

        String numOfSearches  = searches.size() + "/100";

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, searches);
        historyList.setAdapter(arrayAdapter);

        numSearches.setText(numOfSearches);


        return rootView;
    }



}
