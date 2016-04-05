package com.onestepsearch.onestepsearch.parsers;


import com.onestepsearch.onestepsearch.data.Sermon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 2/26/16.
 */
public class ParseSermons {

    private String rawData;
    private ArrayList<Sermon> sermons;


    public ParseSermons(String rawData){
        this.rawData = rawData;
        sermons = new ArrayList<Sermon>();
    }

    public ArrayList<Sermon> getSermons() {
        return sermons;
    }

    public boolean process(){

        boolean status;

        try{
            JSONArray jsonArray = new JSONArray(rawData);

            for(int i=0; i<jsonArray.length(); i++){

                Sermon sermon = new Sermon();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                sermon.setTitle(jsonObject.getString("link_title"));
                sermon.setLink(jsonObject.getString("link_location"));
                sermon.setSpeaker(jsonObject.getString("speaker"));
                sermon.setImageURL(jsonObject.getString("image"));
                sermon.setDownlaodURL(jsonObject.getString("download_location"));
                sermon.setChurch(jsonObject.getString("church"));
                sermon.setDuration(jsonObject.getString("duration"));

                sermons.add(sermon);
            }

            status = true;

        } catch (JSONException e){
            status = false;
        }

        return status;


    }


}
