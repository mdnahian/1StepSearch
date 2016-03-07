package com.mdislam.onestep;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mdislam.onestep.CORE.CraigslistAd;
import com.mdislam.onestep.CORE.Image;
import com.mdislam.onestep.CORE.InputFilter;
import com.mdislam.onestep.CORE.Job;
import com.mdislam.onestep.CORE.Music;
import com.mdislam.onestep.CORE.ParseCraigslist;
import com.mdislam.onestep.CORE.ParseImages;
import com.mdislam.onestep.CORE.ParseJobs;
import com.mdislam.onestep.CORE.ParseMusic;
import com.mdislam.onestep.CORE.ParseResumes;
import com.mdislam.onestep.CORE.ParseSermons;
import com.mdislam.onestep.CORE.ParseVideos;
import com.mdislam.onestep.CORE.Resume;
import com.mdislam.onestep.CORE.Sermon;
import com.mdislam.onestep.CORE.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mdislam on 12/26/15.
 */
public class SearchFragment extends Fragment {


    private ImageView youtubeBtn;
    private ImageView imageBtn;
    private ImageView musicBtn;
    private ImageView resumeBtn;

    private ImageView moreBtn;

    private ImageView jobsBtn;
    private ImageView sermonBtn;
    private ImageView craigslistBtn;

    private ImageView mic;
    protected static final int RESULT_SPEECH = 1;

    private EditText searchBar;

    private MediaPlayer mediaPlayer;
    private int playbackPosition;

    private int tab;

    private ListView results;
    private GridView results2;

    private ArrayList<Video> videos;
    private ArrayList<Image> images;
    private ArrayList<Resume> resumes;
    private ArrayList<Job> jobs;
    private ArrayList<CraigslistAd> craigslistAds;
    private ArrayList<Music> music;
    private ArrayList<Sermon> sermons;

    private boolean isPlaying;

    private static final String SERMON_REQUEST_URL = "sermons.php?q=";

    private static final String CRAIGSLIST_REQUEST_URL = "http://newyork.craigslist.org/search/sss?sort=rel&query=";
    private static final String CRAIGSLIST_PARSE_URL = "craigslist.php";

    private static final String JOB_REQUEST_URL = "jobs.php";

    private static final String RESUME_REQUEST_URL = "resumes.php";

    private static final String MUSIC_REQUEST_URL = "music.php";

    private static final String IMAGES_URL = "https://api.flickr.com/services/rest/?format=json&method=flickr.photos.search";

    private static final String YOUTUBE_VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos?&part=contentDetails&key=AIzaSyBumdwEY7qaO12L0I2EAHVoFSR_XS9UHjI";
    private static final String YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=50&key=AIzaSyBumdwEY7qaO12L0I2EAHVoFSR_XS9UHjI";
    private static final String YOUTUBE_REQUEST_URL = "youtube.php";

    private ProgressDialog progressDialog;

    private int tabSet = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        tab = 1;

