package com.onestepsearch.onestepsearch.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.WebViewActivity;


/**
 * Created by mdislam on 12/26/15.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, container, false);

        rootView.findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), WebViewActivity.class);
                intent.putExtra("title", "1StepSearch Website");
                intent.putExtra("url", "http://1stepsearch.com");
                startActivity(intent);
            }
        });


        rootView.findViewById(R.id.terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), WebViewActivity.class);
                intent.putExtra("title", "Terms & Conditions");
                intent.putExtra("url", "http://1stepsearch.com/terms.html");
                startActivity(intent);
            }
        });


        rootView.findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), WebViewActivity.class);
                intent.putExtra("title", "Privacy Policy");
                intent.putExtra("url", "http://1stepsearch.com/privacy.html");
                startActivity(intent);
            }
        });



        return rootView;
    }

}
