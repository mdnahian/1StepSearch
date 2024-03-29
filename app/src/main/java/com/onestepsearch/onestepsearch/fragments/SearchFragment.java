package com.onestepsearch.onestepsearch.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.onestepsearch.onestepsearch.fragments.searchfragments.CraigslistFragment;
import com.onestepsearch.onestepsearch.fragments.searchfragments.ImageFragment;
import com.onestepsearch.onestepsearch.fragments.searchfragments.JobsFragment;
import com.onestepsearch.onestepsearch.fragments.searchfragments.MusicFragment;
import com.onestepsearch.onestepsearch.fragments.searchfragments.ResumeFragment;
import com.onestepsearch.onestepsearch.fragments.searchfragments.SermonFragment;
import com.onestepsearch.onestepsearch.fragments.searchfragments.YoutubeFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mdislam on 4/5/16.
 */
public class SearchFragment extends Fragment {

    private ImageView youtubeBtn;
    private ImageView imageBtn;
    private ImageView musicBtn;
    private ImageView resumeBtn;

    private ImageView moreBtn;

    private ImageView jobsBtn;
    private ImageView sermonBtn;
    private ImageView craigslistBtn;

    private LinearLayout homeView;
    private TextView greeting;
    private ImageView mic;
    protected static final int RESULT_SPEECH = 1;

    private EditText searchBar;
    public ImageView searchBtn;

    public static MediaPlayer mediaPlayer;
    public static int playbackPosition;
    public static boolean isPlaying;

    private int tab;
    private int tabSet = 2;


    private ParentActivity parentActivity;
    private SavedSession savedSession;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        parentActivity = (ParentActivity) getActivity();

        savedSession = (SavedSession) getActivity().getIntent().getSerializableExtra("SavedSession");
        if(savedSession.getUsername().equals("")){
            parentActivity.logout();
        }

        tab = 1;

        searchBar = (EditText) rootView.findViewById(R.id.searchBar);
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideMic();
                } else {
                    showMic();
                }
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchBar.getText().toString().equals("")) {
                    clearFragment();
                    showMic();
                }
            }
        });

        homeView = (LinearLayout) rootView.findViewById(R.id.homeView);

        greeting = (TextView) rootView.findViewById(R.id.greeting);
        updateGreeting();

        mic = (ImageView) rootView.findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    searchBar.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getActivity().getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });


        youtubeBtn = (ImageView) rootView.findViewById(R.id.youtubeBtn);
        youtubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 1;
                youtubeTab();
                updateGreeting();
            }
        });


        imageBtn = (ImageView) rootView.findViewById(R.id.imageBtn);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 2;
                imageTab();
                updateGreeting();
            }
        });


        musicBtn = (ImageView) rootView.findViewById(R.id.musicBtn);
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 3;
                musicTab();
                updateGreeting();
            }
        });

        jobsBtn = (ImageView) rootView.findViewById(R.id.jobBtn);
        jobsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 4;
                jobsTab();
                updateGreeting();
            }
        });

        moreBtn = (ImageView) rootView.findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreTab();
            }
        });

        resumeBtn = (ImageView) rootView.findViewById(R.id.resumeBtn);
        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 5;
                resumeTab();
                updateGreeting();
            }
        });

        craigslistBtn = (ImageView) rootView.findViewById(R.id.craigslistBtn);
        craigslistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tab = 6;
