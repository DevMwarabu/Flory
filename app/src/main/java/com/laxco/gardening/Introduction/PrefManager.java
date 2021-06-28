package com.laxco.gardening.Introduction;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Timiiza was created by DevMwarabu johmwarabuchone@gmail.com on 1/7/21.
 * Copyright (c) 2021 Laxco Inc-Kericho All rights reserved.
 **/
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String IS_LOGGED_IN = "IsLoggedIn";

    private static final String USER_ID = "user_id";

    private static final String USERNAME = "username";

    private static final String PHONE = "phone";

    private static final String ID_NUMBER = "id_number";

    private static final String PASSWORD = "password";

    private static final String EMAIL = "email";

    private static final String AMOUNT = "amount";

    private static final String TYPE = "type";

    private static final String DAYS = "days";
    //default

    private static final String USER_ID_d = "user_id_D";

    private static final String USERNAME_d = "username_D";

    private static final String PHONE_d = "phone_D";

    private static final String ID_NUMBER_d = "id_number_D";

    private static final String PASSWORD_d = "password_D";

    private static final String EMAIL_d = "email_D";

    private static final String AMOUNT_d = "amount_D";

    private static final String TYPE_d = "type_D";

    private static final String DAYS_d = "days_D";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setIsLoggedIn(boolean isLoggedIn){
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.commit();

    }
    public void setUserId(String user_id){
        editor.putString(USER_ID, user_id);
        editor.commit();
    }
    public void savingUserDetails(String username,String phone){
        editor.putString(USERNAME, username);
        editor.putString(PHONE, phone);
        editor.commit();

    }


    public void creatingUserLoan(String user_id,String type,String amount,String days){
        editor.putString(AMOUNT, amount);
        editor.putString(TYPE, type);
        editor.putString(USER_ID_d, user_id);
        editor.putString(DAYS, days);
        editor.commit();
    }


    public void creatingDefaultUser(String user_id,String username,String id_number,String email, String phone,String password){
        editor.putString(USERNAME, username);
        editor.putString(PHONE, phone);
        editor.putString(USER_ID, user_id);
        editor.putString(ID_NUMBER, id_number);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.commit();
    }


    public void creatingUser(String user_id,String username,String id_number,String email, String phone,String password){
        editor.putString(USERNAME, username);
        editor.putString(PHONE, phone);
        editor.putString(USER_ID, user_id);
        editor.putString(ID_NUMBER, id_number);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public String getLoanAmount(){
        return pref.getString(AMOUNT,null);
    }

    public String getLoanType(){
        return pref.getString(TYPE,null);
    }

    public String getUSERNAME() {
        return pref.getString(USERNAME,null);
    }

    public String getPHONE() {
        return pref.getString(PHONE,null);
    }

    public boolean isDefaultUserExists(String email, String password){
        if (pref.getString(EMAIL,null).equals(email) && pref.getString(PASSWORD,null).equals(password))
            return true;
        else
            return false;
    }

    public boolean isUserExists(String email,String password){
        if (pref.getString(EMAIL,null).equals(email) && pref.getString(PASSWORD,null).equals(password))
            return true;
        else
            return false;
    }

    public boolean isUserLoanExists(String user_id){
        if (pref.getString(USER_ID_d,null).equals(user_id) && !TextUtils.isEmpty(pref.getString(USER_ID_d,null)))
            return true;
        else
            return false;
    }
    public String getUserId(){
        return pref.getString(USER_ID, null);
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
