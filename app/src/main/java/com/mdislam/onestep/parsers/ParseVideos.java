package com.mdislam.onestep.parsers;

import com.mdislam.onestep.data.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 12/26/15.
 */
public class ParseVideos {

    private String rawData;
    private ArrayList<Video> videos;


    public ParseVideos(String rawData){
        this.rawData = rawData;
        videos = new ArrayList<Video>();
    }

    public ArrayList<Video> getVideos(){
        return videos;
    }


    public boolean process(){

        boolean status;

        try{
            JSONObject rootObject = new JSONObject(rawData);
            JSONArray rootArray = rootObject.getJSONArray("items");

            for(int i=0; i<rootArray.length(); i++){

                Video video = new Video();

                JSONObject jsonObject = rootArray.getJSONObject(i);

                video.setVideoURL(jsonObject.getJSONObject("id").getString("videoId"));

                JSONObject snippetObject = jsonObject.getJSONObject("snippet");

                video.setMeta(snippetObject.getString("publishedAt"));
                video.setTitle(snippetObject.getString("title"));
                video.setDescription(snippetObject.getString("description"));

                JSONObject thumbnailObject = snippetObject.getJSONObject("thumbnails");
                JSONObject defaultThumbObject = thumbnailObject.getJSONObject("high");

                video.setThumbnail(defaultThumbObject.getString("url"));

                videos.add(video);
            }

            status = true;

        } catch (JSONException e){
            status = false;
        }


        return status;
    }





}
