package com.onestepsearch.onestepsearch.core;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 4/14/16.
 */
public class ParseResponse {

    private String json;
    private boolean isError;
    private ArrayList<ArrayList> objects;

    public ParseResponse(String json) {
        this.json = json;
        isError = true;
        objects = new ArrayList<>();
    }


    public boolean isError() {
        return isError;
    }

    public ArrayList getObjects() {
        return objects;
    }


    public void execute() {
        try {
            JSONArray jsonArray = new JSONArray(json);

            for(int i=0; i<jsonArray.length(); i++){

                JSONArray innerArray = jsonArray.getJSONArray(i);

                ArrayList<String> object = new ArrayList<>();

                for(int j=0; j<innerArray.length(); j++){
                    object.add(innerArray.get(j).toString());
                }

                objects.add(object);
            }

            isError = false;

        }  catch (JSONException e){

            Log.d("Crash", e.getMessage());

            try {
                JSONObject jsonObject = new JSONObject(json);
                ArrayList<String> object = new ArrayList<>();

                for(int j=0; j<jsonObject.length(); j++){
                    object.add(jsonObject.getString("success"));
                    object.add(jsonObject.getString("id"));
                }

                objects.add(object);

                isError = false;
            } catch (JSONException a){
                isError = true;

                Log.d("Crash", a.getMessage());
            }
        }
    }

}

