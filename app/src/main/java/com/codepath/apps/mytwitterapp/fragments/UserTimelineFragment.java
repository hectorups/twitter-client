package com.codepath.apps.mytwitterapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.TweetsAdapter;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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

    @Override
    public void setupUI(LayoutInflater inflater, View v){
        super.setupUI(inflater, v);

        View header = inflater.inflate(R.layout.profile_header_item, null);

        lvTweets.addHeaderView(header);
        lvTweets.setHeaderDividersEnabled(true);

        FragmentManager fm = getChildFragmentManager();
        Fragment profileInfoFragment = fm.findFragmentById(R.id.fragmentProfileInfo);
        if(profileInfoFragment == null){
            profileInfoFragment = ProfileInfoFragment.newInstance(user);
            fm.beginTransaction()
                    .add(R.id.fragmentProfileInfo, profileInfoFragment)
                    .commit();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        user = getArguments().getParcelable(EXTRA_USER);
    }

    @Override
    protected void loadTweetsFromDb(){
        long userId = user != null ? user.getUserId() : MyTwitterApp.getPreferences().getCurrentUserId();
        User searchUser = User.findById(userId);
        if(searchUser == null) return;
        updateAdapter(Tweet.recentUserTweets(searchUser.getUserId(), TWEETS_PER_LOAD), UPDATE_MODE);
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

    @Override
    public TweetsAdapter getAdapter(){
        return new UserTimelineTweetsAdapter(user, this, getActivity(), tweetList);
    }

    public class UserTimelineTweetsAdapter extends TweetsAdapter {

        protected User profileUser;

        public UserTimelineTweetsAdapter(User profileUser, TweetsListFragments fragment, Context context, List<Tweet> objects) {
            super(fragment, context, objects);
            this.profileUser = profileUser;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            // Dont let go to a profile of the profile we already watch
            ImageView ivAuthorAvatar = (ImageView) v.findViewById(R.id.ivProfile);
            ivAuthorAvatar.setOnClickListener(null);

            return v;
        }

    }

}




