package com.softdesing.devintensive.ui.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.data.managers.DataManager;
import com.softdesing.devintensive.data.network.res.UserListRes;
import com.softdesing.devintensive.data.storage.models.UserDTO;
import com.softdesing.devintensive.ui.adapters.UsersAdapter;
import com.softdesing.devintensive.utils.ConstantManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity {

    static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list) RecyclerView mRecyclerView;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<UserListRes.UserData> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Log.d(TAG, "onCreate()");

        ButterKnife.bind(this);
        mDataManager = DataManager.getINSTANCE();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        
        setupToolbar();
        setupDrawer();
        loadUsers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void loadUsers() {
        Log.d(TAG, "loadUsers()");
        showProgress();
        Call<UserListRes> call = mDataManager.getUserList();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                Log.d(TAG, "onResponse() SUCCSESS");
                try {
                    mUsers = response.body().getData();
                    mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
                        @Override
                        public void onUserItemClickListener(int position) {
                            UserDTO userDTO = new UserDTO(mUsers.get(position));

                            Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                            profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                            startActivity(profileIntent);
                        }
                    });
                    mRecyclerView.setAdapter(mUsersAdapter);
                    hideProgress();
                } catch (NullPointerException e) {
                    hideProgress();
                    Log.e(TAG, e.toString());
                    showSnackbar("Что-то пошло не так");
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                Log.e(TAG, "onFailure() " + t.toString());
                hideProgress();
                showSnackbar("Неудалось загрузить данные с сервера");
            }
        });
    }

    private void setupDrawer() {
        Log.d(TAG, "setupDrawer()");
        //// TODO: 7/14/2016 реализовать переход в другую активити при клике по элементу меню в NavigatoinDrawer
    }

    private void setupToolbar() {
        Log.d(TAG, "setupToolbar()");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
