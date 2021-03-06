package com.codepath.apps.mytwitterapp;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API.  
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "Fu4VLwZovZzgfnTgoGlqw";
    public static final String REST_CONSUMER_SECRET = "1N4W4HG0LhDKmN6WCKRr35AnDAgNx9b6VpMCKTwyjm0";
    public static final String REST_CALLBACK_URL = "oauth://mytwitterapp";
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeline(int limit, long maxId, long sinceId, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if( maxId != 0 ){
            params.put("max_id", String.valueOf(maxId));
        }

        if( sinceId != 0 ){
            params.put("since_id", String.valueOf(sinceId));
        }

        params.put("exclude_replies", "true");
        params.put("count", String.valueOf(limit));
        client.get(url, params, handler);
    }

    public void updateStatus(String status, long inReplyStatusId, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        if( inReplyStatusId != 0 ){
            params.put("in_reply_to_status_id", String.valueOf(inReplyStatusId) );
        }

        params.put("status", status);
        getClient().post(url, params, handler);
    }

    public void getAccountInformation(long userId, String username, AsyncHttpResponseHandler handler) {
        String accountDetailsUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("user_id", String.valueOf(userId) );
        params.put("screen_name", username);
        client.get(accountDetailsUrl, params, handler);
    }

    //gets logged in user's info
    public void getMyInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

    public void reTweet(long tweet_id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/retweet/" + tweet_id + ".json");
        client.post(apiUrl, null, handler);
    }

    public void favorite(long tweet_id, boolean on, AsyncHttpResponseHandler handler){
        String apiUrl;
        if( on ){
            apiUrl = getApiUrl("favorites/create.json");
        } else {
            apiUrl = getApiUrl("favorites/destroy.json");
        }
        RequestParams params = new RequestParams();
        params.put("id", String.valueOf(tweet_id) );
        client.post(apiUrl, params, handler);
    }

    public void getMentions(int limit, long maxId, long sinceId, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if( maxId != 0 ){
            params.put("max_id", String.valueOf(maxId));
        }

        if( sinceId != 0 ){
            params.put("since_id", String.valueOf(sinceId));
        }

        params.put("count", String.valueOf(limit));
        client.get(url, params, handler);
    }


    public void getUserTimeline(int limit, long maxId, long sinceId, long userId, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if( maxId != 0 ){
            params.put("max_id", String.valueOf(maxId));
        }

        if( sinceId != 0 ){
            params.put("since_id", String.valueOf(sinceId));
        }

        if( userId != 0 ){
            params.put("user_id", String.valueOf(userId) );
        }

        params.put("count", String.valueOf(limit));
        client.get(url, params, handler);
    }


}