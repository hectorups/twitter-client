package com.codepath.apps.mytwitterapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.codepath.apps.mytwitterapp.models.User;

/**
 * Created by hectormonserrate on 08/02/14.
 */
public class Preferences {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String PREF_CURRENTUSER = "pref_currentuser";

    private SharedPreferences prefs;
    private static Preferences instance;

    public static Preferences getInstance(Context appContext){
        if( instance == null){
            instance = new Preferences(appContext);
        }

        return instance;
    }

    private Preferences(Context appContext){
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public long getCurrentUserId(){
        return prefs.getLong(PREF_CURRENTUSER, 0 );
    }

    public void setUserId( User user ){
        if( user == null ){
            prefs.edit().remove(PREF_CURRENTUSER).commit();
        } else {
            prefs.edit().putLong(PREF_CURRENTUSER, user.getUserId()).commit();
        }
    }

}