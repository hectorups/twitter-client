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
public class MentionsFragment extends TweetsListFragments {
    protected String TAG = "MentionsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        loadTweets();
    }

    private void loadTweets(){
        loadTweetsFromApi();
//        if( isOnline() ){
//            loadTweetsFromApi();
//        } else {
//            loadTweetsFromDb();
//        }
    }

    private void loadTweetsFromDb(){
        tweetsAdapter.clear();
        updateAdaptor(Tweet.recentTweets(TWEETS_PER_LOAD));
    }

    private void loadTweetsFromApi(){
        MyTwitterApp.getRestClient().getMentions( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                tweetsAdapter.clear();
                Log.d(TAG, jsonTweets.toString());
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
                updateAdaptor(tweets);
            }

            @Override
            public void onFailure(Throwable e, JSONObject error) {
                Log.e(TAG, e.toString());
            }
        });
    }
}
