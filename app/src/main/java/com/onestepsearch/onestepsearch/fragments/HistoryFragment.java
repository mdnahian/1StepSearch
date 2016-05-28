package com.onestepsearch.onestepsearch.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.core.SavedSession;

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

    private SavedSession savedSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        savedSession = (SavedSession) getActivity().getIntent().getSerializableExtra("SavedSession");
        if(savedSession.getUsername().equals("")){
            parentActivity.logout();
        }

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

        String numOfSearches  = savedSession.getCurrentNumOfSearches() + "/" + savedSession.getNumOfSearches();
        numSearches.setText(numOfSearches);

        showSearches();

        return rootView;
    }


    private void showSearches(){
        searches = ((ParentActivity) getActivity()).getSearches();
        Collections.reverse(searches);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.history_list_item, searches);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                String category = item.substring(item.lastIndexOf("in ") + 3);
                String query = item.replace(" in "+category, "").replaceAll("\"", "");

                Intent intent = new Intent(getActivity(), ParentActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("query", query);
                intent.putExtra("SavedSession", savedSession);
                startActivity(intent);
            }
        });


        historyList.setAdapter(arrayAdapter);



    }


}
