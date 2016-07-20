package com.softdesing.devintensive.data.managers;

import android.content.Context;

import com.softdesing.devintensive.data.network.PicassoCache;
import com.softdesing.devintensive.data.network.RestService;
import com.softdesing.devintensive.data.network.ServiceGenerator;
import com.softdesing.devintensive.data.network.req.UserLoginReq;
import com.softdesing.devintensive.data.network.res.UploadPhotoRes;
import com.softdesing.devintensive.data.network.res.UserListRes;
import com.softdesing.devintensive.data.network.res.UserModelRes;
import com.softdesing.devintensive.data.storage.models.DaoSession;
import com.softdesing.devintensive.data.storage.models.User;
import com.softdesing.devintensive.data.storage.models.UserDao;
import com.softdesing.devintensive.utils.DevintensiveApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;

public class DataManager {

    private static DataManager INSTANCE = null;
    private Picasso mPicasso;
    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;
    private DaoSession mDaoSession;

    public DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevintensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCache(mContext).getPicassoInstance();
        this.mDaoSession = DevintensiveApplication.getDaoSession();
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

    public Picasso getPicasso() {
        return mPicasso;
    }

    //region ===================== Network ================================================

    public Call<UserModelRes> loginUser (UserLoginReq userLoginReq){
        return mRestService.loginUser(userLoginReq);
    }

    public Call<UserModelRes> loginWithToken (String userId){
        return mRestService.loginWithToken(userId);
    }

    public Call<UploadPhotoRes> uploadPhoto (String userId, MultipartBody.Part file){
        return mRestService.uploadPhoto(userId, file);
    }

    public Call<UserListRes> getUserListFromNetwork(){
        return mRestService.getUserList();
    }

    //end region

    //region ===================== Database ================================================


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

//    public List<User> getUserListFromDb(){
//
//        List<User> userList = new ArrayList<>();
//
//        try {
//            userList = mDaoSession.queryBuilder(User.class)
//                    .where(UserDao.Properties.CodeLines.gt(0))
//                    .orderDesc(UserDao.Properties.CodeLines)
//                    .build()
//                    .list();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return userList;
//    }

    public List<User> getUserListByName(String query){

        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0), UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }

    //end region
}
