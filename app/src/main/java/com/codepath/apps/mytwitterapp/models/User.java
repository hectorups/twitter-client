package com.codepath.apps.mytwitterapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONObject;

/**
 * Created by hectormonserrate on 18/01/14.
 */
@Table(name = "users")
public class User extends Model {

    @Column(name = "name")
    private String name;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "screen_nme")
    private String screenName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_background_image_url")
    private String profileBackgroundImageUrl;

    @Column(name = "number_tweets")
    private int numberTweets;

    @Column(name = "followers_count")
    private int followersCount;

    @Column(name = "friends_count")
    private int friendsCount;


    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public int getNumberTweets() {
        return numberTweets;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public Long getUserId(){return userId;}

    public User(){super();}

    public static User fromJson(JSONObject json){
        User u = null;

        try{
            Long userId = json.getLong("id");

            u = findById(json.getLong("id"));

            if(u == null) u = new User();

            u.name = json.getString("name");

            u.userId = userId;

            u.screenName = json.getString("screen_name");

            u.profileImageUrl = json.getString("profile_image_url");

            u.profileBackgroundImageUrl = json.getString("profile_background_image_url");

            u.numberTweets = json.getInt("number_tweets");

            u.followersCount = json.getInt("followers_count");

            u.friendsCount = json.getInt("friends_count");

        } catch(Exception e){
            e.printStackTrace();
        }

        return u;
    }

    public static User findById(Long userId) {
        return new Select()
                .from(User.class)
                .where("user_id = ?", userId)
                .executeSingle();
    }
}
