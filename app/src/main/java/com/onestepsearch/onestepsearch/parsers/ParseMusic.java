package com.onestepsearch.onestepsearch.parsers;

import android.util.Log;

import com.onestepsearch.onestepsearch.data.Music;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mdislam on 2/2/16.
 */
public class ParseMusic {

    private String rawData;
    private ArrayList<Music> music;

    public ParseMusic(String rawData){
        this.rawData = rawData;
        music = new ArrayList<Music>();
    }

    public ArrayList<Music> getMusic() {
        return music;
    }

    public boolean process(){
        boolean status;

        try{
            JSONArray jsonArray = new JSONArray(rawData);

            for(int i=0; i<jsonArray.length(); i++){

                Music song = new Music();

                JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                song.setTitle(jsonArray1.get(0).toString());
                song.setPage(jsonArray1.get(1).toString());
                song.setArtist(jsonArray1.get(2).toString());
                song.setAlbum(jsonArray1.get(3).toString());

                music.add(song);
            }

            status = true;

        } catch (JSONException e){
            status = false;
            Log.d("Crash", e.toString());
        }

        return status;
    }

}
