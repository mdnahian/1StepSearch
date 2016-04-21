package com.onestepsearch.onestepsearch.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.core.CrudInBackground;
import com.onestepsearch.onestepsearch.core.OnTaskCompleted;
import com.onestepsearch.onestepsearch.core.SavedSession;
import com.onestepsearch.onestepsearch.fragments.AboutFragment;
import com.onestepsearch.onestepsearch.fragments.BuyFragment;
import com.onestepsearch.onestepsearch.fragments.DownloadsFragment;
import com.onestepsearch.onestepsearch.fragments.HistoryFragment;
import com.onestepsearch.onestepsearch.fragments.SearchFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private SavedSession savedSession;
    private static final String HISTORY_PREFS = "History";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity);

        savedSession = (SavedSession) getIntent().getSerializableExtra("SavedSession");
        if(savedSession.getUsername() == null){
            logout();
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ImageView sideBtn = (ImageView) findViewById(R.id.sideBtn);
        sideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_search);

            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("SavedSession", savedSession);

        if (id == R.id.nav_about) {
            AboutFragment fragment = new AboutFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_buy) {
            BuyFragment fragment = new BuyFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_downloads) {
            DownloadsFragment fragment = new DownloadsFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_history) {
            HistoryFragment fragment = new HistoryFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_search) {
            SearchFragment fragment = new SearchFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_signout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void notConnectedDialog(){
        new AlertDialog.Builder(ParentActivity.this)
                .setTitle("Cannot Connect to Internet")
                .setMessage("You are not connected to the internet.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.logo_red)
                .show();


        navigationView.setCheckedItem(R.id.nav_downloads);

        Fragment fragment = new DownloadsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }


    public void logout(){
        SharedPreferences sp = getSharedPreferences("SavedSession", 0);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.clear();
        Ed.apply();

        Intent intent = new Intent(ParentActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public ArrayList<String> getSearches(){
        ArrayList<String> searches = new ArrayList<>();
        SharedPreferences sp1 = this.getSharedPreferences(HISTORY_PREFS, 0);
        String rawHistory = sp1.getString("history", null);

        if (rawHistory != null) {
            String[] historyArray = rawHistory.split(":::");
            for(String ha : historyArray){
                searches.add(ha);
            }
        }

        return searches;
    }


    public void clearHistory(){
        SharedPreferences sp = this.getSharedPreferences(HISTORY_PREFS, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.commit();

        Toast.makeText(getApplicationContext(), "History Cleared", Toast.LENGTH_SHORT).show();
    }


    public void addSearchQuery(String query, final SavedSession savedSession){

        if(savedSession != null){

            if(savedSession.getPlan().equals("free")) {
                if (savedSession.getCurrentNumOfSearches() >= savedSession.getNumOfSearches()) {
                    maxSearchesMet(savedSession.getNumOfSearches());
                } else {

                    ArrayList<String> searches = getSearches();

                    String rawHistory = "";
                    for (String ha : searches) {
                        rawHistory = rawHistory + ha + ":::";
                    }
                    rawHistory = rawHistory + query + ":::";

                    SharedPreferences sp1 = this.getSharedPreferences(HISTORY_PREFS, 0);
                    SharedPreferences.Editor ed = sp1.edit();

                    ed.putString("history", rawHistory);
                    ed.apply();

                    final int newNumberOfSearches = savedSession.getCurrentNumOfSearches() + 1;

                    CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
                        @Override
                        public void onTaskComplete(String response) {
                            savedSession.setCurrentNumOfSearches(newNumberOfSearches);
                        }
                    });

                    String sql = "UPDATE users SET currentNumOfSearches='"+newNumberOfSearches+"' WHERE username='"+savedSession.getUsername()+"'";
                    crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));

                }
            } else {


                try {
                    DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    Date date = format.parse(savedSession.getPlanExpiration());

                    if(date.after(new Date())){

                        if(savedSession.getCurrentNumOfSearches() < savedSession.getNumOfSearches()) {
                            ArrayList<String> searches = getSearches();

                            String rawHistory = "";
                            for (String ha : searches) {
                                rawHistory = rawHistory + ha + ":::";
                            }
                            rawHistory = rawHistory + query + ":::";

                            SharedPreferences sp1 = this.getSharedPreferences(HISTORY_PREFS, 0);
                            SharedPreferences.Editor ed = sp1.edit();

                            ed.putString("history", rawHistory);
                            ed.apply();

                            final int newNumberOfSearches = savedSession.getCurrentNumOfSearches() + 1;

                            CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
                                @Override
                                public void onTaskComplete(String response) {
                                    savedSession.setCurrentNumOfSearches(newNumberOfSearches);
                                }
                            });

                            String sql = "UPDATE users SET currentNumOfSearches='"+newNumberOfSearches+"' WHERE username='"+savedSession.getUsername()+"'";
                            crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));

                        } else {
                            maxSearchesMet(savedSession.getNumOfSearches());
                        }

                    } else {
                        planExpired(savedSession.getPlan(), date);
                    }


                } catch (ParseException e){
                    Log.d("Crash", e.getMessage());
                }



            }

        } else {
            logout();
        }
    }


    public boolean isOnline() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public void maxSearchesMet(int numOfSearches){
        Log.d("Crash", "max searches met");

        new AlertDialog.Builder(ParentActivity.this)
                .setTitle("Max Searches Reached")
                .setMessage("You have used up "+numOfSearches+" of your "+numOfSearches+" searches. Buy a package to continue searching.")
                .setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.logo_red)
                .show();


//        buy(new TextView(this));
    }


    public void planExpired(String plan, Date expiration){
        Log.d("Crash", "plan expired");

        new AlertDialog.Builder(ParentActivity.this)
                .setTitle("Plan Expired")
                .setMessage("Your plan "+plan+" has expired on "+expiration.toString()+". Buy a new package to continue searching.")
                .setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.logo_red)
                .show();


//        buy(new TextView(this));
    }


    public void buildDialog(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(R.drawable.logo_red)
                .show();
    }


}
