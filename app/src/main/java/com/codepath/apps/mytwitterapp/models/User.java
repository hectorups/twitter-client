package com.codepath.apps.mytwitterapp.models;

import org.json.JSONObject;

/**
 * Created by hectormonserrate on 18/01/14.
 */
public class User extends BaseModel {

    public String getName(){
        return getString("name");
    }

    public long getId(){
        return getLong("id");
    }

    public String getStringName(){
        return getString("screen_name");
    }

    public String getProfileImageUrl(){
        return getString("profile_image_url");
    }

    public String getProfileBackgroundImageUrl(){
        return getString("profile_background_image_url");
    }

    public int getNumberTweets(){
        return getInt("number_tweets");
    }

    public int getFollowersCount(){
        return getInt("followers_count");
    }

    public int getFriendsCount(){
        return getInt("friends_count");
    }

    public static User fromJson(JSONObject json){
        User u = new User();

        try{
            u.jsonObject = json;
        } catch(Exception e){
            e.printStackTrace();
        }

        return u;
    }
}
