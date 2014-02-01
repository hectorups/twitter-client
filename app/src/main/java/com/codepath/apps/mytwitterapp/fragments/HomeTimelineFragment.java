package com.codepath.apps.mytwitterapp.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hectormonserrate on 26/01/14.
 */
public class HomeTimelineFragment extends TweetsListFragments {
    protected String TAG = "HomeTimelineFragment";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadTweetsFromDb(){
        tweetsAdapter.clear();
        updateAdaptor(Tweet.recentTweets(TWEETS_PER_LOAD), UPDATE_MODE);
        pullToRefreshLayout.setRefreshComplete();
    }

    @Override
    protected void loadTweetsFromApi(final int mode){
        long maxId = 0;
        long sinceId = 0;

        if(tweetList.size() > 0){
            if(mode == LOAD_MORE_MODE){
                maxId = tweetList.get(tweetList.size() - 1).getTweetId() - 1;
            } else if(mode == LOAD_UPDATES_MODE){
                sinceId = tweetList.get(0).getTweetId();
            }
        }

        MyTwitterApp.getRestClient().getHomeTimeline(TWEETS_PER_LOAD, maxId, sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                Log.d("DEBUG", jsonTweets.toString());
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
                updateAdaptor(tweets, mode);
            }

            @Override
            public void onFailure(Throwable e, JSONObject error) {
                Log.e(TAG, e.toString());
                pullToRefreshLayout.setRefreshComplete();
            }
        });
    }

}
