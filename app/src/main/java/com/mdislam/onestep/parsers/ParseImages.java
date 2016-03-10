package com.mdislam.onestep.parsers;

import com.mdislam.onestep.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 12/26/15.
 */
public class ParseImages {

    private String rawData;
    private ArrayList<Image> images;


    public ParseImages(String rawData){
        this.rawData = rawData;
        images = new ArrayList<Image>();
    }

    public ArrayList<Image> getImages(){
        return images;
    }


    public boolean process(){

        boolean status;

        rawData = rawData.replace("jsonFlickrApi(", "");
        rawData = rawData.substring(0, rawData.length()-1);

        try{

            JSONObject rootObject = new JSONObject(rawData);
            JSONObject rootObject1 = rootObject.getJSONObject("photos");
            JSONArray rootArray = rootObject1.getJSONArray("photo");

            for(int i=0; i<rootArray.length(); i++){

                Image image = new Image();

                JSONObject jsonObject = rootArray.getJSONObject(i);

                image.setImageURL("http://farm" + jsonObject.getString("farm") + ".static.flickr.com/" + jsonObject.getString("server") + "/" + jsonObject.get("id") + "_" + jsonObject.getString("secret") + ".jpg");

                images.add(image);
            }

            status = true;

        } catch (JSONException e){
            status = false;
        }


        return status;
    }





}
