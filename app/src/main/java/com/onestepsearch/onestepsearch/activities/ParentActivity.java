package com.onestepsearch.onestepsearch.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.fragments.AboutFragment;
import com.onestepsearch.onestepsearch.fragments.BuyFragment;
import com.onestepsearch.onestepsearch.fragments.DownloadsFragment;
import com.onestepsearch.onestepsearch.fragments.HistoryFragment;
import com.onestepsearch.onestepsearch.fragments.SearchFragment;

import java.util.ArrayList;
import java.util.Date;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity);

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

        if (id == R.id.nav_about) {
            Fragment fragment = new AboutFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_buy) {
            Fragment fragment = new BuyFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_downloads) {
            Fragment fragment = new DownloadsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_history) {
            Fragment fragment = new HistoryFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_search) {
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_signout) {

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
//        ParseUser.logOut();
//        Intent intent = new Intent(ParentActivity.this, LoginActivity.class);
//        this.startActivity(intent);
//        finish();
    }


    public ArrayList<String> getSearches(){
        ArrayList<String> searches = new ArrayList<>();
//        SharedPreferences sp1 = this.getSharedPreferences(HISTORY_PREFS, 0);
//        String rawHistory = sp1.getString("history", null);
//
//        if (rawHistory != null) {
//            String[] historyArray = rawHistory.split(":::");
//            for(String ha : historyArray){
//                searches.add(ha);
//            }
//        }

        return searches;
    }


    public void clearHistory(){
//        SharedPreferences sp = this.getSharedPreferences(HISTORY_PREFS, 0);
//        SharedPreferences.Editor ed = sp.edit();
//        ed.clear();
//        ed.commit();
//
//        Toast.makeText(getApplicationContext(), "History Cleared", Toast.LENGTH_SHORT).show();
    }


    public void addSearchQuery(String query){

//        ParseUser parseUser = ParseUser.getCurrentUser();
//        if(parseUser != null){
//
//            if(parseUser.getString("plan").equals("free")) {
//                if (parseUser.getInt("currentNumOfSearches") >= parseUser.getInt("numOfSearches")) {
//                    maxSearchesMet(parseUser.getInt("numOfSearches"));
//                } else {
//
//                    ArrayList<String> searches = getSearches();
//
//                    String rawHistory = "";
//                    for (String ha : searches) {
//                        rawHistory = rawHistory + ha + ":::";
//                    }
//                    rawHistory = rawHistory + query + ":::";
//
//                    SharedPreferences sp1 = this.getSharedPreferences(HISTORY_PREFS, 0);
//                    SharedPreferences.Editor ed = sp1.edit();
//
//                    ed.putString("history", rawHistory);
//                    ed.apply();
//
//                    int newNumberOfSearches = parseUser.getInt("currentNumOfSearches") + 1;
//                    parseUser.put("currentNumOfSearches", newNumberOfSearches);
//                    parseUser.saveInBackground();
//                }
//            } else {
//                if(parseUser.getDate("plan_expiration").after(new Date())){
//
//                    if(parseUser.getInt("currentNumOfSearches") < parseUser.getInt("numOfSearches")) {
//                        ArrayList<String> searches = getSearches();
//
//                        String rawHistory = "";
//                        for (String ha : searches) {
//                            rawHistory = rawHistory + ha + ":::";
//                        }
//                        rawHistory = rawHistory + query + ":::";
//
//                        SharedPreferences sp1 = this.getSharedPreferences(HISTORY_PREFS, 0);
//                        SharedPreferences.Editor ed = sp1.edit();
//
//                        ed.putString("history", rawHistory);
//                        ed.apply();
//
//                        int newNumberOfSearches = parseUser.getInt("currentNumOfSearches") + 1;
//                        parseUser.put("currentNumOfSearches", newNumberOfSearches);
//                        parseUser.saveInBackground();
//                    } else {
//                        maxSearchesMet(parseUser.getInt("numOfSearches"));
//                    }
//
//                } else {
//                    planExpired(parseUser.getString("plan"), parseUser.getDate("plan_expiration"));
//                }
//            }
//
//        } else {
//            logout();
//        }
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
