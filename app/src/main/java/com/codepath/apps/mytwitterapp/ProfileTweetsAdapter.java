package com.codepath.apps.mytwitterapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mytwitterapp.fragments.ProfileInfoFragment;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.codepath.apps.mytwitterapp.models.User;

import java.util.List;

/**
 * Created by hectormonserrate on 06/02/14.
 */
public class ProfileTweetsAdapter extends TweetsAdapter {
    User user;

    private static final int HEADER = 0;
    private static final int TWEET = 1;

    private Fragment fragment;

    public ProfileTweetsAdapter(Fragment fragment, Context context, List<Tweet> objects, User user) {
        super(context, objects);
        this.user = user;
        this.fragment = fragment;
    }

    // Return an integer representing the type by fetching the enum type ordinal
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : TWEET;
    }

    // Total number of types is the number of enum values
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( getItemViewType(position) == HEADER ){
            return getHeaderView(convertView, parent);
        } else {
            return super.getView(position, convertView, parent);
        }
    }

    public View getHeaderView(View convertView, ViewGroup parent){
        View headerView = convertView;

        if(headerView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            headerView = inflater.inflate(R.layout.profile_header_item, null);
        }

        FragmentManager fm = fragment.getChildFragmentManager();
        Fragment profileInfoFragment = fm.findFragmentById(R.id.fragmentProfileInfo);
        if(profileInfoFragment == null){
            profileInfoFragment = ProfileInfoFragment.newInstance(user);
        }

        fm.beginTransaction()
                    .replace(R.id.fragmentProfileInfo, profileInfoFragment)
                    .commit();

        return headerView;
    }


}
