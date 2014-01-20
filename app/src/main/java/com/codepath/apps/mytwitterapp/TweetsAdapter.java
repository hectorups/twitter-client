package com.codepath.apps.mytwitterapp;

import android.content.Context;
import android.text.Html;
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

        Tweet t = getItem(position);

        // Author Avatar
        ImageView ivAuthorAvatar = (ImageView)tweetView.findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(t.getUser().getProfileImageUrl(), ivAuthorAvatar);

        // Author name
        TextView tvAuthorName = (TextView)tweetView.findViewById(R.id.tvName);
        String authorName = t.getUser().getName();
        tvAuthorName.setText(Html.fromHtml(authorName));

        ivAuthorAvatar.setTag(authorName);

        // Tweet Text
        TextView tvTextView = (TextView)tweetView.findViewById(R.id.tvBody);
        tvTextView.setText(Html.fromHtml(t.getBody()));


        return tweetView;
    }




}
