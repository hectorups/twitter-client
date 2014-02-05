package com.codepath.apps.mytwitterapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.codepath.apps.mytwitterapp.fragments.ProfileInfoFragment;
import com.codepath.apps.mytwitterapp.fragments.UserTimelineFragment;
import com.codepath.apps.mytwitterapp.models.User;

public class ProfileActivity extends ActionBarActivity {

    public static final String EXTRA_USER = "user_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_profile);

        User user = getIntent().getParcelableExtra(EXTRA_USER);

        FragmentManager fm = getSupportFragmentManager();
        Fragment profileInfoFragment = fm.findFragmentById(R.id.fragmentContainer);
        Fragment timeLineFragment = fm.findFragmentById(R.id.fragmentUserTimeline);

        if(profileInfoFragment == null){
            profileInfoFragment = ProfileInfoFragment.newInstance(user);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, profileInfoFragment)
                    .commit();
        }

        if(timeLineFragment == null){
            timeLineFragment = UserTimelineFragment.newInstance(user);
            fm.beginTransaction()
                    .add(R.id.fragmentUserTimeline, timeLineFragment)
                    .commit();
        }

    }






}
