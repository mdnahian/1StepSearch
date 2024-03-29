package com.onestepsearch.onestepsearch.fragments.searchfragments;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.onestepsearch.onestepsearch.activities.VideoViewerActivty;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.onestepsearch.onestepsearch.data.Sermon;
import com.onestepsearch.onestepsearch.parsers.ParseSermons;
import com.squareup.picasso.Picasso;

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
public class SermonFragment extends Fragment {

    private ParentActivity parentActivity;

    private ListView results;
    private ProgressDialog progressDialog;
    private ArrayList<Sermon> sermons;
    private static final String SERMON_REQUEST_URL = "sermons.php?q=";

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

        DownloadSermonJSON downloadSermonJSON = new DownloadSermonJSON();
        downloadSermonJSON.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }


    private class DownloadSermonJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadSermonJSONFromURL(params[0]);

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
                parseSermonsJSON(s);
            }
        }

        private String downloadSermonJSONFromURL(String query){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+SERMON_REQUEST_URL+ Uri.encode(query, "UTF-8"));
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

    private void parseSermonsJSON(String json){
        ParseSermons parseSermons = new ParseSermons(json);
        parseSermons.process();

        sermons = parseSermons.getSermons();

        if(sermons.size() > 0){
            ((ParentActivity) getActivity()).addSearchQuery("\"" + getArguments().getSerializable("query").toString() + "\"" + " in Sermons", savedSession);

            SermonsAdapter sermonsAdapter = new SermonsAdapter();
            results.setAdapter(sermonsAdapter);

            progressDialog.dismiss();

        } else {
            parentActivity.buildDialog("No Results Found", "Please make sure you spelled all words correctly and try again.");
            progressDialog.dismiss();
        }


    }

    private class SermonsAdapter extends ArrayAdapter<Sermon> {

        public SermonsAdapter() {
            super(getActivity().getApplicationContext(), R.layout.sermon_list_item, sermons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Sermon sermon = getItem(position);


            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sermon_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView speaker = (TextView) convertView.findViewById(R.id.speaker);
            TextView duration = (TextView) convertView.findViewById(R.id.duration);
            ImageView profile = (ImageView) convertView.findViewById(R.id.profile);


            final ImageView actionBtn = (ImageView) convertView.findViewById(R.id.actionBtn);

            ImageView downloadBtn = (ImageView) convertView.findViewById(R.id.downloadBtn);


            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), VideoViewerActivty.class);
                    intent.putExtra("videoURL", sermon.getDownlaodURL());
                    intent.putExtra("videoTitle", sermon.getTitle());
                    startActivity(intent);
                }
            });

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    downloadSermonFromUrl(sermon.getDownlaodURL(), InputFilter.encodeFileName(sermon.getTitle()) + ".mp3");
                }
            });


            Picasso.with(getActivity()).load(sermon.getImageURL().replaceAll("//", "http://")).into(profile);


            title.setText(sermon.getTitle());
            String speakernchurch = sermon.getSpeaker()+" - "+sermon.getChurch();
            speaker.setText(speakernchurch);

            duration.setText(sermon.getDuration());

            return convertView;
        }
    }

    private void downloadSermonFromUrl(String sermonURL, String fileName){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sermonURL));
            request.setDescription("Downloading Sermon...");
            request.setTitle(fileName);

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/Sermons/";
            File resumeFile = new File(filePath);

            if(resumeFile.mkdir()){
                Log.d("Crash", "Made new Sermons folder...");
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
            Log.d("Crash", "Could not download sermon, update os.");
        }
    }


}
