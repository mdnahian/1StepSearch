package com.mdislam.onestep.searchfragments;

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
import android.os.PowerManager;
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
import com.mdislam.onestep.activities.VideoViewerActivty;
import com.mdislam.onestep.data.Music;
import com.mdislam.onestep.data.Video;
import com.mdislam.onestep.fragments.SearchFragment;
import com.mdislam.onestep.parsers.ParseVideos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private ArrayList<Video> videos;
    private ArrayList<Music> music;

    private static final String YOUTUBE_VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos?&part=contentDetails&key=AIzaSyBumdwEY7qaO12L0I2EAHVoFSR_XS9UHjI";
    private static final String YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=50&key=AIzaSyBumdwEY7qaO12L0I2EAHVoFSR_XS9UHjI";
    private static final String YOUTUBE_REQUEST_URL = "youtube.php?c=mp3";


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

        DownloadYoutubeString downloadString = new DownloadYoutubeString();
        downloadString.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }




    private class DownloadYoutubeString extends AsyncTask<String, Void, String> {

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

            if(s == null){
                parentActivity.notConnectedDialog();
            } else {
                parseYoutubeJSON(s);
            }
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
        music = new ArrayList<>();

        for(Video video : videos){
            DownloadVideoData downloadVideoData = new DownloadVideoData(video);
            downloadVideoData.execute(video.getVideoURL());

            if(video.getVideoURL().equals(videos.get(videos.size()-1).getVideoURL())){
                progressDialog.dismiss();
                convertVideoToMusic();
                MusicAdapter musicAdapter = new MusicAdapter();
                results.setAdapter(musicAdapter);
            }
        }
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

            final ImageView downloadBtn = (ImageView) convertView.findViewById(R.id.downloadBtn);


            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if(SearchFragment.isPlaying){
//                        actionBtn.setImageResource(R.drawable.play);
//                        SearchFragment.isPlaying = false;
//                    } else{
//                        actionBtn.setImageResource(R.drawable.pause);
//                        SearchFragment.isPlaying = true;
//                    }

                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    DowloadYouTubeLinkForViewing dowloadYouTubeLinkForViewing = new DowloadYouTubeLinkForViewing(song.getTitle());
                    dowloadYouTubeLinkForViewing.execute(getString(R.string.baseURL)+YOUTUBE_REQUEST_URL, "https://www.youtube.com/watch?v=" + song.getMusicURL());

                }
            });


            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    DownloadYouTubeLink downloadYouTubeLink = new DownloadYouTubeLink(downloadBtn, song, SearchFragment.encodeFileName(song.getTitle()) + ".mp3");
                    downloadYouTubeLink.execute(getString(R.string.baseURL) + YOUTUBE_REQUEST_URL, "https://www.youtube.com/watch?v=" + song.getMusicURL());
                    song.setIsDownloading(true);
//                    DownloadMusicURL downloadMusicURL = new DownloadMusicURL(false, SearchFragment.encodeFileName(song.getTitle())+".mp3");
//                    downloadMusicURL.execute(song.getDownloadURL());
                }
            });


            title.setText(song.getTitle());
            artist.setText(song.getArtist());
            album.setText(song.getAlbum());

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
            return fileContents;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null){
                parentActivity.notConnectedDialog();
            } else {

                try {
                    JSONObject rootObject = new JSONObject(s);
                    JSONArray rootArray = rootObject.getJSONArray("items");
                    JSONObject itemsObject = rootArray.getJSONObject(0);
                    JSONObject contentObject = itemsObject.getJSONObject("contentDetails");
                    String rawDuration = contentObject.getString("duration");

                    String duration = rawDuration.replace("M", ":").replace("PT", "").replace("S", "").replace("H", ":");

                    if (duration.substring(duration.length() - 1).equals(":")) {
                        duration += "00";
                    }

                    if (duration.length() > 2) {
                        if (duration.substring(duration.length() - 2).substring(0, 1).equals(":")) {
                            String sec = ":0" + duration.substring(duration.length() - 1);
                            duration = duration.substring(0, duration.length() - 2) + sec;
                        }
                    } else {
                        duration = "0:" + duration;
                    }

                    if (duration.length() >= 5) {
                        if (duration.substring(duration.length() - 5).substring(0, 1).equals(":")) {
                            String sec = ":0" + duration.substring(duration.length() - 4);
                            duration = duration.substring(0, duration.length() - 5) + sec;
                        }
                    }

                    String newMeta = "Duration " + duration;

                    video.setMeta(newMeta);

                } catch (JSONException e) {
                    Log.d("Crash", "Could not load meta data.");
                }
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
        private String fileName;
        private Music music;

        public DownloadYouTubeLink(ImageView downloadBtn, Music music, String fileName) {
            this.fileName = fileName;
            this.downloadBtn = downloadBtn;
            this.music = music;
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

            if(s == null){
                parentActivity.notConnectedDialog();
            } else {
                if (s.equals("error")) {
                    Log.d("Crash", s);
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                        request.setDescription("Downloading Song...");
                        request.setTitle(fileName);

                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OneStepSearch/Music/";
                        File youtubeFile = new File(filePath);

                        if (youtubeFile.mkdir()) {
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

                                while (downloading) {

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
                                                music.setIsDownloading(false);
                                                downloadBtn.setVisibility(View.VISIBLE);
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity().getApplicationContext(), "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    cursor.close();
                                }

                            }
                        }).start();


                    } else {

                        DownloadTask downloadTask = new DownloadTask(getActivity().getApplicationContext(), downloadBtn, fileName);
                        downloadTask.execute(s);
                    }

                }
            }

        }


        private String downloadYouTubeURL(String requestURL, String youtubeURL){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(requestURL+"&URL="+ Uri.encode(youtubeURL, "UTF-8"));
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

            if(s == null){
                parentActivity.notConnectedDialog();
            } else {
                Log.d("Crash", s);
                Intent intent = new Intent(getActivity().getApplicationContext(), VideoViewerActivty.class);
                intent.putExtra("videoURL", s);
                intent.putExtra("videoTitle", videoTitle);
                startActivity(intent);
            }
        }


        private String downloadYouTubeURL(String requestURL, String youtubeURL){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(requestURL+"&URL="+ Uri.encode(youtubeURL, "UTF-8"));
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
        private ImageView downloadBtn;

        public DownloadTask(Context context, ImageView downloadBtn, String fileName) {
            this.context = context;
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

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/Music/";
                File youtubeFile = new File(filePath);

                if(youtubeFile.mkdir()){
                    Log.d("Crash", "Made new Music folder...");
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


        @Override
        protected void onPostExecute(String result) {
            wakeLock.release();

            if (result != null) {
                Log.d("Crash", "Could not download file: " + result);

                downloadBtn.setVisibility(View.VISIBLE);

                Toast.makeText(context, "Failed to Download File", Toast.LENGTH_LONG).show();
            }
            else {
                Log.d("Crash", "File Downloaded Successfully");
                Toast.makeText(context, "File Downloaded Successfully", Toast.LENGTH_SHORT).show();
            }

        }


    }




    private void convertVideoToMusic(){
        for(Video video : videos) {
            Music song = new Music();
            song.setTitle(video.getTitle());
            song.setAlbum(video.getMeta());
            song.setArtist(video.getDescription());
            song.setMusicURL(video.getVideoURL());

            music.add(song);
        }
    }

}