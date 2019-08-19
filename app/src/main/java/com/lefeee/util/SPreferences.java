package com.lefeee.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPreferences {

    private static final String SP_NAME = "HxEducation";

    private static final String LOGIN_STATUS = "login_status";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private Context mContext;
    private SharedPreferences mSp;

    private static SPreferences mInstance;


    private SPreferences(Context context) {
        this.mContext = context;
        mSp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPreferences get(Context context) {
        if (null == mInstance) {
            synchronized (Object.class) {
                if (null == mInstance) {
                    mInstance = new SPreferences(context);
                }
            }
        } else {
            mInstance = new SPreferences(context);
        }
        return mInstance;
    }



    public boolean saveLoginStatus(int loginStatus) {
        SharedPreferences.Editor editor = mSp.edit();
        return editor.putInt(LOGIN_STATUS, loginStatus).commit();
    }

    public int getLoginStatus(){
        return mSp.getInt(LOGIN_STATUS, 0);             //0未登录 1已登陆
    }

    public boolean saveUsername(String username) {
        SharedPreferences.Editor editor = mSp.edit();
        return editor.putString(USERNAME, username).commit();
    }

    public String getUsername() {
        return mSp.getString(USERNAME, null);
    }

    public boolean savePassword(String password) {
        SharedPreferences.Editor editor = mSp.edit();
        return editor.putString(PASSWORD, password).commit();
    }

    public String getPassword() {
        return mSp.getString(PASSWORD, null);
    }




}
