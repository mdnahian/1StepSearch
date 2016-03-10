package com.mdislam.onestep.parsers;

import com.mdislam.onestep.data.Resume;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mdislam on 1/4/16.
 */
public class ParseResumes {

    private String rawData;
    private ArrayList<Resume> resumes;

    public ParseResumes(String rawData) {
        this.rawData = rawData;
        resumes = new ArrayList<Resume>();
    }

    public ArrayList<Resume> getResumes() {
        return resumes;
    }

    public boolean process(){

        boolean status;

        try{
            JSONArray jsonArray = new JSONArray(rawData);

            for(int i=0; i<jsonArray.length(); i++){

                Resume resume = new Resume();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                resume.setName(jsonObject.getString("name"));
                resume.setLink(jsonObject.getString("link"));
                resume.setLocation(jsonObject.getString("location"));
                resume.setUpdate(jsonObject.getString("update"));

                JSONArray experiencesArray = jsonObject.getJSONArray("experiences");
                String experiences = "";

                for (int j=0; j<experiencesArray.length(); j++) {
                    experiences += experiencesArray.getString(j) + "\n";
                }

                resume.setExperiences(experiences);
                resumes.add(resume);
            }

            status = true;

        } catch (JSONException e){
            status = false;
        }

        return status;
    }


}
