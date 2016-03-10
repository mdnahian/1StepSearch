package com.mdislam.onestep.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.mdislam.onestep.R;
import com.mdislam.onestep.data.Resume;

/**
 * Created by mdislam on 1/7/16.
 */
public class ViewResumeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_resume_activity);

        Intent intent = getIntent();
        Resume resume = (Resume) intent.getExtras().getSerializable("resume");


        TextView name = (TextView) findViewById(R.id.name);
        TextView location = (TextView) findViewById(R.id.location);
        TextView updated = (TextView) findViewById(R.id.updated);
        TextView summary = (TextView) findViewById(R.id.summary);
        TextView experiences = (TextView) findViewById(R.id.experiences);
        TextView education = (TextView) findViewById(R.id.education);
        TextView skills = (TextView) findViewById(R.id.skills);
        TextView other = (TextView) findViewById(R.id.other);

        if(resume == null){
            finish();
        } else{
            name.setText(resume.getName());
            location.setText(resume.getLocation());
            updated.setText(resume.getUpdate());
            summary.setText(Html.fromHtml(resume.getSummary()));
            experiences.setText(Html.fromHtml(resume.getExperiences()));
            education.setText(Html.fromHtml(resume.getEducation()));
            skills.setText(Html.fromHtml(resume.getSkills()));
            other.setText(Html.fromHtml(resume.getOther()));
        }



    }



}
