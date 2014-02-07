package com.codepath.apps.mytwitterapp.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.ProfileTweetsAdapter;
import com.codepath.apps.mytwitterapp.TweetsAdapter;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;

/**
 * Created by hectormonserrate on 02/02/14.
 */
public class UserTimelineFragment extends TweetsListFragments {
    protected String TAG = "UserTimelineFragment";
    private static final String EXTRA_USER = "user";

    private User user;

    public static UserTimelineFragment newInstance(User user){
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        UserTimelineFragment fragment = new UserTimelineFragment();
        fragment.setArguments(args);

        return fragment;
    }

    // Add dummy Tweet for the header
    @Override
    public ArrayList<Tweet> createNewList(){
        ArrayList<Tweet> list =  new ArrayList<Tweet>();
        list.add(new Tweet());
        return list;
    }

    @Override
    public int firstTweetPosition(){
        return 1;
    }

    @Override
    public TweetsAdapter getAdapter(){
        return new ProfileTweetsAdapter(this, getActivity(), tweetList, user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        user = getArguments().getParcelable(EXTRA_USER);
    }

    @Override
    protected void loadTweetsFromDb(){
//        updateAdaptor(Tweet.recentTweets(TWEETS_PER_LOAD), UPDATE_MODE);
//        pullToRefreshLayout.setRefreshComplete();
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

        long userId = 0;
        if( user != null ){
            userId = user.getUserId();
        }

        MyTwitterApp.getRestClient().getUserTimeline(TWEETS_PER_LOAD, maxId, sinceId, userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                Log.d("DEBUG", jsonTweets.toString());

                saveTweetsObservable(jsonTweets)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(saveTweetsObserver(mode));
            }

            @Override
            public void onFailure(Throwable e, JSONObject error) {
                Log.e(TAG, e.toString());
                pullToRefreshLayout.setRefreshComplete();
            }
        });
    }

}




