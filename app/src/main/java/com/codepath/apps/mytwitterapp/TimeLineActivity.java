package com.codepath.apps.mytwitterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

public class TimeLineActivity extends Activity {

    private ListView lvTweets;
    private TweetsAdapter tweetsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        lvTweets = (ListView) findViewById(R.id.lvTweet);
        tweetsAdapter = new TweetsAdapter(this, new ArrayList<Tweet>());
        lvTweets.setAdapter(tweetsAdapter);

        MyTwitterApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                Log.d("DEBUG", jsonTweets.toString());
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);

                for(Tweet t: tweets){
                    tweetsAdapter.add(t);
                }

                tweetsAdapter.notifyDataSetChanged();
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


}
