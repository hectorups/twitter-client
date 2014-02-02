package com.codepath.apps.mytwitterapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mytwitterapp.ComposeTweetActivity;
import com.codepath.apps.mytwitterapp.EndlessScrollListener;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.TweetsAdapter;
import com.codepath.apps.mytwitterapp.models.Tweet;

import org.json.JSONArray;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by hectormonserrate on 26/01/14.
 */
public abstract class TweetsListFragments extends Fragment {
    private static final String TWEET_RESULTS = "tweet_results";
    private static final String TAG = "timelineactivity";
    protected static final int TWEETS_PER_LOAD = 25;
    public final static int REQUEST_CODE = 20;
    protected ListView lvTweets;
    protected TweetsAdapter tweetsAdapter;
    protected PullToRefreshLayout pullToRefreshLayout;

    protected static final int LOAD_MORE_MODE = 1;
    protected static final int LOAD_UPDATES_MODE = 2;
    protected static final int UPDATE_MODE = 3;

    protected ArrayList<Tweet> tweetList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        if (savedInstanceState != null) {
            tweetList = savedInstanceState.getParcelableArrayList(TWEET_RESULTS);

        } else {
            tweetList = new ArrayList<Tweet>();
        }

        setupUI(v);

        // If we dont have tweets try to load them now
        if(tweetList.size() == 0){
            loadTweets(LOAD_MORE_MODE);
        }

        return v;
    }

    public void setupUI(View v){
        lvTweets = (ListView) v.findViewById(R.id.lvTweet);
        tweetsAdapter = new TweetsAdapter(getActivity(), tweetList);
        lvTweets.setAdapter(tweetsAdapter);
        lvTweets.setOnScrollListener(endlessScrollListener);

        pullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(onRefreshListener)
                .setup(pullToRefreshLayout);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putParcelableArrayList(TWEET_RESULTS, tweetList);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            //loadTweets(LOAD_UPDATES_MODE);
            Tweet tweet = data.getParcelableExtra(ComposeTweetActivity.CREATED_TWEET);
            ArrayList<Tweet> tweets = new ArrayList<Tweet>();
            tweets.add(tweet);
            updateAdaptor(tweets, LOAD_UPDATES_MODE);
        }
    }



    public void updateAdaptor(ArrayList<Tweet> tweets, int mode){
        pullToRefreshLayout.setRefreshComplete();

        if(mode == UPDATE_MODE){
            tweetList.clear();
        }

        switch( mode ){
            case LOAD_MORE_MODE:
                tweetList.addAll(tweets);
                break;
            default:
                tweetList.addAll(0, tweets);
        }

        tweetsAdapter.notifyDataSetChanged();
    }

    public boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void loadTweets(int mode){
        if( isOnline() ){
            loadTweetsFromApi(mode);
        } else {
            loadTweetsFromDb();
        }
    }

    protected OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefreshStarted(View view) {
            loadTweets(LOAD_UPDATES_MODE);
        }
    };

    protected EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            loadTweets(LOAD_MORE_MODE);
        }
    };

    /* Rx */

    protected Observable<ArrayList<Tweet>> saveTweetsObservable(final JSONArray jsonTweets) {
        return Observable.create(new Observable.OnSubscribeFunc<ArrayList<Tweet>>() {
            @Override
            public Subscription onSubscribe(Observer<? super ArrayList<Tweet>> tweetObserver) {
                try {
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets, true);
                    // Give tweets immediately to observer so they can be shown in UI
                    tweetObserver.onNext(tweets);
                    // Continue to save the tweets to DB
                    Tweet.saveTweets(tweets);
                    tweetObserver.onCompleted();
                } catch (Exception e) {
                    tweetObserver.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    protected Action1<ArrayList<Tweet>> saveTweetsObserver(final int mode){
        return (new Action1<ArrayList<Tweet>>() {
            @Override
            public void call(ArrayList<Tweet> tweets) {
                updateAdaptor(tweets, mode);
            }
        });
    };

    /* Abstract methods */
    protected abstract void loadTweetsFromApi(int mode);
    protected abstract void loadTweetsFromDb();

}
