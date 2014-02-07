package com.codepath.apps.mytwitterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.format.DateUtils;
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

/**
 * Created by hectormonserrate on 04/02/14.
 */
public class TweeterDetailFragment extends Fragment {
    private static final String TWEET_EXTRA = "tweet_extra";

    private Tweet tweet;

    private ImageView ivReplyTweet;
    private ImageView ivRetweet;

    public static TweeterDetailFragment newInstance(Tweet tweet){
        Bundle args = new Bundle();
        args.putParcelable(TWEET_EXTRA, tweet);

        TweeterDetailFragment fragment = new TweeterDetailFragment();
        fragment.setArguments(args);

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
                getActivity().startActivity(i);
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
                getActivity().startActivity(i);
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
        String relativeDate = (String) DateUtils.getRelativeDateTimeString(
                getActivity(),
                tweet.getCreatedAt().getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0);
        String[] parts = relativeDate.split(",");
        tvCreatedAt.setText(parts[0].trim());
    }


    private void reTweet(){
        getActivity().setProgressBarIndeterminateVisibility(true);

        MyTwitterApp.getRestClient().reTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error, String details) {
                super.onFailure(error, details);
                Log.d("DEBUG", "Failed tweet!: " + details);
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.retweet_failure)
                        , Toast.LENGTH_SHORT).show();
                getActivity().setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onSuccess(JSONObject jsonTweet) {
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.retweet_success)
                        , Toast.LENGTH_SHORT).show();
                getActivity().setProgressBarIndeterminateVisibility(false);
                ivRetweet.setImageResource(R.drawable.ic_green_retweet);
                ivRetweet.setOnClickListener(null);
                tweet.setRetweeted(true);
            }
        });
    }
}
