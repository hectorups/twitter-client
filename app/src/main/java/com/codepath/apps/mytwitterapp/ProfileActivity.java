package com.codepath.apps.mytwitterapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.codepath.apps.mytwitterapp.fragments.ProfileFragment;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ProfileFragment())
                    .commit();
        }
    }






}
