package com.softdesing.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesing.devintensive.utils.ConstantManager;
import com.softdesing.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_MAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_BIO_KEY
    };

    private static final String[] USER_VALUES = {
            ConstantManager.USER_RATING_VALUE,
            ConstantManager.USER_CODE_LINES_VALUE,
            ConstantManager.USER_PROJECTS_VALUE
    };

    public PreferencesManager(){
        this.mSharedPreferences = DevintensiveApplication.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> userFields){

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for(int i = 0; i < USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData(){

        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GIT_KEY, ""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_BIO_KEY, ""));
        return userFields;
    }

    public void saveUserPhoto(Uri uri){

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserPhoto(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_photo"));
    }

    public void saveUserProfileValues(int[] userValues){

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for(int i = 0; i < USER_VALUES.length; i++){
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    public List<String> loadUserProfileValues(){

        List<String> userValues = new ArrayList<>();
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_RATING_VALUE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_CODE_LINES_VALUE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_PROJECTS_VALUE, "0"));
        return userValues;
    }


    /**
     * метод сохранения токена авторизации, чтобы потом
     * подписывать им исходящие запросы
     * @param authToken - токен авторизации
     */
    public void saveAuthToken(String authToken){

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    /**
     * метод получения токена из SharedPreferences
     * @return - токен авторизации
     */
    public String getAuthToken(){
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "null");
    }

    /**
     * метод сохранения идентификатора пользователя
     * @param userId - идентификатор пользователя
     */
    public void saveUserId(String userId){

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    /**
     * метод получения идентификатора пользователя из SharedPreferences
     * @return - идентификатор пользователя
     */
    public String getUserId(){
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "null");
    }

    public void saveUserAvatar(Uri uri) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserAvatar() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_avatar"));
    }

    public void saveUserName (String[] name){

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_FIRST_NAME_KEY, name[0]);
        editor.putString(ConstantManager.USER_SECOND_NAME_KEY, name[1]);
        editor.apply();
    }

    public String[] loadUserName() {

        String[] name = {mSharedPreferences.getString(ConstantManager.USER_FIRST_NAME_KEY, ""),
        mSharedPreferences.getString(ConstantManager.USER_SECOND_NAME_KEY, "")};
        return name;
    }
}