        searchBar = (EditText) rootView.findViewById(R.id.searchBar);

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideMic();
                }else {
                    showMic();
                }
            }
        });


        mic = (ImageView) rootView.findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    searchBar.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getActivity().getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });



        youtubeBtn = (ImageView) rootView.findViewById(R.id.youtubeBtn);
        youtubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 1;
                youtubeTab();
            }
        });


        imageBtn = (ImageView) rootView.findViewById(R.id.imageBtn);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 2;
                imageTab();
            }
        });


        musicBtn = (ImageView) rootView.findViewById(R.id.musicBtn);
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 3;
                musicTab();
            }
        });


        resumeBtn = (ImageView) rootView.findViewById(R.id.resumeBtn);
        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 4;
                resumeTab();
            }
        });


        moreBtn = (ImageView) rootView.findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreTab();
            }
        });


        jobsBtn = (ImageView) rootView.findViewById(R.id.jobBtn);
        jobsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 5;
                jobsTab();
            }
        });


        craigslistBtn = (ImageView) rootView.findViewById(R.id.craigslistBtn);
        craigslistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 6;
                craigslistTab();
            }
        });


        sermonBtn = (ImageView) rootView.findViewById(R.id.sermonBtn);
        sermonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 7;
                sermonTab();
            }
        });


        ImageView searchBtn = (ImageView) rootView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideMic();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                if (InputFilter.checkNumCharacters(searchBar.getText().toString(), 0)) {
                    ((ParentActivity) getActivity()).addSearchQuery(searchBar.getText().toString());
                    switch (tab) {
                        case 1:
                            DownloadYoutubeString downloadString = new DownloadYoutubeString();
                            downloadString.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            break;
                        case 2:
                            DownloadImagesString downloadImagesString = new DownloadImagesString();
                            downloadImagesString.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            break;
                        case 3:
                            DownloadMusicJSON downloadMusicString = new DownloadMusicJSON();
                            downloadMusicString.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            break;
                        case 4:
                            DownloadResumeString downloadResumeString = new DownloadResumeString();
                            downloadResumeString.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            break;
                        case 5:
                            DownloadJobsString downloadJobsString = new DownloadJobsString();
                            downloadJobsString.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            break;
                        case 6:
                            DownloadCraigslistString downloadCraigslistString = new DownloadCraigslistString();
                            downloadCraigslistString.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            break;
                        case 7:
                            DownloadSermonJSON downloadSermonJSON = new DownloadSermonJSON();
                            downloadSermonJSON.execute(searchBar.getText().toString());

                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                    }
                }
            }
        });


        results = (ListView) rootView.findViewById(R.id.results);
        results2 = (GridView) rootView.findViewById(R.id.results2);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/";
        File rootDirectory = new File(filePath);

        if(rootDirectory.mkdir()){
            Log.d("Crash", "Made new OneStepSearch folder...");
        }


        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    searchBar.setText(text.get(0));
                }
                break;
            }

        }
    }


    private void showMic(){
        mic.setVisibility(View.VISIBLE);
        results.setVisibility(View.GONE);
        results2.setVisibility(View.GONE);

    }

    private void hideMic(){
        mic.setVisibility(View.GONE);
        results.setVisibility(View.VISIBLE);
        results2.setVisibility(View.VISIBLE);
    }


    private void youtubeTab(){

        results.setAdapter(null);
        results2.setAdapter(null);

        results2.setVisibility(View.GONE);

        results.setVisibility(View.VISIBLE);

        youtubeBtn.setBackgroundColor(Color.parseColor("#63090A"));
        imageBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        musicBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        resumeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search YouTube...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void imageTab(){

        results.setAdapter(null);
        results2.setAdapter(null);

        results.setVisibility(View.GONE);

        results2.setVisibility(View.VISIBLE);

        youtubeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        imageBtn.setBackgroundColor(Color.parseColor("#63090A"));
        musicBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        resumeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Images...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void musicTab(){

        results.setAdapter(null);
        results2.setAdapter(null);

        youtubeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        imageBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        musicBtn.setBackgroundColor(Color.parseColor("#63090A"));
        resumeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Music...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void resumeTab(){

        results.setAdapter(null);
        results2.setAdapter(null);

        results2.setVisibility(View.GONE);

        results.setVisibility(View.VISIBLE);

        youtubeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        imageBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        musicBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        resumeBtn.setBackgroundColor(Color.parseColor("#63090A"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Résumé...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void moreTab(){
        switch(tabSet){
            case 1:
                youtubeBtn.setVisibility(View.VISIBLE);
                imageBtn.setVisibility(View.VISIBLE);
                musicBtn.setVisibility(View.VISIBLE);
                resumeBtn.setVisibility(View.VISIBLE);

                jobsBtn.setVisibility(View.GONE);
                craigslistBtn.setVisibility(View.GONE);
                sermonBtn.setVisibility(View.GONE);

                tabSet = 2;
                tab = 1;
                youtubeTab();
                break;
            case 2:
                jobsBtn.setVisibility(View.VISIBLE);
                craigslistBtn.setVisibility(View.VISIBLE);
                sermonBtn.setVisibility(View.VISIBLE);

                youtubeBtn.setVisibility(View.GONE);
                imageBtn.setVisibility(View.GONE);
                musicBtn.setVisibility(View.GONE);
                resumeBtn.setVisibility(View.GONE);

                tabSet = 1;
                tab = 5;
                jobsTab();
                break;
        }
    }

    private void jobsTab(){
        results.setAdapter(null);
        results2.setAdapter(null);

        results2.setVisibility(View.GONE);

        results.setVisibility(View.VISIBLE);

        jobsBtn.setBackgroundColor(Color.parseColor("#63090A"));
        craigslistBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        sermonBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Jobs...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void craigslistTab(){
        results.setAdapter(null);
        results2.setAdapter(null);

        results2.setVisibility(View.GONE);

        results.setVisibility(View.VISIBLE);

        jobsBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        craigslistBtn.setBackgroundColor(Color.parseColor("#63090A"));
        sermonBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Craigslist...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void sermonTab(){
        results.setAdapter(null);
        results2.setAdapter(null);

        results2.setVisibility(View.GONE);

        results.setVisibility(View.VISIBLE);

        jobsBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        craigslistBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        sermonBtn.setBackgroundColor(Color.parseColor("#63090A"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Sermons...";
        searchBar.setHint(newHint);

        showMic();
        searchBar.setText("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }




    private class DownloadSermonJSON extends AsyncTask<String, Void, String>{

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
            parseSermonsJSON(s);
        }

        private String downloadSermonJSONFromURL(String query){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+SERMON_REQUEST_URL+Uri.encode(query, "UTF-8"));
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

        SermonsAdapter sermonsAdapter = new SermonsAdapter();
        results.setAdapter(sermonsAdapter);

        progressDialog.dismiss();

    }


    private class SermonsAdapter extends ArrayAdapter<Sermon>{

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
                    if(isPlaying){
                        actionBtn.setImageResource(R.drawable.play);

                        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                            playbackPosition = mediaPlayer.getCurrentPosition();
                            mediaPlayer.pause();
                        }

                        isPlaying = false;
                    } else{
                        actionBtn.setImageResource(R.drawable.pause);

                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        try {
                            playAudio(sermon.getDownlaodURL());
                        } catch(Exception e){
                            Log.d("Crash", "Could not play sermon");
                        }

                        isPlaying = true;
                    }
                }
            });

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    downloadSermonFromUrl(sermon.getDownlaodURL(), encodeFileName(sermon.getTitle()) + ".mp3");
                }
            });


            if(sermon.getProfileBit() == null) {
                new DownloadImageTask(profile, sermon).execute(sermon.getImageURL());
            } else {
                profile.setImageBitmap(sermon.getProfileBit());
            }


            title.setText(sermon.getTitle());
            String speakernchurch = sermon.getSpeaker()+" - "+sermon.getChurch();
            speaker.setText(speakernchurch);

            duration.setText(sermon.getDuration());

            return convertView;
        }
    }



    private void playAudio(String url) throws Exception {

        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();

        progressDialog.dismiss();
    }


    private void killMediaPlayer() {
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
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


    private class DownloadCraigslistString extends AsyncTask<String, Void, String>{

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
            parseCraigslistHtml(s);
        }

        private String downloadCraigslistStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(CRAIGSLIST_REQUEST_URL+Uri.encode(query, "UTF-8"));
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
            parseCraigslistJSON(s);
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

        CraigslistAdapter craigslistAdapter = new CraigslistAdapter();
        results.setAdapter(craigslistAdapter);

        progressDialog.dismiss();
    }


    private class CraigslistAdapter extends ArrayAdapter<CraigslistAd>{

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

                    Intent intent = new Intent(getActivity().getApplicationContext(), ViewInWebActivity.class);
                    intent.putExtra("url", "http://craigslist.com"+craigslistAd.getLink());
                    startActivity(intent);

                    progressDialog.dismiss();
                }
            });

            return convertView;
        }
    }


    private class DownloadJobsString extends AsyncTask<String, Void, String>{

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
            parseJobsJSON(s);
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

        JobsAdapter jobsAdapter = new JobsAdapter();
        results.setAdapter(jobsAdapter);

        progressDialog.dismiss();
    }

    private class JobsAdapter extends ArrayAdapter<Job>{

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
                public void onClick(android.view.View v) {
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
            parseJobsFull(s, job);
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
            Intent intent = new Intent(getActivity().getApplicationContext(), ViewInWebActivity.class);
            intent.putExtra("url", job.getLink());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity().getApplicationContext(), ViewJobActivity.class);
            intent.putExtra("job", jobCopy);
            startActivity(intent);
        }

        progressDialog.dismiss();

    }



    private class ViewResumeFull extends AsyncTask<String, Void, String>{

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
            parseResumeJSON(s);
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

        ResumeAdapter resumeAdapter = new ResumeAdapter();
        results.setAdapter(resumeAdapter);

        progressDialog.dismiss();
    }



    private class ResumeAdapter extends ArrayAdapter<Resume>{

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

                    DownloadResumeFull downloadResumeFull = new DownloadResumeFull(encodeFileName(resume.getName()) + ".pdf");
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
            downloadResumeFromURL(s, name);
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



    private class DownloadImagesString extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadImagesStringFromURL(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseImagesJSON(s);
        }

        private String downloadImagesStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(IMAGES_URL+"&text="+ Uri.encode(query, "UTF-8")+"&api_key="+getString(R.string.flickrAPIKey));
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


    private void parseImagesJSON(String json){
        ParseImages parseImages = new ParseImages(json);
        parseImages.process();

        images = parseImages.getImages();

        ImageAdapter imageAdapter = new ImageAdapter();
        results.setVisibility(View.GONE);
        results2.setVisibility(View.VISIBLE);
        results2.setAdapter(imageAdapter);


        progressDialog.dismiss();
    }



    private class ImageAdapter extends ArrayAdapter<Image>{

        private boolean isFilled = true;
        private int currentPosition = 0;

        public ImageAdapter() {
            super(getActivity().getApplicationContext(), R.layout.images_list_item, images);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.images_list_item, parent, false);
            }


            if(currentPosition == images.size()){
                return convertView;
            }

            Image image = getItem(currentPosition);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image1);

            if(isFilled) {
                image = getItem(currentPosition);
                imageView = (ImageView) convertView.findViewById(R.id.image1);
                currentPosition++;
                isFilled = false;
            } else{
                if(images.size() != (currentPosition)) {
                    image = getItem(currentPosition);
                    imageView = (ImageView) convertView.findViewById(R.id.image2);
                    currentPosition = currentPosition + 2;
                    isFilled = true;
                } else{

                }
            }


            if (image.getImageBit() == null) {
                new DownloadImageTask(imageView, image, 1).execute(image.getImageURL());
            } else {
                image.setImageBit(image.getImageBit());
            }

            final Image imageCopy = image;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ViewMediaActivity.class);
                    intent.putExtra("uri", imageCopy.getImageURL());
                    startActivity(intent);
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    (new AlertDialog.Builder(getActivity())
                            .setTitle("Save Image?")
                            .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage("Loading...");
                                    progressDialog.show();

                                    String filenameArray[] = imageCopy.getImageURL().split("\\.");
                                    String extension = filenameArray[filenameArray.length-1];

                                    downloadImage(imageCopy.getImageURL(), generateRandomString()+"."+extension);
                                }
                            })).show();

                    return false;
                }
            });

            return convertView;
        }
    }


    private void downloadImage(String imageURL, String fileName){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageURL));
            request.setDescription("Downloading Image...");
            request.setTitle(fileName);

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/Images/";
            File resumeFile = new File(filePath);

            if(resumeFile.mkdir()){
                Log.d("Crash", "Made new Images folder...");
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
            Log.d("Crash", "Could not download image, update os.");
            progressDialog.dismiss();
        }
    }


    private class DownloadMusicJSON extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String fileContent = downloadMusicJSONFromUrl(params[0]);

            if(fileContent == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContent;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseMusicJSON(s);
        }

        private String downloadMusicJSONFromUrl(String query){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+MUSIC_REQUEST_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = Uri.encode("q", "UTF-8")+"="+Uri.encode(query, "UTF-8");

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
                Log.d("Crash", "Could not read data.");
            } catch (SecurityException a){
                Log.d("Crash", "Do not have the permissions to connect to URL.");
            }

            return null;
        }

    }


    private void parseMusicJSON(String json){
        ParseMusic parseMusic = new ParseMusic(json);
        parseMusic.process();

        music = parseMusic.getMusic();

        MusicAdapter musicAdapter = new MusicAdapter();
        results.setAdapter(musicAdapter);

        progressDialog.dismiss();
    }


    private class MusicAdapter extends ArrayAdapter<Music>{

        public MusicAdapter() {
            super(getActivity().getApplicationContext(), R.layout.music_list_item, music);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Music song = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.music_list_item, parent, false);
            }


            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView artist = (TextView) convertView.findViewById(R.id.artist);
            TextView album = (TextView) convertView.findViewById(R.id.album);

            final ImageView actionBtn = (ImageView) convertView.findViewById(R.id.actionBtn);

            ImageView downloadBtn = (ImageView) convertView.findViewById(R.id.downloadBtn);


            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isPlaying){
                        actionBtn.setImageResource(R.drawable.play);
                        isPlaying = false;
                    } else{
                        actionBtn.setImageResource(R.drawable.pause);
                        isPlaying = true;
                    }
                }
            });


            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    DownloadMusicURL downloadMusicURL = new DownloadMusicURL(false, encodeFileName(song.getTitle())+".mp3");
                    downloadMusicURL.execute(song.getDownloadURL());
                }
            });


            title.setText(song.getTitle());
            artist.setText(song.getArtist());
            album.setText(song.getAlbum());

            return convertView;
        }
    }


    private class DownloadMusicURL extends AsyncTask<String, Void, String>{

        private boolean viewOnly;
        private String fileName;

        public DownloadMusicURL(boolean viewOnly, String fileName){
            this.viewOnly = viewOnly;
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContent = downloadMusicURLFromUrl(params[0]);

            if(fileContent == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContent;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(viewOnly){

            } else{
                downloadMusicMP3(s, fileName);
            }

            progressDialog.dismiss();
        }

        private String downloadMusicURLFromUrl(String pageURL){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(getString(R.string.baseURL)+MUSIC_REQUEST_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = Uri.encode("u", "UTF-8")+"="+Uri.encode(pageURL, "UTF-8");

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
                Log.d("Crash", "Could not read data.");
            } catch (SecurityException a){
                Log.d("Crash", "Do not have the permissions to connect to URL.");
            }

            return null;
        }

    }


    private void downloadMusicMP3(String downloadURL, String fileName){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.d("Crash", downloadURL);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setDescription("Downloading Song...");
            request.setTitle(fileName);

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/Music/";
            File resumeFile = new File(filePath);

            if(resumeFile.mkdir()){
                Log.d("Crash", "Made new Music folder...");
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
            Log.d("Crash", "Could not download song, update os.");
        }
    }



    private class DownloadYoutubeString extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadStringFromURL(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseYoutubeJSON(s);
        }



        private String downloadStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(YOUTUBE_URL+"&q="+ Uri.encode(query, "UTF-8"));
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


    private void parseYoutubeJSON(String json){
        ParseVideos parseVideos = new ParseVideos(json);
        parseVideos.process();

        videos = parseVideos.getVideos();

        for(Video video : videos){
            DownloadVideoData downloadVideoData = new DownloadVideoData(video);
            downloadVideoData.execute(video.getVideoURL());

            if(video.getVideoURL().equals(videos.get(videos.size()-1).getVideoURL())){
                progressDialog.dismiss();
                VideoAdapter videoAdapter = new VideoAdapter();
                results.setAdapter(videoAdapter);
            }
        }

    }


    private class VideoAdapter extends ArrayAdapter<Video> {

        public VideoAdapter(){
            super(getActivity().getApplicationContext(), R.layout.youtube_list_item, videos);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Video video = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.youtube_list_item, parent, false);
            }

            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView meta = (TextView) convertView.findViewById(R.id.meta);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            ImageView viewBtn = (ImageView) convertView.findViewById(R.id.viewBtn);
            final ImageView downloadBtn = (ImageView) convertView.findViewById(R.id.downloadBtn);
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.downloadProgress);


            if(video.getThumbnailBit() == null) {
                new DownloadImageTask(thumbnail, video).execute(video.getThumbnail());
            } else {
                thumbnail.setImageBitmap(video.getThumbnailBit());
            }


            title.setText(video.getTitle());
            meta.setText(video.getMeta());
            description.setText(video.getDescription());

            if(!video.isDownloading()) {

                progressBar.setIndeterminate(false);
                progressBar.setProgress(0);
                progressBar.setMax(0);
                progressBar.setVisibility(View.GONE);
                downloadBtn.setVisibility(View.VISIBLE);

                downloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressBar.setIndeterminate(true);
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        downloadBtn.setVisibility(ImageView.GONE);

                        DownloadYouTubeLink downloadYouTubeLink = new DownloadYouTubeLink(downloadBtn, progressBar, video, encodeFileName(video.getTitle()) + ".mp4");
                        downloadYouTubeLink.execute(getString(R.string.baseURL)+YOUTUBE_REQUEST_URL, "https://www.youtube.com/watch?v=" + video.getVideoURL());
                        video.setIsDownloading(true);
                    }
                });

            } else {
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(ProgressBar.VISIBLE);
                downloadBtn.setVisibility(ImageView.GONE);
            }




            viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    DowloadYouTubeLinkForViewing dowloadYouTubeLinkForViewing = new DowloadYouTubeLinkForViewing(video.getTitle());
                    dowloadYouTubeLinkForViewing.execute(getString(R.string.baseURL)+YOUTUBE_REQUEST_URL, "https://www.youtube.com/watch?v=" + video.getVideoURL());
                }
            });


            return convertView;
        }

    }




    private class DownloadVideoData extends AsyncTask<String, Void, String>{

        private Video video;

        public DownloadVideoData(Video video) {
            this.video = video;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadYouTudeVideoDetails(params[0]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject rootObject = new JSONObject(s);
                JSONArray rootArray = rootObject.getJSONArray("items");
                JSONObject itemsObject = rootArray.getJSONObject(0);
                JSONObject contentObject = itemsObject.getJSONObject("contentDetails");
                String rawDuration = contentObject.getString("duration");

                String duration = rawDuration.replace("M", ":").replace("PT", "").replace("S", "").replace("H", ":");

                if(duration.substring(duration.length() - 1).equals(":")){
                    duration += "00";
                }

                if(duration.length() > 2) {
                    if (duration.substring(duration.length() - 2).substring(0, 1).equals(":")) {
                        String sec = ":0" + duration.substring(duration.length() - 1);
                        duration = duration.substring(0, duration.length() - 2) + sec;
                    }
                } else {
                    duration = "0:"+duration;
                }

                if(duration.length() >= 5){
                    if(duration.substring(duration.length() - 5).substring(0, 1).equals(":")){
                        String sec = ":0"+duration.substring(duration.length() - 4);
                        duration = duration.substring(0, duration.length() - 5)+sec;
                    }
                }

                String newMeta = "Duration "+duration;

                video.setMeta(newMeta);

            } catch (JSONException e){
                Log.d("Crash", "Could not load meta data.");
            }


        }


        private String downloadYouTudeVideoDetails(String videoId){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(YOUTUBE_VIDEO_URL+"&id="+ Uri.encode(videoId, "UTF-8"));
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

            return "error";
        }


    }



    private class DownloadYouTubeLink extends AsyncTask<String, Void, String>{

        private ImageView downloadBtn;
        private ProgressBar progressBar;
        private String fileName;
        private Video video;

        public DownloadYouTubeLink(ImageView downloadBtn, ProgressBar progressBar, Video video, String fileName) {
            this.progressBar = progressBar;
            this.fileName = fileName;
            this.downloadBtn = downloadBtn;
            this.video = video;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadYouTubeURL(params[0], params[1]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("error")){
                Log.d("Crash", s);
            } else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                    request.setDescription("Downloading Youtube Video...");
                    request.setTitle(fileName);

                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/YouTube/";
                    File youtubeFile = new File(filePath);

                    if(youtubeFile.mkdir()){
                        Log.d("Crash", "Made new YouTube folder...");
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

                                final int progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            video.setIsDownloading(false);
                                            progressBar.setIndeterminate(false);
                                            progressBar.setProgress(0);
                                            progressBar.setMax(0);
                                            progressBar.setVisibility(View.GONE);
                                            downloadBtn.setVisibility(View.VISIBLE);

                                            Toast.makeText(getActivity().getApplicationContext(), "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setIndeterminate(false);
                                        progressBar.setMax(100);
                                        progressBar.setProgress(progress);
                                    }
                                }).start();

                                cursor.close();
                            }

                        }
                    }).start();


                } else{

                    DownloadTask downloadTask = new DownloadTask(getActivity().getApplicationContext(), downloadBtn, progressBar, fileName);
                    downloadTask.execute(s);
                }

            }

        }


        private String downloadYouTubeURL(String requestURL, String youtubeURL){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(requestURL+"?URL="+ Uri.encode(youtubeURL, "UTF-8"));
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

            return "error";
        }
    }


    private class DowloadYouTubeLinkForViewing extends AsyncTask<String, Void, String>{

        private String videoTitle;

        public DowloadYouTubeLinkForViewing(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        @Override
        protected String doInBackground(String... params) {
            String fileContents = downloadYouTubeURL(params[0], params[1]);

            if(fileContents == null){
                Log.d("Crash", "Could not download string.");
            }

            return fileContents;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            Intent intent = new Intent(getActivity().getApplicationContext(), VideoViewerActivty.class);
            intent.putExtra("videoURL", s);
            intent.putExtra("videoTitle", videoTitle);
            startActivity(intent);
        }


        private String downloadYouTubeURL(String requestURL, String youtubeURL){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(requestURL+"?URL="+ Uri.encode(youtubeURL, "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                Log.d("Crash", url.toString());

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

            return "error";
        }

    }


    private class DownloadTask extends AsyncTask<String, Void, String>{

        private String fileName;

        private Context context;
        private PowerManager.WakeLock wakeLock;
        private ProgressBar progressBar;
        private ImageView downloadBtn;

        public DownloadTask(Context context, ImageView downloadBtn, ProgressBar progressBar, String fileName) {
            this.context = context;
            this.progressBar = progressBar;
            this.fileName = fileName;
            this.downloadBtn = downloadBtn;
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                Log.d("Crash", Integer.toString(fileLength)+" bytes");

                // download the file
                input = connection.getInputStream();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/YouTube/";
                File youtubeFile = new File(filePath);

                if(youtubeFile.mkdir()){
                    Log.d("Crash", "Made new YouTube folder...");
                }

                output = new FileOutputStream(filePath+fileName);

                Log.d("Crash", output.toString());

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        onProgressUpdate((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            wakeLock.acquire();
        }


        protected void onProgressUpdate(int... progress) {
            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            wakeLock.release();

            if (result != null) {
                Log.d("Crash", "Could not download file: " + result);
                progressBar.setIndeterminate(false);
                progressBar.setProgress(0);
                progressBar.setMax(0);
                progressBar.setVisibility(View.GONE);

                downloadBtn.setVisibility(View.VISIBLE);

                Toast.makeText(context, "Failed to Download File", Toast.LENGTH_LONG).show();
            }
            else {
                Log.d("Crash", "File Downloaded Successfully");
                Toast.makeText(context, "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
            }

        }


    }




    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView thumbnail;
        Video video;
        Sermon sermon;

        Image image;
        int imageNum;


        public DownloadImageTask(ImageView thumbnail, Sermon sermon){
            this.thumbnail = thumbnail;
            this.sermon = sermon;
        }


        public DownloadImageTask(ImageView thumbnail, Video video) {
            this.thumbnail = thumbnail;
            this.video = video;
        }

        public DownloadImageTask(ImageView thumbnail, Image image, int imageNum) {
            this.thumbnail = thumbnail;
            this.image = image;
            this.imageNum = imageNum;
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            String urldisplay = params[0];

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.d("Crash", "Could not download image");
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            thumbnail.setImageBitmap(result);

            if(video != null) {
                video.setThumbnailBit(result);
            } else if(sermon != null){
                sermon.setProfileBit(result);
            } else{
                image.setImageBit(result);
            }
        }

    }



    private static String generateRandomString(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }



    private static String encodeFileName(String rawFileName){

        String newFileName = rawFileName.replaceAll("\\s", "-");
        newFileName = newFileName.replace("\"", "");
        newFileName = newFileName.replace("\'", "");
        newFileName = newFileName.replaceAll("[^a-zA-Z0-9]+","");

        return newFileName;
    }

}
