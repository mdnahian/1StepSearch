package com.mdislam.onestep.searchfragments;

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

import com.mdislam.onestep.R;
import com.mdislam.onestep.activities.ParentActivity;
import com.mdislam.onestep.activities.ViewMediaActivity;
import com.mdislam.onestep.data.Image;
import com.mdislam.onestep.fragments.SearchFragment;
import com.mdislam.onestep.parsers.ParseImages;

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

    private GridView results2;
    private ProgressDialog progressDialog;
    private ArrayList<Image> images;

    private static final String IMAGES_URL = "https://api.flickr.com/services/rest/?format=json&method=flickr.photos.search";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.image_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        results2 = (GridView) rootView.findViewById(R.id.results2);

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
                parentActivity.notConnectedDialog();
            } else {
                parseImagesJSON(s);
            }
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
        results2.setAdapter(imageAdapter);

        progressDialog.dismiss();
    }



    private class ImageAdapter extends ArrayAdapter<Image> {

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

                                    downloadImage(imageCopy.getImageURL(), SearchFragment.generateRandomString()+"."+extension);
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


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView thumbnail;
        Image image;
        int imageNum;

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
            if(result == null){
                parentActivity.notConnectedDialog();
            } else {
                thumbnail.setImageBitmap(result);
                image.setImageBit(result);
            }
        }

    }


}