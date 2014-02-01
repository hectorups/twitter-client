package com.codepath.apps.mytwitterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hectormonserrate on 18/01/14.
 */
@Table(name = "tweets")
public class Tweet extends Model implements Parcelable {

    @Column(name = "body")
    private String body;

    @Column(name = "tweet_id")
    private Long tweetId;


    @Column(name = "User")
    private User user;

    @Column(name = "created_at", index = true)
    private Date createdAt;

    public User getUser(){
        return user;
    }

    public String getBody(){
        return body;
    }


    public Date getCreatedAt(){ return createdAt; }

    public Long getTweetId(){
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
            t.setDateFromString(jsonObject.getString("created_at"));
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

    private void setDateFromString(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        sf.setLenient(true);
        try{
            this.createdAt = sf.parse(date);
        } catch (ParseException e) { e.printStackTrace(); }
    }

    /*
     *  Parcelable Overrides
     */

    protected Tweet(Parcel in) {
        body = in.readString();
        tweetId = in.readLong();
        user = in.readParcelable(User.class.getClassLoader());
        createdAt = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeLong(tweetId);
        dest.writeParcelable(user, flags);
        dest.writeLong(createdAt.getTime());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

}