package com.codepath.apps.mytwitterapp.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.TweetsAdapter;
import com.codepath.apps.mytwitterapp.models.Tweet;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by hectormonserrate on 26/01/14.
 */
public abstract class TweetsListFragments extends Fragment implements OnRefreshListener {
    protected String TAG = "TweetsListFragments";
    protected static int TWEETS_PER_LOAD = 25;
    private ListView lvTweets;
    protected TweetsAdapter tweetsAdapter;

    protected PullToRefreshLayout pullToRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweet);

        tweetsAdapter = new TweetsAdapter(getActivity(), new ArrayList<Tweet>());
        lvTweets.setAdapter(tweetsAdapter);


        pullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(pullToRefreshLayout);


        return v;
    }




    public void updateAdaptor(ArrayList<Tweet> tweets){
        for(Tweet t: tweets){
            tweetsAdapter.add(t);
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

    @Override
    public void onRefreshStarted(View view) {
        refresh();
    }

    public abstract void refresh();


}
