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

    public void getHomeTimeline(int limit, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", String.valueOf(limit));
        client.get(url, params, handler);
    }

    public void updateStatus(String status, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        getClient().post(url, params, handler);
    }


}