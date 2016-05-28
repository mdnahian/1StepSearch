package com.onestepsearch.onestepsearch.fragments.searchfragments;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.activities.ViewMediaActivity;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.onestepsearch.onestepsearch.core.SquaredImageView;
import com.onestepsearch.onestepsearch.data.Image;
import com.onestepsearch.onestepsearch.parsers.ParseImages;
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
public class ImageFragment extends Fragment {

    private ParentActivity parentActivity;

    private GridView results;
    private ProgressDialog progressDialog;
    private ArrayList<Image> images;

    private static final String IMAGES_URL = "https://bingapis.azure-api.net/api/v5/images/search";

    private SavedSession savedSession;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.image_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        savedSession = (SavedSession) getActivity().getIntent().getSerializableExtra("SavedSession");

        results = (GridView) rootView.findViewById(R.id.gridView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DownloadImagesString downloadImagesString = new DownloadImagesString();
        downloadImagesString.execute(getArguments().getSerializable("query").toString());

        return rootView;
    }


    private class DownloadImagesString extends AsyncTask<String, Void, String> {

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
            if(s == null){
                Log.d("Crash", "s is null");
            } else {
//                Log.d("Crash", s);
                parseImagesJSON(s);
            }
        }

        private String downloadImagesStringFromURL(String query){

            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(IMAGES_URL+"?q="+ Uri.encode(query, "UTF-8")+"&mkt=en-us"+"&key="+getString(R.string.bing_search_key_2));
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

        if(images.size() > 0){
            ((ParentActivity) getActivity()).addSearchQuery("\""+getArguments().getSerializable("query").toString()+"\""+" in Images",savedSession);

            ImageAdapter imageAdapter = new ImageAdapter();
            results.setAdapter(imageAdapter);

            progressDialog.dismiss();

        } else {
            parentActivity.buildDialog("No Results Found", "Please make sure you spelled all words correctly and try again.");
            progressDialog.dismiss();

        }



    }



    private class ImageAdapter extends ArrayAdapter<Image>{

        public ImageAdapter() {
            super(getActivity().getApplicationContext(), R.layout.images_list_item, images);
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            SquaredImageView view = (SquaredImageView) convertView;

            if (view == null) {
                view = new SquaredImageView(getActivity().getApplicationContext());
            }

            final String url = getItem(position).getImageURL();
            Picasso.with(getActivity()).load(url).resize(100, 100).centerCrop().into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    (new AlertDialog.Builder(getActivity())
                                .setTitle("What do you want to do with this image?")
                                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getActivity().getApplicationContext(), ViewMediaActivity.class);
                                        intent.putExtra("uri", url);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.setMessage("Loading...");
                                        progressDialog.show();

                                        String filenameArray[] = url.split("\\.");
                                        String extension = filenameArray[filenameArray.length - 1];

                                        downloadImage(url, InputFilter.generateRandomString() + "." + extension);
                                    }
                                })).show();
                }
            });

            return view;
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


}
