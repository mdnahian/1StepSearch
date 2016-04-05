package com.onestepsearch.onestepsearch.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mdislam on 3/3/16.
 */
public class HistoryFragment extends Fragment {

    private ParentActivity parentActivity;

    private ListView historyList;
    private ArrayList<String> searches;
    private TextView numSearches;
    private TextView clearBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        historyList = (ListView) rootView.findViewById(R.id.history_list);
        numSearches = (TextView) rootView.findViewById(R.id.num_searches);
        clearBtn = (TextView) rootView.findViewById(R.id.clearBtn);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.clearHistory();
                showSearches();
            }
        });

//        ParseUser parseUser = ParseUser.getCurrentUser();
//        String numOfSearches  = parseUser.getInt("currentNumOfSearches") + "/" + parseUser.getInt("numOfSearches");
//        numSearches.setText(numOfSearches);

        showSearches();

        return rootView;
    }


    private void showSearches(){
        searches = ((ParentActivity) getActivity()).getSearches();
        Collections.reverse(searches);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, searches);
        historyList.setAdapter(arrayAdapter);
    }


}
