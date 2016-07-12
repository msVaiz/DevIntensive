package com.softdesing.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.data.managers.DataManager;
import com.softdesing.devintensive.data.network.req.UserLoginReq;
import com.softdesing.devintensive.data.network.res.UserModelRes;
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

    static final String TAG = ConstantManager.TAG_PREFIX + "AuthorizationActivity";
    private DataManager mDataManager;

    @BindView(R.id.auth_button) Button mSignIn;
    @BindView(R.id.remember_txt) TextView mRememberPassword;
    @BindView(R.id.login_email_et) EditText mLogin;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mDataManager = DataManager.getINSTANCE();
        ButterKnife.bind(this);

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    /**
     * метод обрабатывает нажатие на элементы View
     * @param view - объект на которое произошло нажатие
     */
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

    /**
     *  вспомогательный метод для показа сообщения пользователю
     *  в материальном стиле
     * @param message - сообщение для пользователя
     */
    private void showSnackBar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     *  метод перенаправляет пользователя на страничку
     *  восстановления пароля
     */
    private void rememberPassword(){
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel){

        //showSnackBar(response.body().getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        saveUserValues(userModel);
        saveUserProfileData(userModel);
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn(){

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
                    //// TODO: 7/11/2016 обработать ошибки
                }
            });
        } else {
            showSnackBar("Сеть на данный момент недоступна, попробуйте позже");
        }
    }

    private void saveUserValues(UserModelRes userModel){
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserProfileData(UserModelRes userModel){

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
}
