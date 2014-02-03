package com.codepath.apps.mytwitterapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.codepath.apps.mytwitterapp.fragments.ProfileInfoFragment;
import com.codepath.apps.mytwitterapp.models.User;

public class ProfileActivity extends ActionBarActivity {

    public static final String EXTRA_USER = "user_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User user = getIntent().getParcelableExtra(EXTRA_USER);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null){
            fragment = ProfileInfoFragment.newInstance(user);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

    }






}
