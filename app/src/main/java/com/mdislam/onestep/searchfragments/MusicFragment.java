package com.mdislam.onestep.searchfragments;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.mdislam.onestep.R;
import com.mdislam.onestep.activities.ParentActivity;
import com.mdislam.onestep.data.Music;
import com.mdislam.onestep.fragments.SearchFragment;
import com.mdislam.onestep.parsers.ParseMusic;

import java.io.BufferedWriter;
import java.io.File;
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
public class MusicFragment extends Fragment {

    private ParentActivity parentActivity;

    private ListView results;
    private ProgressDialog progressDialog;
    private ArrayList<Music> music;
    private static final String MUSIC_REQUEST_URL = "music.php";


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

        DownloadMusicJSON downloadMusicString = new DownloadMusicJSON();
        downloadMusicString.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }




    private class DownloadMusicJSON extends AsyncTask<String, Void, String> {

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


    private class MusicAdapter extends ArrayAdapter<Music> {

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
                    if(SearchFragment.isPlaying){
                        actionBtn.setImageResource(R.drawable.play);
                        SearchFragment.isPlaying = false;
                    } else{
                        actionBtn.setImageResource(R.drawable.pause);
                        SearchFragment.isPlaying = true;
                    }
                }
            });


            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    DownloadMusicURL downloadMusicURL = new DownloadMusicURL(false, SearchFragment.encodeFileName(song.getTitle())+".mp3");
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



}
