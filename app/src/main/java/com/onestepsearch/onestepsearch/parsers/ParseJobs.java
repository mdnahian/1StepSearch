package com.onestepsearch.onestepsearch.parsers;


import com.onestepsearch.onestepsearch.data.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 1/12/16.
 */
public class ParseJobs {

    private String rawData;
    private ArrayList<Job> jobs;

    public ParseJobs(String rawData){
        this.rawData = rawData;
        jobs = new ArrayList<Job>();
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public boolean process(){

        boolean status;

        try{
            JSONArray jsonArray = new JSONArray(rawData);

            for(int i=0; i<jsonArray.length(); i++){

                Job job = new Job();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                job.setTitle(jsonObject.getString("title"));
                job.setLink(jsonObject.getString("link"));
                job.setCompany(jsonObject.getString("company"));
                job.setLocation(jsonObject.getString("location"));
                job.setDescription(jsonObject.getString("description"));
                job.setDate(jsonObject.getString("date"));

                jobs.add(job);
            }

            status = true;

        } catch (JSONException e){
            status = false;
        }

        return status;
    }

}
