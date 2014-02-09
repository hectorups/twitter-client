package com.codepath.apps.mytwitterapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mytwitterapp.ComposeTweetActivity;
import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.ProfileActivity;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by hectormonserrate on 04/02/14.
 */
public class TweeterDetailFragment extends Fragment {
    private static final String TWEET_EXTRA = "tweet_extra";



    private Tweet tweet;

    private ImageView ivReplyTweet;
    private ImageView ivRetweet;
    private ImageView ivFavorite;
    private TextView tvTweetsCounter;
    private LinearLayout llTweetsCounter;

    private int favoritesCount = 0;

    private TweetDetailsCallbacks tweetDetailsCallbacks;

    public static TweeterDetailFragment newInstance(Tweet tweet, TweetDetailsCallbacks callbacks){
        Bundle args = new Bundle();
        args.putParcelable(TWEET_EXTRA, tweet);

        TweeterDetailFragment fragment = new TweeterDetailFragment();
        fragment.setArguments(args);
        fragment.tweetDetailsCallbacks = callbacks;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        tweet = getArguments().getParcelable(TWEET_EXTRA);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tweeter_detail, container, false);
        
        setupUI(rootView);
        
        return rootView;
    }
    
    private void setupUI(View tweetView){
        ImageView ivAuthorAvatar = (ImageView)tweetView.findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), ivAuthorAvatar);

        tvTweetsCounter = (TextView) tweetView.findViewById(R.id.tvTweetsCounter);
        llTweetsCounter = (LinearLayout) tweetView.findViewById(R.id.llTweetsCounter);

        ivAuthorAvatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra(ProfileActivity.EXTRA_USER, tweet.getUser());
                startActivity(i);
            }
        });


        // Author name
        TextView tvAuthorName = (TextView)tweetView.findViewById(R.id.tvName);
        String authorName = tweet.getUser().getName().trim();
        tvAuthorName.setText(Html.fromHtml(authorName));

        ivAuthorAvatar.setTag(authorName);

        // Author ScreenName
        TextView tvAuthorScreenName = (TextView)tweetView.findViewById(R.id.tvScreenName);
        String screenName = tweet.getUser().getScreenName().trim();
        tvAuthorScreenName.setText("@" + screenName);

        // Tweet Text
        TextView tvTextView = (TextView)tweetView.findViewById(R.id.tvBody);
        tvTextView.setText(tweet.getBody());

        // Reply Tweet
        ivReplyTweet = (ImageView)tweetView.findViewById(R.id.ivReplyTweet);
        ivReplyTweet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ComposeTweetActivity.class);
                i.putExtra(ComposeTweetActivity.REPLY_TWEET, tweet);
                startActivity(i);
            }
        });

        // Favorite
        ivFavorite = (ImageView) tweetView.findViewById(R.id.ivFavorite);
        setFavoriteIcon();
        ivFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                favorite();
            }
        });

        // Retweet
        ivRetweet = (ImageView) tweetView.findViewById(R.id.ivReTweet);
        if( tweet.getUser().getUserId() == MyTwitterApp.getPreferences().getCurrentUserId() ){
            ivRetweet.setEnabled(false);
        }
        else if(tweet.isRetweeted()){
            ivRetweet.setImageResource(R.drawable.ic_green_retweet);
        } else {
            ivRetweet.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    showRetweet();
                }
            });
        }

        // Time
        TextView tvCreatedAt = (TextView)tweetView.findViewById(R.id.tvCreatedAt);
        tvCreatedAt.setText(new SimpleDateFormat("HH:mmaa - yy MMM dd").format(tweet.getCreatedAt()));

        // Counters
        updateCounters();
    }

    private void updateCounters(){
        // Tweets
        if( tweet.getTweetsCount() == 0){
            llTweetsCounter.setVisibility(View.GONE);

        } else {
            llTweetsCounter.setVisibility(View.VISIBLE);
            tvTweetsCounter.setText(String.valueOf(tweet.getTweetsCount()));
        }
    }


    private void reTweet(){
        getActivity().setProgressBarIndeterminateVisibility(true);

        MyTwitterApp.getRestClient().reTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error, String details) {
                super.onFailure(error, details);
                Log.d("DEBUG", "Failed retweet!: " + details);
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.retweet_failure)
                        , Toast.LENGTH_SHORT).show();
                getActivity().setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onSuccess(JSONObject jsonTweet) {
                ivRetweet.setImageResource(R.drawable.ic_green_retweet);
                ivRetweet.setOnClickListener(null);
                tweet.setRetweeted(true);
                tweet.setTweetsCount(tweet.getTweetsCount() + 1);
                tweet.save();
                tweetDetailsCallbacks.tweetUpdated(tweet);
                updateCounters();
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private void favorite(){
        getActivity().setProgressBarIndeterminateVisibility(true);

        MyTwitterApp.getRestClient().favorite(tweet.getTweetId(), !tweet.isFavorited(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error, String details) {
                super.onFailure(error, details);
                Log.d("DEBUG", "Failed favorite!: " + details);
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.favorite_failure)
                        , Toast.LENGTH_SHORT).show();
                getActivity().setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onSuccess(JSONObject jsonTweet) {
                tweet.setFavorited(!tweet.isFavorited());
                favoritesCount = tweet.isFavorited() ? 1 : -1;
                tweet.save();
                tweetDetailsCallbacks.tweetUpdated(tweet);
                setFavoriteIcon();
                updateCounters();

                animateIv(ivFavorite);

                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private void showRetweet() {
        FragmentManager fm = getChildFragmentManager();
        RetweetDialogFragment retweetDialogFragment = RetweetDialogFragment.newInstance(tweet);
        retweetDialogFragment.setTargetFragment(this, RetweetDialogFragment.RETWEET_CODE);
        retweetDialogFragment.show(fm, "fragment_edit_name");
    }

    private void setFavoriteIcon(){
        int drawable;
        if(tweet.isFavorited()){
            drawable = R.drawable.ic_favorite_on;
        } else {
            drawable = R.drawable.ic_favorite_normal;
        }

        ivFavorite.setImageResource(drawable);
    }


    public static interface TweetDetailsCallbacks {
        public void tweetUpdated(Tweet tweet);
    }

    private void animateIv(ImageView iv){
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.enlarge);
        anim.setTarget(ivFavorite);
        anim.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == RetweetDialogFragment.RETWEET_CODE){
            reTweet();
        }
    }
}
