package com.softdesing.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.data.managers.DataManager;
import com.softdesing.devintensive.data.network.req.UserLoginReq;
import com.softdesing.devintensive.data.network.res.UserListRes;
import com.softdesing.devintensive.data.network.res.UserModelRes;
import com.softdesing.devintensive.data.storage.models.Repository;
import com.softdesing.devintensive.data.storage.models.RepositoryDao;
import com.softdesing.devintensive.data.storage.models.User;
import com.softdesing.devintensive.data.storage.models.UserDao;
import com.softdesing.devintensive.utils.AppConfig;
import com.softdesing.devintensive.utils.ConstantManager;
import com.softdesing.devintensive.utils.NetworkStatusChecker;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends BaseActivity implements View.OnClickListener {

    static final String TAG = ConstantManager.TAG_PREFIX + "AuthActivity";

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    @BindView(R.id.auth_button) Button mSignIn;
    @BindView(R.id.remember_txt) TextView mRememberPassword;
    @BindView(R.id.login_email_et) EditText mLogin;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        ButterKnife.bind(this);
        mDataManager = DataManager.getINSTANCE();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.auth_button:
                signIn();
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
        }
    }

    private void showSnackBar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     *  метод перенаправляет пользователя на страничку
     *  восстановления пароля
     */
    private void rememberPassword(){
        Log.d(TAG, "rememberPassword");
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        Intent chosenIntent = Intent.createChooser(rememberIntent, "Что использовать?");
        startActivity(chosenIntent);
    }

    /**
     * метод вызывается при успешной авторизации
     * @param userModel
     */
    private void loginSuccess(UserModelRes userModel){
        Log.d(TAG, "loginSuccess");

        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        saveUserValues(userModel);
        saveUserProfileData(userModel);
        saveUserInDb();

        Intent loginIntent = new Intent(AuthorizationActivity.this, MainActivity.class);
        startActivity(loginIntent);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Intent loginIntent = new Intent(this, MainActivity.class);
//                Intent loginIntent = new Intent(AuthorizationActivity.this, UserListActivity.class);
//                startActivity(loginIntent);
//            }
//        }, AppConfig.START_DELAY);
    }

    private void signIn(){
        Log.d(TAG, "signIn");

        if (NetworkStatusChecker.isNetworkAvailable(this)) {

            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200){
                        loginSuccess(response.body());
                    } else if (response.code() == 404){
                        showSnackBar("Неверный логин или пароль");
                    } else {
                        showSnackBar("Что-то пошло не так!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    showSnackBar("Что-то пошло не так!");
                    t.printStackTrace();
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            showSnackBar("Сеть на данный момент недоступна, попробуйте позже");
        }
    }

    /**
     * метод сохраняет данные о рейтинге, строчках кода и проектах в SharedPreferences
     * @param userModel - тело ответа от сервера
     */
    private void saveUserValues(UserModelRes userModel){
        Log.d(TAG, "saveUserValues");

        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);

    }

    /**
     * метод сохраняет прочие данные о пользователе в SharedPreferences
     * @param userModel - тело ответа от сервера
     */

    private void saveUserProfileData(UserModelRes userModel){
        Log.d(TAG, "saveUserProfileData");

        List<String> userInfoFields = new ArrayList<>();
        userInfoFields.add(userModel.getData().getUser().getContacts().getPhone());
        userInfoFields.add(userModel.getData().getUser().getContacts().getEmail());
        userInfoFields.add(userModel.getData().getUser().getContacts().getVk());
        userInfoFields.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userInfoFields.add(userModel.getData().getUser().getPublicInfo().getBio());

        mDataManager.getPreferencesManager().saveUserProfileData(userInfoFields);
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));

        String[] name = {userModel.getData().getUser().getFirstName(), userModel.getData().getUser().getSecondName()};
        mDataManager.getPreferencesManager().saveUserName(name);
    }

    /**
     * метод запрашивает у сервера список всех пользователей и сохранятх их в БД
     */
    private void saveUserInDb(){
        Log.d(TAG, "saveUserInDb");

        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {

                        List<Repository> allRepositories = new ArrayList<Repository>();
                        List<User> allUsers = new ArrayList<User>();

                        for (UserListRes.UserData userRes : response.body().getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));
                        }

                        mRepositoryDao.insertOrReplaceInTx(allRepositories);
                        mUserDao.insertOrReplaceInTx(allUsers);

                    } else {

                        showSnackBar("Список пользователей не может быть получен");
                        Log.e(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showSnackBar("Что-то пошло не так");
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                Log.e(TAG, "onFailure() " + t.toString());
                showSnackBar("Неудалось загрузить данные с сервера");
            }
        });
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {

        final String userId = userData.getId();
        List<Repository> reposirories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()){
            reposirories.add(new Repository(repositoryRes, userId));
        }
        return reposirories;
    }
}
