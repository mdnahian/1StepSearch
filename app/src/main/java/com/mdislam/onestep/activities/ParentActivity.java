package com.mdislam.onestep.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mdislam.onestep.R;
import com.mdislam.onestep.fragments.AboutFragment;
import com.mdislam.onestep.fragments.BuyFragment;
import com.mdislam.onestep.fragments.DownloadsFragment;
import com.mdislam.onestep.fragments.HistoryFragment;
import com.mdislam.onestep.fragments.SearchFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mdislam on 12/22/15.
 */
public class ParentActivity extends FragmentActivity {

    private String[] nav;
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;

    private ImageView sideBtn;
    private ArrayList<TextView> menuItems;

    private static final int MAX_SESSIONS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity);

        checkUserSession();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sideBtn = (ImageView) findViewById(R.id.sideBtn);
        sideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        nav = getResources().getStringArray(R.array.nav);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ListView) findViewById(R.id.left_drawer);

        leftDrawer.setAdapter(new NavigationAdapter(this, nav));

        search(new TextView(this));
    }




    private class NavigationAdapter extends ArrayAdapter<String>{

        public NavigationAdapter(Context context, String[] items) {
            super(context, 0, items);
            menuItems = new ArrayList<>();
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final String item = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
            }

            final TextView menuItem = (TextView) convertView.findViewById(R.id.menuItem);
            menuItem.setText(item);

            menuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            about(menuItem);
                            break;
                        case 1:
                            buy(menuItem);
                            break;
                        case 2:
                            downloads(menuItem);
                            break;
                        case 3:
                            history(menuItem);
                            break;
                        case 4:
                            search(menuItem);
                            break;
                        case 5:
                            logout();
                            break;
                    }


                    leftDrawer.setItemChecked(position, true);
                    setTitle(nav[position]);
                    drawerLayout.closeDrawer(leftDrawer);
                }
            });

            menuItems.add(menuItem);

            return convertView;

        }


    }



    private void clearSelection(TextView currentItem){
        for(TextView menuItem : menuItems){
            menuItem.setBackgroundColor(Color.parseColor("#930e0f"));
        }

        currentItem.setBackgroundColor(Color.parseColor("#780B0C"));
    }




    private void downloads(TextView currentItem){
        clearSelection(currentItem);
        Fragment fragment = new DownloadsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void about(TextView currentItem){
        clearSelection(currentItem);
        Fragment fragment = new AboutFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void buy(TextView currentItem){
        clearSelection(currentItem);
        Fragment fragment = new BuyFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void history(TextView currentItem){
        clearSelection(currentItem);
        Fragment fragment = new HistoryFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void search(TextView currentItem){
        if(isOnline()) {
            clearSelection(currentItem);
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            notConnectedDialog();
        }
    }




    private void checkUserSession(){

        if(ParseSession.getCurrentSessionInBackground() == null){
            logout();
        } else{

            ParseQuery<ParseSession> query = ParseSession.getQuery();
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.findInBackground(new FindCallback<ParseSession>() {
                @Override
                public void done(List<ParseSession> sessions, ParseException e) {
                    if (e == null) {
                        if (sessions.size() == MAX_SESSIONS) {
                            for (ParseSession session : sessions) {
                                if (session.getCreatedAt().after(ParseSession.getCurrentSessionInBackground().getResult().getCreatedAt())) {
                                    session.deleteInBackground();
                                    logout();
                                } else {
                                    session.deleteInBackground();
                                    break;
                                }
                            }
                        } else {
                            Log.d("Crash", "Number of sessions for " + ParseUser.getCurrentUser().getUsername() + ": " + Integer.toString(sessions.size()));
                        }
                    } else {
                        Log.d("Crash", "Session does not exist.");
                    }
                }
            });


        }

    }

    private void logout(){
        ParseUser.logOut();
        Intent intent = new Intent(ParentActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finish();
    }


    public ArrayList<String> getSearches(){
        ArrayList<String> searches = new ArrayList<>();
        SharedPreferences sp1 = this.getSharedPreferences("SearchHistory", 0);
        String rawHistory = sp1.getString("history", null);

        if (rawHistory != null) {
            String[] historyArray = rawHistory.split(":::");
            for(String ha : historyArray){
                searches.add(ha);
            }
        }

        return searches;
    }


    public void addSearchQuery(String query){
        ArrayList<String> searches = getSearches();

        String rawHistory = "";
        for(String ha : searches){
            rawHistory = rawHistory+ha+":::";
        }
        rawHistory = rawHistory+query+":::";

        SharedPreferences sp1 = this.getSharedPreferences("SearchHistory", 0);
        SharedPreferences.Editor ed = sp1.edit();

        ed.putString("history", rawHistory);
        ed.apply();
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

    public void notConnectedDialog(){
        new AlertDialog.Builder(ParentActivity.this)
                .setTitle("Cannot Connect to Internet")
                .setMessage("You are not connected to the internet.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.alert)
                .show();
        downloads(new TextView(this));
    }


}
