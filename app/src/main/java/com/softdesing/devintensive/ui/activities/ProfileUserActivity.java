package com.softdesing.devintensive.ui.activities;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.data.storage.models.UserDTO;
import com.softdesing.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesing.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileUserActivity extends BaseActivity {

    static final String TAG = ConstantManager.TAG_PREFIX + "ProfileUserActivity";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;
    @BindView(R.id.about_me_et) EditText mUserBio;
    @BindView(R.id.user_info_rait_txt) TextView mUserRating;
    @BindView(R.id.user_info_code_lines_txt) TextView mUserCodeLine;
    @BindView(R.id.user_info_projects_txt) TextView mUserProjects;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.repositories_list) ListView mRepoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        Log.d(TAG, "onCreate()");

        ButterKnife.bind(this);

        setupToolbar();
        initProfileData();
    }

    private void setupToolbar(){
        Log.d(TAG, "setupToolbar()");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData(){
        Log.d(TAG, "initProfileData()");

        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);

        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                EditText editText = (EditText) view.findViewById(R.id.repository_et);
                String url = editText.getText().toString();
                Uri uri = Uri.parse("http://" + url);
                Intent openRepositoryIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(openRepositoryIntent);
            }
        });

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLine.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());
        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        Point size = getUserProfileImageSize();
        Picasso.with(this)
                .load(userDTO.getPhoto())
                .resize(size.x, 0)
                //.centerCrop()
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mProfileImage);
    }
}
