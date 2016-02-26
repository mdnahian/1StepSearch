package com.mdislam.onestep;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by mdislam on 12/29/15.
 */
public class VideoViewerActivty extends Activity {

    private boolean isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.video_viewer_activty);

        Intent intent = getIntent();
        String videoURL = intent.getExtras().getString("videoURL");
        String videoTitle = intent.getExtras().getString("videoTitle");

        final LinearLayout topPanel = (LinearLayout) findViewById(R.id.topPanel);

        TextView title = (TextView) findViewById(R.id.title);
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        final VideoView video = (VideoView) findViewById(R.id.video);

        title.setText(videoTitle);


        final MediaController controller = new MediaController(this){
            @Override
            public void hide() {
                this.show(0);
            }

            @Override
            public void setMediaPlayer(MediaController.MediaPlayerControl player) {
                super.setMediaPlayer(player);
                this.show();
            }

            @Override
            public void show() {
                super.show();
            }

        };


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        isShowing = true;
        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isShowing) {
                        video.setMediaController(controller);
                        controller.setVisibility(View.VISIBLE);
                        controller.show();
                        topPanel.setVisibility(View.VISIBLE);
                        isShowing = true;
                    } else{
                        video.setMediaController(null);
                        controller.setVisibility(View.GONE);
                        controller.hide();
                        topPanel.setVisibility(View.GONE);
                        isShowing = false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        video.setMediaController(controller);
        video.setVideoURI(Uri.parse(videoURL));
        video.start();

    }


}