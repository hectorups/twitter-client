package com.codepath.apps.mytwitterapp;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class TweetsAdapter extends ArrayAdapter<Tweet>{

    public TweetsAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tweetView = convertView;

        if(tweetView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            tweetView = inflater.inflate(R.layout.tweet_item, null);
        }

        final Tweet t = getItem(position);

        // Author Avatar
        ImageView ivAuthorAvatar = (ImageView)tweetView.findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(t.getUser().getProfileImageUrl(), ivAuthorAvatar);

        ivAuthorAvatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("adf", "go to profile " + t.getUser().getUserId());
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra(ProfileActivity.EXTRA_USER, t.getUser());
                getContext().startActivity(i);
            }
        });


        // Author name
        TextView tvAuthorName = (TextView)tweetView.findViewById(R.id.tvName);
        String authorName = t.getUser().getName().trim();
        tvAuthorName.setText(Html.fromHtml(authorName));

        ivAuthorAvatar.setTag(authorName);

        // Author ScreenName
        TextView tvAuthorScreenName = (TextView)tweetView.findViewById(R.id.tvScreenName);
        String screenName = t.getUser().getScreenName().trim();
        tvAuthorScreenName.setText("@" + screenName);

        // Tweet Text
        TextView tvTextView = (TextView)tweetView.findViewById(R.id.tvBody);
        tvTextView.setText(t.getBody());

        // Time
        TextView tvCreatedAt = (TextView)tweetView.findViewById(R.id.tvCreatedAt);
        String relativeDate = (String) DateUtils.getRelativeDateTimeString(
                getContext(),
                t.getCreatedAt().getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0);
        String[] parts = relativeDate.split(",");
        tvCreatedAt.setText(parts[0].trim());



        return tweetView;
    }




}