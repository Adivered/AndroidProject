package com.example.website;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class UserSession {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final String PREFER_NAME = "Reg";
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String IS_USER_LOGIN_KNISA = "IsUserLoggedInKnisa";
    public static final String IS_USER_SENT_LOG = "IsUserSentLog";
    public static final String KEY_FULLNAME = "KEY_FULLNAME";
    public static final String RANK = "RANK";
    public static final String PASSWORD = "PASSWORD";
    public static final String SAVEME = "SAVE";



    public UserSession(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String username, String name, String rank,String pass, Boolean saveMe){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_NAME, username);
        editor.putString(PASSWORD, pass);
        editor.putString(KEY_FULLNAME, name);
        editor.putString(RANK, rank);
        editor.putBoolean(IS_USER_LOGIN_KNISA, false);
        editor.putBoolean(IS_USER_SENT_LOG, false);
        editor.putBoolean(SAVEME, saveMe);
        editor.commit();
    }

    public void createUserKnisaSession(){
        editor.putBoolean(IS_USER_LOGIN_KNISA, true);
        editor.commit();
    }

    public void setRegisterPhoneLog(){
        editor.putBoolean(IS_USER_SENT_LOG, true);
        editor.commit();
    }


    public boolean checkLogin(){
        if(!this.isUserLoggedIn()){
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }

    public boolean checkKnisaLogin(){
        if(!this.isUserLoggedInKnisa()){
            Intent i = new Intent(_context, reportLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        user.put(KEY_FULLNAME, pref.getString(KEY_FULLNAME, ""));
        return user;
    }


    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public boolean isSaved(){
        return pref.getBoolean(SAVEME, false);
    }

    public String returnUsername(){
        return pref.getString(KEY_NAME, "");
    }

    public String returnName(){
        return pref.getString(KEY_FULLNAME, "");
    }
    // Check for Knisa and logs out
    public void logoutKnisa(){
        editor.putBoolean(IS_USER_LOGIN_KNISA, false);
       // editor.putBoolean(SAVEME,false);
        editor.commit();
    }

    // Clear phonelog sent after logout
    public void clearPhoneLog(){
        editor.putBoolean(IS_USER_SENT_LOG, false);
        editor.commit();
    }

    // Check for Knisa
    public boolean isUserLoggedInKnisa(){
        return pref.getBoolean(IS_USER_LOGIN_KNISA, false);
    }


    public String returnRank(){
        return pref.getString(RANK, "");
    }
    // Check for Manager
    public boolean isManager(){
        return pref.getBoolean(RANK, false);
    }

    // Check if alraedy sent PhoneLog
    public boolean isPhoneLogSent(){
        return pref.getBoolean(IS_USER_SENT_LOG, false);
    }
}