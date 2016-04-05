package com.onestepsearch.onestepsearch.fragments.searchfragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.activities.WebViewActivity;
import com.onestepsearch.onestepsearch.data.CraigslistAd;
import com.onestepsearch.onestepsearch.parsers.ParseCraigslist;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mdislam on 3/10/16.
 */
public class CraigslistFragment extends Fragment {

    private ParentActivity parentActivity;

    private ListView results;
    private ProgressDialog progressDialog;
    private ArrayList<CraigslistAd> craigslistAds;

    private static final String CRAIGSLIST_REQUEST_URL = "http://newyork.craigslist.org/search/sss?sort=rel&query=";
    private static final String CRAIGSLIST_PARSE_URL = "craigslist.php";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        results = (ListView) rootView.findViewById(R.id.results);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DownloadCraigslistString downloadCraigslistString = new DownloadCraigslistString();
        downloadCraigslistString.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }






    private class DownloadCraigslistString extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadCraigslistStringFromURL(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null){
                parentActivity.notConnectedDialog();
            } else {
                parseCraigslistHtml(s);
            }
        }

        private String downloadCraigslistStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(CRAIGSLIST_REQUEST_URL+ Uri.encode(query, "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];

                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }

                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();

            } catch (IOException e ){
                Log.d("Crash", "Could not read data.");
            } catch (SecurityException a){
                Log.d("Crash", "Do not have the permissions to connect to URL.");

            }

            return null;
        }

    }



    private void parseCraigslistHtml(String html){
        DownloadCraigslistJSON downloadCraigslistJSON = new DownloadCraigslistJSON();
        downloadCraigslistJSON.execute(html);
    }



    private class DownloadCraigslistJSON extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadCraigslistJSONFromURL(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null){
                parentActivity.notConnectedDialog();
            } else {
                parseCraigslistJSON(s);
            }
        }

        private String downloadCraigslistJSONFromURL(String html){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+CRAIGSLIST_PARSE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = Uri.encode("html", "UTF-8")+"="+Uri.encode(html, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];

                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }

                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();

            } catch (IOException e ){
                Log.d("LoginPrompt", "Error 201: Could not read data.");
            } catch (SecurityException a){
                Log.d("LoginPrompt", "Error 203: Do not have the permissions to connect to URL.");

            }

            return null;

        }
    }


    private void parseCraigslistJSON(String json){
        ParseCraigslist parseCraigslist = new ParseCraigslist(json);
        parseCraigslist.process();

        craigslistAds = parseCraigslist.getCraigslistAds();

        if(craigslistAds.size() > 0){
            ((ParentActivity) getActivity()).addSearchQuery(getArguments().getSerializable("query").toString());

            CraigslistAdapter craigslistAdapter = new CraigslistAdapter();
            results.setAdapter(craigslistAdapter);

            progressDialog.dismiss();

        } else {
            parentActivity.buildDialog("No Results Found", "Please make sure you spelled all words correctly and try again.");
            progressDialog.dismiss();
        }

    }


    private class CraigslistAdapter extends ArrayAdapter<CraigslistAd> {

        public CraigslistAdapter() {
            super(getActivity().getApplicationContext(), R.layout.craigslist_list_item, craigslistAds);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final CraigslistAd craigslistAd = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.craigslist_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView price = (TextView) convertView.findViewById(R.id.price);
            TextView location = (TextView) convertView.findViewById(R.id.location);
            TextView date = (TextView) convertView.findViewById(R.id.date);


            title.setText(craigslistAd.getTitle());
            price.setText(craigslistAd.getPrice());
            location.setText(craigslistAd.getLocation());
            date.setText(craigslistAd.getTime());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    Intent intent = new Intent(getActivity().getApplicationContext(), WebViewActivity.class);
                    intent.putExtra("url", "http://craigslist.com"+craigslistAd.getLink());
                    intent.putExtra("title", craigslistAd.getTitle());
                    startActivity(intent);

                    progressDialog.dismiss();
                }
            });

            return convertView;
        }
    }






}
