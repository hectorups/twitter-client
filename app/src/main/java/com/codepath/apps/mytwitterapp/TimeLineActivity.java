package com.codepath.apps.mytwitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mytwitterapp.fragments.HomeTimelineFragment;
import com.codepath.apps.mytwitterapp.fragments.MentionsFragment;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

public class TimeLineActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final String TAG = "timelineactivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        loadProfileInfo();
        setupNavigationTabs();

    }

    public void loadProfileInfo() {
        MyTwitterApp.getRestClient().getMyInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                User u = User.fromJson(json);
                setTitle("@" + u.getScreenName());
            }
            
            @Override
            public void onFailure(Throwable  e, JSONObject error) {
                Log.e(TAG, e.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.time_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_write_tweet:
                Intent intent = new Intent(getApplicationContext(), ActivityComposeTweet.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                MyTwitterApp.getRestClient().clearAccessToken();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupNavigationTabs(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        ActionBar.Tab tabHome = actionBar.newTab()
                .setText("Home")
                .setTag("HomeTimeLineFragment")
                .setIcon(R.drawable.ic_action_home)
                .setTabListener(this);

        ActionBar.Tab tabMentions = actionBar.newTab()
                .setText("Mentions")
                .setTag("MentionsTimeLineFragment")
                .setIcon(R.drawable.ic_action_mention)
                .setTabListener(this);

        actionBar.addTab(tabHome);
        actionBar.addTab(tabMentions);

        actionBar.selectTab(tabHome);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fts = manager.beginTransaction();
        if(tab.getTag() == "HomeTimeLineFragment"){
            fts.replace(R.id.frameContainer, new HomeTimelineFragment());
        } else {
            fts.replace(R.id.frameContainer, new MentionsFragment());
        }
        fts.commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}


}
