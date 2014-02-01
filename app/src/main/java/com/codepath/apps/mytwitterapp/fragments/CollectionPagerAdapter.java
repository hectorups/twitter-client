package com.codepath.apps.mytwitterapp.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hectormonserrate on 26/01/14.
 */
public class CollectionPagerAdapter extends FragmentStatePagerAdapter {

    private static HomeTimelineFragment homeTimelineFragment;
    private static MentionsFragment mentionsFragment;

    public CollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            if(homeTimelineFragment == null ){
                homeTimelineFragment =  new HomeTimelineFragment();
            }
            return homeTimelineFragment;
        } else {
            if(mentionsFragment == null ){
                mentionsFragment =  new MentionsFragment();
            }
            return mentionsFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Home";
        } else {
            return "Mentions";
        }
    }
}
