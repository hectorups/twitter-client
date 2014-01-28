package com.codepath.apps.mytwitterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by hectormonserrate on 27/01/14.
 */
public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        loadProfileInfo();

        return rootView;
    }

    public void loadProfileInfo() {
        MyTwitterApp.getRestClient().getMyInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                User u = User.fromJson(json);
                getActivity().setTitle("@" + u.getScreenName());
            }

            @Override
            public void onFailure(Throwable  e, JSONObject error) {
                Log.e(TAG, e.toString());
            }
        });
    }
}
