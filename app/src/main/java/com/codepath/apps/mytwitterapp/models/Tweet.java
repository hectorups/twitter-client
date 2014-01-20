package com.codepath.apps.mytwitterapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hectormonserrate on 18/01/14.
 */
@Table(name = "tweets")
public class Tweet extends Model {

    @Column(name = "body")
    private String body;

    @Column(name = "tweet_id")
    private Long tweetId;

    @Column(name = "is_favored")
    private boolean isFavored;

    @Column(name = "User")
    private User user;


    public User getUser(){
        return user;
    }

    public String getBody(){
        return body;
    }

    public boolean IsFavored(){
        return isFavored;
    }

    public Long tweetId(){
        return tweetId;
    }

    public Tweet(){super();}

    public static Tweet fromJson(JSONObject jsonObject){
        Tweet t = null;
        try{
            Long tweetId = jsonObject.getLong("id");
            t = Tweet.findById(tweetId);
            if(t == null) t = new Tweet();
            t.user = User.fromJson(jsonObject.getJSONObject("user"));
            t.body = jsonObject.getString("text");
            t.tweetId = tweetId;
            t.isFavored = jsonObject.getBoolean("is_favored");
        } catch (JSONException e){
            e.printStackTrace();
        }

        return  t;
    }

    public void saveWithUser(){
        user.save();
        this.save();
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject tweetJson = null;
            try{
                tweetJson = jsonArray.getJSONObject(i);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson);
            if(tweet != null){
                tweet.saveWithUser();
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    public static ArrayList<Tweet> recentTweets(int limit) {
        List<Tweet> tweetList = new Select().from(Tweet.class).orderBy("tweet_id DESC").limit(limit).execute();
        return new ArrayList<Tweet>(tweetList);
    }

    public static Tweet findById(Long tweetId) {
        return new Select()
                .from(Tweet.class)
                .where("tweet_id = ?", tweetId)
                .executeSingle();
    }
}
