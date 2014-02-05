package com.codepath.apps.mytwitterapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mytwitterapp.MyTwitterApp;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

/**
 * Created by hectormonserrate on 02/02/14.
 */
public class ProfileInfoFragment extends Fragment {
    private static final String EXTRA_USER = "user";

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvDescription;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private ImageView ivBgProfile;

    private User user;

    public static ProfileInfoFragment newInstance(User user){
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        ProfileInfoFragment fragment = new ProfileInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        user = getArguments().getParcelable(EXTRA_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_info, parent, false);

        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvFollowers = (TextView) v.findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) v.findViewById(R.id.tvFollowing);
        ivBgProfile = (ImageView) v.findViewById(R.id.ivBgProfile);


        loadProfileInfo();

        return v;
    }

    protected void loadProfileInfo(){
        if(user != null){
            loadOtherProfileInfo();
        } else {
            loadMyProfileInfo();
        }
    }

    protected void loadOtherProfileInfo(){
        MyTwitterApp.getRestClient().getAccountInformation(user.getUserId(), user.getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                User u = User.fromJson(json);
                getActivity().getActionBar().setTitle("@" + u.getName());
                populateProfileView(u);
            }
        });
    }

    protected void loadMyProfileInfo(){
        MyTwitterApp.getRestClient().getMyInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject json){
                User u = User.fromJson(json);
                getActivity().getActionBar().setTitle("@" + u.getName());
                populateProfileView(u);
            }
        });
    }

    private void populateProfileView(User u){
        tvName.setText(u.getName());
        tvDescription.setText(u.getScreenName());
        tvFollowers.setText(u.getFollowersCount() + "");
        tvFollowing.setText(u.getFriendsCount() + "");
        tvDescription.setText(u.getTagLine());
        ImageLoader.getInstance().displayImage(u.getProfileImageUrl(), ivProfile);

        if(u.getProfileBackgroundImageUrl() != null){
            ImageLoader.getInstance().displayImage(u.getProfileBannerUrl(), ivBgProfile);
        }
    }

}