//                craigslistTab();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Coming Soon!")
                        .setMessage("Craigslist search will be available soon.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.logo)
                        .show();
            }
        });


        sermonBtn = (ImageView) rootView.findViewById(R.id.sermonBtn);
        sermonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = 7;
                sermonTab();
                updateGreeting();
            }
        });



        searchBtn = (ImageView) rootView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });


        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/";
        File rootDirectory = new File(filePath);

        if(rootDirectory.mkdir()){
            Log.d("Crash", "Made new OneStepSearch folder...");
        }


        String category = getActivity().getIntent().getStringExtra("category");
        String query = getActivity().getIntent().getStringExtra("query");

        if(category != null){
            searchBar.setText(query);

            switch (category) {
                case "YouTube":
                    tab = 1;
                    youtubeTab();
                    break;
                case "Images":
                    tab = 2;
                    imageTab();
                    break;
                case "Music":
                    tab = 3;
                    musicTab();
                    break;
                case "Jobs":
                    tab = 4;
                    jobsTab();
                    break;
                case "Resumes":
                    tabSet = 2;
                    moreTab();
                    tab = 5;
                    resumeTab();
                    break;
                case "Craigslist":
                    tabSet = 2;
                    moreTab();
                    tab = 6;
                    craigslistTab();
                    break;
                case "Sermons":
                    tabSet = 2;
                    moreTab();
                    tab = 7;
                    sermonTab();
                    break;
            }

        }

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    searchBar.setText(text.get(0));
                    search();
                }
                break;
            }

        }
    }


    private void showMic(){
        homeView.setVisibility(View.VISIBLE);
    }

    private void hideMic(){

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        homeView.setVisibility(View.GONE);
    }

    private void clearFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EmptyFragment emptyFragment = new EmptyFragment();
        fragmentTransaction.replace(R.id.fragment, emptyFragment);
        fragmentTransaction.commit();
    }


    private void updateGreeting(){
        String strGreeting = "Hello, "+ savedSession.getFname()+"\n"+
                "Current Plan: "+savedSession.getPlan()+"\n"+
                "Searches: "+savedSession.getCurrentNumOfSearches()+"/"+savedSession.getNumOfSearches();
        greeting.setText(strGreeting);
    }



    private void youtubeTab(){
        clearFragment();

        youtubeBtn.setBackgroundColor(Color.parseColor("#63090A"));
        imageBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        musicBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        jobsBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search YouTube...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void imageTab(){
        clearFragment();

        youtubeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        imageBtn.setBackgroundColor(Color.parseColor("#63090A"));
        musicBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        jobsBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Images...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void musicTab(){
        clearFragment();

        youtubeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        imageBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        musicBtn.setBackgroundColor(Color.parseColor("#63090A"));
        jobsBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Music...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void jobsTab(){
        clearFragment();

        youtubeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        imageBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        musicBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        jobsBtn.setBackgroundColor(Color.parseColor("#63090A"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Jobs...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }



    private void moreTab(){
        switch(tabSet){
            case 1:
                youtubeBtn.setVisibility(View.VISIBLE);
                imageBtn.setVisibility(View.VISIBLE);
                musicBtn.setVisibility(View.VISIBLE);
                jobsBtn.setVisibility(View.VISIBLE);

                resumeBtn.setVisibility(View.GONE);
                craigslistBtn.setVisibility(View.GONE);
                sermonBtn.setVisibility(View.GONE);

                tabSet = 2;
                tab = 1;
                youtubeTab();
                break;
            case 2:
                resumeBtn.setVisibility(View.VISIBLE);
                craigslistBtn.setVisibility(View.VISIBLE);
                sermonBtn.setVisibility(View.VISIBLE);

                youtubeBtn.setVisibility(View.GONE);
                imageBtn.setVisibility(View.GONE);
                musicBtn.setVisibility(View.GONE);
                jobsBtn.setVisibility(View.GONE);

                tabSet = 1;
                tab = 5;
                resumeTab();
                break;
        }
    }



    private void resumeTab(){
        clearFragment();

        resumeBtn.setBackgroundColor(Color.parseColor("#63090A"));
        craigslistBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        sermonBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Résumé...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void craigslistTab(){
        clearFragment();

        resumeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        craigslistBtn.setBackgroundColor(Color.parseColor("#63090A"));
        sermonBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Craigslist...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    private void sermonTab(){
        clearFragment();

        resumeBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        craigslistBtn.setBackgroundColor(Color.parseColor("#930e0f"));
        sermonBtn.setBackgroundColor(Color.parseColor("#63090A"));
        moreBtn.setBackgroundColor(Color.parseColor("#930e0f"));

        String newHint = "Search Sermons...";
        searchBar.setHint(newHint);

        showMic();
        //((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }





    private void search(){
        if (InputFilter.checkNumCharacters(searchBar.getText().toString(), 0)) {

            hideMic();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable("query", searchBar.getText().toString());
            bundle.putSerializable("SavedSession", savedSession);

            switch (tab) {
                case 1:
                    YoutubeFragment youtubeFragment = new YoutubeFragment();
                    youtubeFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, youtubeFragment);
                    fragmentTransaction.commit();
                    break;
                case 2:
                    ImageFragment imageFragment = new ImageFragment();
                    imageFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, imageFragment);
                    fragmentTransaction.commit();
                    break;
                case 3:
                    MusicFragment musicFragment = new MusicFragment();
                    musicFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, musicFragment);
                    fragmentTransaction.commit();
                    break;
                case 4:
                    JobsFragment jobsFragment = new JobsFragment();
                    jobsFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, jobsFragment);
                    fragmentTransaction.commit();
                    break;
                case 5:
                    ResumeFragment resumeFragment = new ResumeFragment();
                    resumeFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, resumeFragment);
                    fragmentTransaction.commit();
                    break;
                case 6:
                    CraigslistFragment craigslistFragment = new CraigslistFragment();
                    craigslistFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, craigslistFragment);
                    fragmentTransaction.commit();
                    break;
                case 7:
                    SermonFragment sermonFragment = new SermonFragment();
                    sermonFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment, sermonFragment);
                    fragmentTransaction.commit();
                    break;
            }
        }
    }


    public static void playAudio(String url) throws Exception {

        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public static void killMediaPlayer() {
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}

