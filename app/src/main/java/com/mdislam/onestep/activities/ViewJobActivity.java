package com.mdislam.onestep.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.mdislam.onestep.R;
import com.mdislam.onestep.data.Job;

/**
 * Created by mdislam on 1/20/16.
 */
public class ViewJobActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_job_activity);

        Intent intent = getIntent();
        Job job = (Job) intent.getSerializableExtra("job");

        TextView title = (TextView) findViewById(R.id.title);
        TextView location = (TextView) findViewById(R.id.location);
        TextView company = (TextView) findViewById(R.id.company);
        TextView description = (TextView) findViewById(R.id.description);
        TextView date = (TextView) findViewById(R.id.date);

        if(job != null) {
            title.setText(job.getTitle());
            location.setText(job.getLocation());
            company.setText(job.getCompany());
            description.setText(Html.fromHtml(job.getSummary()));
            date.setText(job.getDate());
        } else{
            finish();
        }

    }
    
    
    
    
}
