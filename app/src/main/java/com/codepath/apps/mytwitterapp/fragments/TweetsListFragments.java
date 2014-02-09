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
import android.widget.Toast;

import com.codepath.apps.mytwitterapp.ComposeTweetActivity;
import com.codepath.apps.mytwitterapp.EndlessScrollListener;
import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.TweeterDetailActivity;
import com.codepath.apps.mytwitterapp.TweetsAdapter;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public final static int TWEET_UPDATED = 21;
    protected ListView lvTweets;
    protected TweetsAdapter tweetsAdapter;
    protected PullToRefreshLayout pullToRefreshLayout;

    protected static final int LOAD_MORE_MODE = 1;
    protected static final int LOAD_UPDATES_MODE = 2;
    protected static final int UPDATE_MODE = 3;

    protected ArrayList<Tweet> tweetList;

    private Callbacks callbacks;

    public interface Callbacks {
        void onLoading(boolean loading);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        if (savedInstanceState != null) {
            tweetList = savedInstanceState.getParcelableArrayList(TWEET_RESULTS);

        } else {
            tweetList = new ArrayList<Tweet>();
        }

        setupUI(inflater, v);

        // If we dont have tweets try to load them now
        if(tweetList.size() == 0){
            loadTweets(LOAD_MORE_MODE);
        }

        return v;
    }

    public ArrayList<Tweet> createNewList(){
        return new ArrayList<Tweet>();
    }

    public void setupUI(LayoutInflater inflater, View v){
        lvTweets = (ListView) v.findViewById(R.id.lvTweet);
        tweetsAdapter = getAdapter();
        lvTweets.setAdapter(tweetsAdapter);
        lvTweets.setOnScrollListener(endlessScrollListener);

        pullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(onRefreshListener)
                .setup(pullToRefreshLayout);
    }

    public TweetsAdapter getAdapter(){
        return new TweetsAdapter(this, getActivity(), tweetList);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putParcelableArrayList(TWEET_RESULTS, tweetList);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if( requestCode == REQUEST_CODE) {
            //loadTweets(LOAD_UPDATES_MODE);
            Tweet tweet = data.getParcelableExtra(ComposeTweetActivity.CREATED_TWEET);
            ArrayList<Tweet> tweets = new ArrayList<Tweet>();
            tweets.add(tweet);
            updateAdapter(tweets, LOAD_UPDATES_MODE);
        } else if( requestCode == TWEET_UPDATED ){
            Tweet tweet = data.getParcelableExtra(TweeterDetailActivity.UPDATED_TWEET);
            replaceTweetOnadapter(tweet);
        }
    }

    public void replaceTweetOnadapter(Tweet tweet){
        for(int i = 0; i < tweetList.size(); i++){
            if( tweetList.get(i).equals(tweet)){
                tweetList.set(i, tweet);
                break;
            }
        }
        tweetsAdapter.notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Tweet> tweets, int mode){
        pullToRefreshLayout.setRefreshComplete();

        if(mode == UPDATE_MODE){
            tweetList.clear();
        }

        switch( mode ){
            case LOAD_MORE_MODE:
                tweetList.addAll(tweets);
                break;
            default:
                tweetList.addAll(firstTweetPosition(), tweets);
        }

        if(callbacks != null) callbacks.onLoading(false);
        tweetsAdapter.notifyDataSetChanged();
    }

    public int firstTweetPosition(){
        return 0;
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
        if(callbacks != null) callbacks.onLoading(true);
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
                    ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets,  MyTwitterApp.getPreferences().getCurrentUserId());
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
                updateAdapter(tweets, mode);
            }
        });
    };

    /* Abstract methods */
    protected abstract void loadTweetsFromApi(int mode);
    protected abstract void loadTweetsFromDb();

    public View.OnClickListener onClickRetweetListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            callbacks.onLoading(true);

            final Tweet tweet = (Tweet) v.getTag();

            MyTwitterApp.getRestClient().reTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                @Override
                public void onFailure(Throwable error, String details) {
                    super.onFailure(error, details);
                    Log.d("DEBUG", "Failed retweet!: " + details);
                    Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(R.string.retweet_failure)
                            , Toast.LENGTH_SHORT).show();
                    callbacks.onLoading(false);
                }

                @Override
                public void onSuccess(JSONObject jsonTweet) {
                    Tweet t = findInTweetList(tweet.getTweetId());
                    t.setRetweeted(true);
                    t.setTweetsCount(t.getTweetsCount() + 1);
                    tweetsAdapter.notifyDataSetChanged();
                    t.save();
                    callbacks.onLoading(false);
                }
            });
        }
    };

    public View.OnClickListener onClickFavoriteListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            callbacks.onLoading(true);

            final Tweet tweet = (Tweet) v.getTag();

            MyTwitterApp.getRestClient().favorite(tweet.getTweetId(), !tweet.isFavorited(), new JsonHttpResponseHandler() {
                @Override
                public void onFailure(Throwable error, String details) {
                    super.onFailure(error, details);
                    Log.d("DEBUG", "Failed favorite!: " + details);
                    Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(R.string.favorite_failure)
                            , Toast.LENGTH_SHORT).show();
                    callbacks.onLoading(false);
                }

                @Override
                public void onSuccess(JSONObject jsonTweet) {
                    Tweet t = findInTweetList(tweet.getTweetId());
                    t.setFavorited(!t.isFavorited());
                    tweetsAdapter.notifyDataSetChanged();
                    tweet.save();
                    callbacks.onLoading(false);
                }
            });
        }
    };

    private Tweet findInTweetList( long tweetId ){
        for(Tweet tweet: tweetList){
            if( tweet.getTweetId() == tweetId ){
                return tweet;
            }
        }
        return null;
    }
}
