package com.codepath.apps.mytwitterapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadProfileInfo();
    }

    protected void loadProfileInfo(){
        MyTwitterApp.getRestClient().getMyInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject json){
                User u = User.fromJson(json);
                getActionBar().setTitle("@" + u.getName());

            }
        });
    }






}
