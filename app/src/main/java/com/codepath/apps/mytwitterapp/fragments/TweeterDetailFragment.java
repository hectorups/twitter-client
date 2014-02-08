package com.codepath.apps.mytwitterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        if(tweet.isRetweeted()){
            ivRetweet.setImageResource(R.drawable.ic_green_retweet);
        } else {
            ivRetweet.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    reTweet();
                }
            });
        }

        // Time
        TextView tvCreatedAt = (TextView)tweetView.findViewById(R.id.tvCreatedAt);
        tvCreatedAt.setText(new SimpleDateFormat("HH:mmaa - yy MMM dd").format(tweet.getCreatedAt()));
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
                tweet.save();
                tweetDetailsCallbacks.tweetUpdated(tweet);

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
                tweet.save();
                tweetDetailsCallbacks.tweetUpdated(tweet);
                setFavoriteIcon();

                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        });
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
}
