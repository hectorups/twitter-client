package com.codepath.apps.mytwitterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
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

    @Column(name="tweet_id", unique=true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long tweetId;

    @Column(name = "User")
    private User user;

    @Column(name = "created_at", index = true)
    private Date createdAt;

    @Column(name = "my_mention")
    private boolean myMention;

    @Column(name = "retweeted")
    private boolean retweeted;

    @Column(name = "favorited")
    private boolean favorited;

    @Column(name = "tweets_count")
    private int tweetsCount;

    public int getTweetsCount() {
        return tweetsCount;
    }

    public void setTweetsCount(int tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    public User getUser(){
        return user;
    }

    public String getBody(){
        return body;
    }


    public Date getCreatedAt(){ return createdAt; }

    public long getTweetId(){
        return tweetId;
    }

    public Tweet(){super();}

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean isFavorited() { return favorited; }

    public void setFavorited(boolean favorited) { this.favorited = favorited; }

    public static Tweet fromJson(JSONObject jsonObject, long currentuserId){
        Tweet t = null;
        try{
            long tweetId = jsonObject.getLong("id");
            t = Tweet.findById(tweetId);
            if(t == null) t = new Tweet();
            t.user = User.fromJson(jsonObject.getJSONObject("user"));
            t.body = jsonObject.getString("text");
            t.tweetId = tweetId;
            if(currentuserId != 0){
                t.myMention = isUserMentioned(jsonObject.getJSONObject("entities").getJSONArray("user_mentions"), currentuserId);
            }
            t.setDateFromString(jsonObject.getString("created_at"));
            t.retweeted = jsonObject.getBoolean("retweeted");
            t.favorited = jsonObject.getBoolean("favorited");
            t.tweetsCount = jsonObject.getInt("retweet_count");
        } catch (JSONException e){
            e.printStackTrace();
        }

        return  t;
    }

    private static boolean isUserMentioned(JSONArray jsonArray, long userId){
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject mentionJson;
            try{
                mentionJson = jsonArray.getJSONObject(i);
                if(mentionJson.getLong("id") == userId){
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public void saveWithUser(){
        user.save();
        this.save();
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray, long currentuserId){
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject tweetJson = null;
            try{
                tweetJson = jsonArray.getJSONObject(i);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson, currentuserId);
            if(tweet != null){
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    public static void saveTweets(ArrayList<Tweet> tweets){
        ActiveAndroid.beginTransaction();
        for(Tweet tweet: tweets){
            tweet.saveWithUser();
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
    }


    public static ArrayList<Tweet> recentTweets(int limit) {
        List<Tweet> tweetList = new Select().from(Tweet.class).orderBy("tweet_id DESC").limit(limit).execute();
        return new ArrayList<Tweet>(tweetList);
    }

    public static ArrayList<Tweet> recentTweetsWithMentions(int limit) {
        List<Tweet> tweetList = new Select().from(Tweet.class).where("my_mention = ?", 1).orderBy("tweet_id DESC").limit(limit).execute();
        return new ArrayList<Tweet>(tweetList);
    }

    public static Tweet findById(Long tweetId) {
        return new Select()
                .from(Tweet.class)
                .where("tweet_id = ?", tweetId)
                .executeSingle();
    }

    public static void deleteAll(){
        new Delete().from(Tweet.class).execute();
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
        myMention = in.readInt() == 0 ? false : true;
        retweeted = in.readInt() == 0 ? false : true;
        favorited = in.readInt() == 0 ? false : true;
        tweetsCount = in.readInt();
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
        dest.writeInt(myMention ? 1 : 0);
        dest.writeInt(retweeted ? 1 : 0);
        dest.writeInt(favorited ? 1 : 0);
        dest.writeInt(tweetsCount);
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

    public boolean equals(Tweet b){
        return this.getTweetId() == b.getTweetId();
    }

}