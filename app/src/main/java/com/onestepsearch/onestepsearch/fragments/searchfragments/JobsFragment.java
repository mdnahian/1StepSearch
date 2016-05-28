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
import com.onestepsearch.onestepsearch.activities.ViewJobActivity;
import com.onestepsearch.onestepsearch.activities.WebViewActivity;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.onestepsearch.onestepsearch.data.Job;
import com.onestepsearch.onestepsearch.parsers.ParseJobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mdislam on 3/10/16.
 */
public class JobsFragment extends Fragment {

    private ParentActivity parentActivity;

    private ListView results;
    private ProgressDialog progressDialog;
    private ArrayList<Job> jobs;
    private static final String JOB_REQUEST_URL = "jobs.php";

    private SavedSession savedSession;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        savedSession = (SavedSession) getActivity().getIntent().getSerializableExtra("SavedSession");

        results = (ListView) rootView.findViewById(R.id.results);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DownloadJobsString downloadJobsString = new DownloadJobsString();
        downloadJobsString.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }




    private class DownloadJobsString extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadJobsStringFromURL(params[0]);

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
                parseJobsJSON(s);
            }
        }

        public String downloadJobsStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+JOB_REQUEST_URL+"?q="+ Uri.encode(query, "UTF-8"));
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

    private void parseJobsJSON(String json){
        ParseJobs parseJobs = new ParseJobs(json);
        parseJobs.process();

        jobs = parseJobs.getJobs();

        if(jobs.size() > 0){
            ((ParentActivity) getActivity()).addSearchQuery("\""+getArguments().getSerializable("query").toString()+"\""+" in Jobs",savedSession);


            JobsAdapter jobsAdapter = new JobsAdapter();
            results.setAdapter(jobsAdapter);

            progressDialog.dismiss();

        } else {
            parentActivity.buildDialog("No Results Found", "Please make sure you spelled all words correctly and try again.");
            progressDialog.dismiss();
        }


    }

    private class JobsAdapter extends ArrayAdapter<Job> {

        public JobsAdapter(){
            super(getActivity().getApplicationContext(), R.layout.job_list_item, jobs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Job job = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.job_list_item, parent, false);
            }


            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView location = (TextView) convertView.findViewById(R.id.location);
            TextView company = (TextView) convertView.findViewById(R.id.company);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            TextView date = (TextView) convertView.findViewById(R.id.date);


            title.setText(job.getTitle());
            location.setText(job.getLocation());
            company.setText(job.getCompany());
            description.setText(job.getDescription());
            date.setText(job.getDate());


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    ViewJobFull viewJobFull = new ViewJobFull(job);
                    viewJobFull.execute(job.getLink());
                }
            });


            return convertView;
        }
    }


    private class ViewJobFull extends AsyncTask<String, Void, String>{

        private Job job;

        public ViewJobFull(Job job) {
            this.job = job;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContents = viewJobFullFromURL(params[0]);

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
                parseJobsFull(s, job);
            }
        }

        private String viewJobFullFromURL(String jobURL){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+JOB_REQUEST_URL+"?url="+ Uri.encode(jobURL, "UTF-8"));
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


    private void parseJobsFull(String html, Job job){

        Job jobCopy = job;
        jobCopy.setSummary(html);

        if(jobCopy.getSummary().equals("")){
            Intent intent = new Intent(getActivity().getApplicationContext(), WebViewActivity.class);
            intent.putExtra("url", job.getLink());
            intent.putExtra("title", job.getTitle());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity().getApplicationContext(), ViewJobActivity.class);
            intent.putExtra("job", jobCopy);
            startActivity(intent);
        }

        progressDialog.dismiss();

    }





}
