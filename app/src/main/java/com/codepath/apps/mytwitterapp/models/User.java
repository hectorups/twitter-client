package com.codepath.apps.mytwitterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONObject;

/**
 * Created by hectormonserrate on 18/01/14.
 */
@Table(name = "users")
public class User extends Model implements Parcelable {

    @Column(name = "name")
    private String name;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_banner_url")
    private String profileBannerUrl;

    @Column(name = "profile_background_image_url")
    private String profileBackgroundImageUrl;

    @Column(name = "followers_count")
    private int followersCount;

    @Column(name = "friends_count")
    private int friendsCount;

    @Column(name = "description")
    private String tagLine;

    public String getTagLine() {
        return tagLine;
    }


    public String getProfileBannerUrl(){ return profileBannerUrl; }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

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

            u.followersCount = json.getInt("followers_count");

            u.friendsCount = json.getInt("friends_count");

            u.tagLine = json.getString("description");

            if( json.has("profile_banner_url")){
                u.profileBannerUrl = json.getString("profile_banner_url");
            } else {
                u.profileBannerUrl = null;
            }

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

    /*
     *  Parcelable Overrides
     */

    protected User(Parcel in) {
        name = in.readString();
        userId = in.readLong();
        screenName = in.readString();
        profileImageUrl = in.readString();
        profileBackgroundImageUrl = in.readString();
        followersCount = in.readInt();
        friendsCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(userId);
        dest.writeString(screenName);
        dest.writeString(profileImageUrl);
        dest.writeString(profileBackgroundImageUrl);
        dest.writeInt(followersCount);
        dest.writeInt(friendsCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


}