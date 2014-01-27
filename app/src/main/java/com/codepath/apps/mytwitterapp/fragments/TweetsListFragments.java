package com.codepath.apps.mytwitterapp.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.TweetsAdapter;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by hectormonserrate on 26/01/14.
 */
public class TweetsListFragments extends Fragment {
    private static final String TAG = "TweetsListFragments";
    private static final int TWEETS_PER_LOAD = 25;
    private ListView lvTweets;
    private TweetsAdapter tweetsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweet);

        tweetsAdapter = new TweetsAdapter(getActivity(), new ArrayList<Tweet>());
        lvTweets.setAdapter(tweetsAdapter);

        loadTweets();

        return v;
    }


    private void loadTweets(){
        if( isOnline() ){
            loadTweetsFromApi();
        } else {
            loadTweetsFromDb();
        }
    }

    private void loadTweetsFromDb(){
        tweetsAdapter.clear();
        updateAdaptor(Tweet.recentTweets(TWEETS_PER_LOAD));
    }

    private void loadTweetsFromApi(){
        MyTwitterApp.getRestClient().getHomeTimeline(TWEETS_PER_LOAD, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                tweetsAdapter.clear();
                Log.d("DEBUG", jsonTweets.toString());
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
                updateAdaptor(tweets);
            }

            @Override
            public void onFailure(Throwable e, JSONObject error) {
                Log.e(TAG, e.toString());
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
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefreshStarted(View view) {
            loadTweets();
        }
    };

}
