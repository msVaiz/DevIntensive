package com.softdesing.devintensive.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.data.managers.DataManager;
import com.softdesing.devintensive.data.network.res.UserListRes;
import com.softdesing.devintensive.data.storage.models.User;
import com.softdesing.devintensive.data.storage.models.UserDTO;
import com.softdesing.devintensive.ui.adapters.UsersAdapter;
import com.softdesing.devintensive.utils.ConstantManager;
import com.softdesing.devintensive.utils.TransformToCircle;
import com.squareup.picasso.Picasso;

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
    private List<User> mUsers;
    private MenuItem mSearchItem;
    private String mQuery;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Log.d(TAG, "onCreate");

        ButterKnife.bind(this);
        mDataManager = DataManager.getINSTANCE();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mHandler = new Handler();
        
        setupToolbar();
        setupDrawer();
        loadUsersFromDb();
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

    private void loadUsersFromDb() {
        Log.d(TAG, "loadUsersFromDb");

        if (mDataManager.getUserListFromDb().size() == 0){
            showSnackbar("Список пользователей не может быть загружен");
        } else {

            showUsers(mDataManager.getUserListFromDb());
        }

//        showProgress();
//        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
//        call.enqueue(new Callback<UserListRes>() {
//            @Override
//            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
//                Log.d(TAG, "onResponse() SUCCSESS");
//                try {
//                    mUsers = response.body().getData();
//                    mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
//                        @Override
//                        public void onUserItemClickListener(int position) {
//                            UserDTO userDTO = new UserDTO(mUsers.get(position));
//
//                            Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
//                            profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
//                            startActivity(profileIntent);
//                        }
//                    });
//                    mRecyclerView.setAdapter(mUsersAdapter);
//                    hideProgress();
//                } catch (NullPointerException e) {
//                    hideProgress();
//                    Log.e(TAG, e.toString());
//                    showSnackbar("Что-то пошло не так");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserListRes> call, Throwable t) {
//                Log.e(TAG, "onFailure() " + t.toString());
//                hideProgress();
//                showSnackbar("Неудалось загрузить данные с сервера");
//            }
//        });
    }

    private void setupDrawer() {
        Log.d(TAG, "setupDrawer");

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.team_menu:
                        mNavigationDrawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.user_profile_menu:
                        mNavigationDrawer.closeDrawer(GravityCompat.START);
                        finish();
                        break;
                }
                return false;
            }
        });
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        //View headerView = navigationView.getHeaderView(0);
        //Object a = headerView.findViewById(R.id.user_ava);
        ImageView mUserAvatar = (ImageView) headerView.findViewById(R.id.user_ava);
        TextView mUserName = (TextView) headerView.findViewById(R.id.user_name_txt);
        TextView mUserEmail = (TextView) headerView.findViewById(R.id.user_email_txt);

        String temp = mDataManager.getPreferencesManager().loadUserName()[0] + " " + mDataManager.getPreferencesManager().loadUserName()[1];
        mUserName.setText(temp);
        mUserEmail.setText(mDataManager.getPreferencesManager().loadUserProfileData().get(1));

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .transform(new TransformToCircle())
                .into(mUserAvatar);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        mSearchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint("Введите имя пользователя");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                showUserByQuery(s);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void showUsers(List<User> users){
        Log.d(TAG, "showUsers");

        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDTO userDTO = new UserDTO(mUsers.get(position));

                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);
            }
        });
        mRecyclerView.swapAdapter(mUsersAdapter, false);
    }

    private void showUserByQuery(String query) {

        mQuery = query;
        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {
                showUsers(mDataManager.getUserListByName(mQuery));
            }
        };

        mHandler.removeCallbacks(searchUsers);
        if (query.isEmpty()){
            mHandler.postDelayed(searchUsers, 0);
        } else {
            mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_DELAY);
        }
    }
}
