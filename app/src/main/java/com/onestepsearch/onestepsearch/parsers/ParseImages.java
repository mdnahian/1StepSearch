package com.onestepsearch.onestepsearch.parsers;

import android.util.Log;

import com.onestepsearch.onestepsearch.data.Image;

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

        try{

            JSONObject rootObject = new JSONObject(rawData);
            JSONArray rootArray = rootObject.getJSONArray("value");

            for(int i=0; i<rootArray.length(); i++){

                Image image = new Image();

                JSONObject jsonObject = rootArray.getJSONObject(i);
                image.setImageURL(jsonObject.getString("contentUrl"));

                images.add(image);
            }

        } catch (JSONException e){
            Log.d("Crash", e.getMessage());
            return false;
        }


        return true;
    }





}
