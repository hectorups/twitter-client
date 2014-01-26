package com.codepath.apps.mytwitterapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimeLineActivity extends Activity {
    private static final String TAG = "timelineactivity";
    private static final int TWEETS_PER_LOAD = 25;
    private ListView lvTweets;
    private TweetsAdapter tweetsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        lvTweets = (ListView) findViewById(R.id.lvTweet);

        tweetsAdapter = new TweetsAdapter(this, new ArrayList<Tweet>());
        lvTweets.setAdapter(tweetsAdapter);

        loadProfileInfo();
        loadTweets();

    }

    public void loadProfileInfo() {
        MyTwitterApp.getRestClient().getMyInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                User u = User.fromJson(json);
                setTitle("@" + u.getScreenName());
            }
            
            @Override
            public void onFailure(Throwable e, JSONObject error) {
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
            case R.id.action_add_tweet:
                Intent intent = new Intent(getApplicationContext(), ActivityComposeTweet.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadTweets(){
        if( isOnline() ){
            loadTweetsFromApi();
        } else {
            loadTweetsFromDb();
        }
    }

    private void loadTweetsFromDb(){
        updateAdaptor(Tweet.recentTweets(TWEETS_PER_LOAD));
    }

    private void loadTweetsFromApi(){
        MyTwitterApp.getRestClient().getHomeTimeline(TWEETS_PER_LOAD, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                Log.d("DEBUG", jsonTweets.toString());
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
                updateAdaptor(tweets);
            }
        });
    }

    public void updateAdaptor(ArrayList<Tweet> tweets){
        for(Tweet t: tweets){
            tweetsAdapter.add(t);
        }

        tweetsAdapter.notifyDataSetChanged();
    }

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}
