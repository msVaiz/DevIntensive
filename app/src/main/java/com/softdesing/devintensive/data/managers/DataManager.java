package com.softdesing.devintensive.data.managers;

import com.softdesing.devintensive.data.network.RestService;
import com.softdesing.devintensive.data.network.ServiceGenerator;
import com.softdesing.devintensive.data.network.req.UserLoginReq;
import com.softdesing.devintensive.data.network.res.UploadPhotoRes;
import com.softdesing.devintensive.data.network.res.UserModelRes;

import okhttp3.MultipartBody;
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

    public Call<UploadPhotoRes> uploadPhoto (String userId, MultipartBody.Part file){
        return mRestService.uploadPhoto(userId, file);
    }

    //end region

    //region ===================== Database ================================================



    //end region
}
