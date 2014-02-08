package com.codepath.apps.mytwitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.apps.mytwitterapp.models.User;
import com.codepath.oauth.OAuthLoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {
    private final static String TAG = "loginactivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	
	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        if( MyTwitterApp.getPreferences().getCurrentUserId() != 0){
            goToTimeline();
            return;
        }

        setProgressBarIndeterminateVisibility(true);
        MyTwitterApp.getRestClient().getMyInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                User u = User.fromJson(json);
                MyTwitterApp.getPreferences().setUserId(u);
                goToTimeline();
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onFailure(Throwable  e, JSONObject error) {
                Log.e(TAG, e.toString());
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private void goToTimeline(){
        Intent i = new Intent(LoginActivity.this, TimeLineActivity.class);
        startActivity(i);
    }
    
    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }
    
    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
    }

}
