package com.codepath.apps.mytwitterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.codepath.apps.mytwitterapp.fragments.TweeterDetailFragment;
import com.codepath.apps.mytwitterapp.models.Tweet;

public class TweeterDetailActivity extends ActionBarActivity {
    public static final String TWEET_EXTRA = "tweet_extra";
    public static final String UPDATED_TWEET = "updated_tweet";
    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_tweeter_detail);

        tweet = getIntent().getParcelableExtra(TWEET_EXTRA);

        Fragment fragment = TweeterDetailFragment.newInstance(tweet, new TweeterDetailFragment.TweetDetailsCallbacks() {
            @Override
            public void tweetUpdated(Tweet tweet) {
                TweeterDetailActivity.this.tweet = tweet;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent();
        i.putExtra(UPDATED_TWEET, tweet);
        setResult(Activity.RESULT_OK, i);
        super.onBackPressed();
    }


}
