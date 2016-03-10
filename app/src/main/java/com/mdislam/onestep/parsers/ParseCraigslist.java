package com.mdislam.onestep.parsers;

import com.mdislam.onestep.data.CraigslistAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 1/26/16.
 */
public class ParseCraigslist {

    private String rawData;
    private ArrayList<CraigslistAd> craigslistAds;

    public ParseCraigslist(String rawData){
        this.rawData = rawData;
        craigslistAds = new ArrayList<CraigslistAd>();
    }

    public ArrayList<CraigslistAd> getCraigslistAds() {
        return craigslistAds;
    }

    public boolean process(){

        boolean status;

        try{
            JSONArray jsonArray = new JSONArray(rawData);

            for(int i=0; i<jsonArray.length(); i++){

                CraigslistAd craigslistAd = new CraigslistAd();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                craigslistAd.setTitle(jsonObject.getString("title"));
                craigslistAd.setLink(jsonObject.getString("link"));
                craigslistAd.setPrice(jsonObject.getString("price"));
                craigslistAd.setLocation(jsonObject.getString("location"));
                craigslistAd.setTime(jsonObject.getString("time"));

                craigslistAds.add(craigslistAd);
            }

            status = true;

        } catch (JSONException e){
            status = false;
        }

        return status;

    }

}
