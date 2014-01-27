package com.codepath.apps.mytwitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

public class TimeLineActivity extends ActionBarActivity {
    private static final String TAG = "timelineactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        loadProfileInfo();

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


}
