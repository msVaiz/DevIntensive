package com.softdesing.devintensive.data.managers;

import com.softdesing.devintensive.data.network.RestService;
import com.softdesing.devintensive.data.network.ServiceGenerator;
import com.softdesing.devintensive.data.network.req.UserLoginReq;
import com.softdesing.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;

public class DataManager {

    private static DataManager INSTANCE = null;

    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    public DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getINSTANCE(){
        if (INSTANCE == null){
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    //region ===================== Network ================================================

    public Call<UserModelRes> loginUser (UserLoginReq userLoginReq){
        return mRestService.loginUser(userLoginReq);
    }

    //end region

    //region ===================== Database ================================================



    //end region
}
