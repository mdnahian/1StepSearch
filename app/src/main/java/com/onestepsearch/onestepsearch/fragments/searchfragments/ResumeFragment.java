package com.onestepsearch.onestepsearch.fragments.searchfragments;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.activities.ViewResumeActivity;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.onestepsearch.onestepsearch.data.Resume;
import com.onestepsearch.onestepsearch.parsers.ParseResumes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mdislam on 3/10/16.
 */
public class ResumeFragment extends Fragment {

    private ParentActivity parentActivity;

    private ListView results;
    private ProgressDialog progressDialog;
    private ArrayList<Resume> resumes;
    private static final String RESUME_REQUEST_URL = "resumes.php";


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

        DownloadResumeString downloadResumeString = new DownloadResumeString();
        downloadResumeString.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }


    private class ViewResumeFull extends AsyncTask<String, Void, String> {

        private Resume resume;

        public ViewResumeFull(Resume resume) {
            this.resume = resume;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContents = viewResumeFullFromURL(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseResumeFullJSON(s, resume);
        }

        private String viewResumeFullFromURL(String resumeURL){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+RESUME_REQUEST_URL+"?t=view&url="+ Uri.encode(resumeURL, "UTF-8"));
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


    private void parseResumeFullJSON(String json, Resume resume){

        try{

            Resume resumeCopy = resume;

            JSONObject jsonObject = new JSONObject(json);
            resumeCopy.setSummary(jsonObject.getString("summary"));

            String experiences = "";
            JSONArray jsonArray = jsonObject.getJSONArray("experiences");
            for(int i=0; i<jsonArray.length(); i++){
                experiences += jsonArray.getString(i)+"\n\n";
            }

            resumeCopy.setExperience(experiences);

            String education = "";
            JSONArray jsonArray1 = jsonObject.getJSONArray("education");
            for(int i=0; i<jsonArray1.length(); i++){
                education += jsonArray1.getString(i)+"\n\n";
            }

            resumeCopy.setEducation(education);
            resumeCopy.setSkills(jsonObject.getString("skills"));
            resumeCopy.setOther(jsonObject.getString("other"));

            Intent intent = new Intent(getActivity().getApplicationContext(), ViewResumeActivity.class);
            intent.putExtra("resume", resumeCopy);
            startActivity(intent);

            progressDialog.dismiss();


        } catch (JSONException e){
            Log.d("Crash", "Could not parse json.");
        }
    }


    private class DownloadResumeString extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadResumeStringFromURL(params[0]);

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
                parseResumeJSON(s);
            }
        }

        private String downloadResumeStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+RESUME_REQUEST_URL+"?q="+ Uri.encode(query, "UTF-8"));
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



    private void parseResumeJSON(String json){
        ParseResumes parseResumes = new ParseResumes(json);
        parseResumes.process();

        resumes = parseResumes.getResumes();

        if(resumes.size() > 0){
            ((ParentActivity) getActivity()).addSearchQuery("\""+getArguments().getSerializable("query").toString()+"\""+" in Resumes", savedSession);

            ResumeAdapter resumeAdapter = new ResumeAdapter();
            results.setAdapter(resumeAdapter);

            progressDialog.dismiss();

        } else {
            parentActivity.buildDialog("No Results Found", "Please make sure you spelled all words correctly and try again.");
            progressDialog.dismiss();
        }

    }



    private class ResumeAdapter extends ArrayAdapter<Resume> {

        public ResumeAdapter(){
            super(getActivity().getApplicationContext(), R.layout.resume_list_item, resumes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Resume resume = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.resume_list_item, parent, false);
            }


            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView location = (TextView) convertView.findViewById(R.id.location);
            TextView experiences = (TextView) convertView.findViewById(R.id.experiences);
            TextView updated = (TextView) convertView.findViewById(R.id.updated);
            ImageView viewBtn = (ImageView) convertView.findViewById(R.id.viewBtn);
            ImageView downloadBtn = (ImageView) convertView.findViewById(R.id.downloadBtn);

            name.setText(resume.getName());
            location.setText(resume.getLocation());
            experiences.setText(resume.getExperiences());
            updated.setText(resume.getUpdate());


            viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    ViewResumeFull viewResumeFull = new ViewResumeFull(resume);
                    viewResumeFull.execute(resume.getLink());
                }
            });


            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    DownloadResumeFull downloadResumeFull = new DownloadResumeFull(InputFilter.encodeFileName(resume.getName()) + ".pdf");
                    downloadResumeFull.execute(resume.getLink());
                }
            });


            return convertView;
        }
    }


    private class DownloadResumeFull extends AsyncTask<String, Void, String>{

        String name;

        public DownloadResumeFull(String name) {
            this.name = name;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadResumeURL(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null){
                parentActivity.notConnectedDialog();
            } else {
                downloadResumeFromURL(s, name);
            }
        }


        private String downloadResumeURL(String resumeURL){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+RESUME_REQUEST_URL+"?t=download&url="+ Uri.encode(resumeURL, "UTF-8"));
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



    private void downloadResumeFromURL(String resumeURL, String fileName){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(resumeURL));
            request.setDescription("Downloading Resume...");
            request.setTitle(fileName);

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/Resumes/";
            File resumeFile = new File(filePath);

            if(resumeFile.mkdir()){
                Log.d("Crash", "Made new Resume folder...");
            }

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationUri(Uri.parse("file://" + filePath + fileName));

            final DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);


            new Thread(new Runnable() {
                @Override
                public void run() {

                    boolean downloading = true;

                    while (downloading){

                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(downloadId);

                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();

                        int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    Toast.makeText(getActivity().getApplicationContext(), "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        cursor.close();
                    }

                }
            }).start();


        } else{
            Log.d("Crash", "Could not download resume, update os.");
        }

    }


}
