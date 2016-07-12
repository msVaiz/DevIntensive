package com.softdesing.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.data.managers.DataManager;
import com.softdesing.devintensive.utils.ConstantManager;
import com.softdesing.devintensive.utils.TransformToCircle;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;

    @BindView(R.id.call_img)ImageView mCallImage;
    @BindView(R.id.send_img)ImageView mSendImage;
    @BindView(R.id.vk_img)ImageView mVkImage;
    @BindView(R.id.github_img)ImageView mGithubImage;

    @BindView(R.id.phone_et) EditText mUserPhone;
    @BindView(R.id.email_et) EditText mUserMail;
    @BindView(R.id.profile_vk_et) EditText mUserVK;
    @BindView(R.id.repository_et) EditText mUserGit;
    @BindView(R.id.about_me_et) EditText mUserBio;

    @BindView(R.id.user_info_rait_txt) TextView mUserValueRating;
    @BindView(R.id.user_info_code_lines_txt) TextView mUserValueCodeLines;
    @BindView(R.id.user_info_projects_txt) TextView mUserValueProjects;

    private List<TextView> mUserValueViews;
    private List<EditText> mUserInfoViews;
    private DataManager mDataManager;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private int mCurrentEditMode = 0;

    /**
     * метод вызывается при создании активити (после изменения конфигурации/возврата к текущей
     * активити после его уничтожения)
     *
     * в данном методе инициализируются/производятся:
     * - UI пользовательский интерфейс (статика)
     * - инициализация статических данных активити
     * - связь данных со списками (инициализация адаптеров)
     *
     * Не запускать длительные операции по работе с данными в onCreate() !
     *
     * @param savedInstanceState - объект со значениями сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate");

        ButterKnife.bind(this);
        mDataManager = DataManager.getINSTANCE();
        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLines);
        mUserValueViews.add(mUserValueProjects);

        mFloatingActionButton.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mCallImage.setOnClickListener(this);
        mSendImage.setOnClickListener(this);
        mVkImage.setOnClickListener(this);
        mGithubImage.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValues();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg)
                .into(mProfileImage);

        if(savedInstanceState == null){
            //активити запускается впервые
        } else {
            // активити уже создалось
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  метод вызывается при старте активити перед моментом того как UI станет доступен пользователю
     *  как правило в этом методе просиходит регистрация подписки на события остановка которых
     *  была произведена в onStop()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    /**
     *  метод вызывается когда активити становится доступна пользователю для взаимодействия
     *  в данном методе как правило происходит запуск анимаций/аудио/видео/запуск BroadcastReceiver
     *  необходимых для реализации UI логики/запуск выполнения потоков и т.д.
     *  метод должен быть максимально легковесным для максимальной отзывчивости UI
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    /**
     *  метод вызывается когда активити теряет фокус но остается видимой(всплытие диалогового
     *  окна/частичное перекрытие другой активити и т.д.
     *  в данном методе реализуют сохранение легковесных UI/данных/анимации/аудио/видео и т.д.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        saveUserFields();
    }

    /**
     *  метод вызывается когда активити становится невидимой для пользователя
     *  в данном методе происходит отписка от событий, остановка сложных анимаций, сложные операции
     *  по сохранению данных, прерывание запущенных потоков и т.д.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    /**
     *  метод вызывается при окончании работы активити когда это происходит системно или после
     *  вызова метода finish()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    /**
     *  метод обрабатывает нажатие на элементы View
     * @param v - объект на которое произошло нажатие
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0){
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;

            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_img:
                Intent callNumberIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUserPhone.getText().toString()));
                startActivity(callNumberIntent);
                break;

            case R.id.send_img:
                Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mUserMail.getText().toString()));
                startActivity(sendEmailIntent);
                break;

            case R.id.vk_img:
                Intent openVkProfileIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserVK.getText().toString()));
                startActivity(openVkProfileIntent);
                break;

            case R.id.github_img:
                Intent openGithubProfileIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserGit.getText().toString()));
                startActivity(openGithubProfileIntent);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackBar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        String temp = mDataManager.getPreferencesManager().loadUserName()[0] + " " + mDataManager.getPreferencesManager().loadUserName()[1];
        actionBar.setTitle(temp);

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackBar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
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


    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null){
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                    break;
                }
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }

        }
    }


    /**
     *  метод обрабатывающий нажатие системной клавиши BACK
     */
    @Override
    public void onBackPressed() {
        if(mNavigationDrawer.isDrawerOpen(GravityCompat.START)){
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 1 - режим редактирования, 0 - режим просмотра
    private void changeEditMode(int mode){
        if (mode == 1){

            mFloatingActionButton.setImageResource(R.drawable.ic_done_white_24dp);

            for (EditText userValue : mUserInfoViews){
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
            mUserInfoViews.get(0).requestFocus();
            mUserInfoViews.get(0).setSelection(mUserInfoViews.get(0).getText().length());
            mUserPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d(TAG, "beforeTextChanged");
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d(TAG, "onTextChanged");
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG, "afterTextChanged");

                    String phone = editable.toString();
                    if (CheckPhone(phone)) {
                        mUserPhone.setError(null);
                    }
                    else {
                        mUserPhone.setError("Введите в формате\n+7 XXX XXX XX XX или\n8 XXX XXX XX XX");
                    }

                }
            });

            mUserMail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    String mail = editable.toString();
                    if (CheckEmail(mail)) {
                        mUserMail.setError(null);
                    }
                    else {
                        mUserMail.setError("Введите в формате XXX@XX.XX");
                    }
                }
            });

            mUserVK.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    String vkProfile = editable.toString();
                    if (CheckVkProfile(vkProfile)) {
                        mUserVK.setError(null);
                    }
                    else {
                        mUserVK.setError("Введите в формате vk.com/XXX");
                    }
                }
            });

            mUserGit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    String githubProfile = editable.toString();
                    if (CheckGithubProfile(githubProfile)) {
                        mUserGit.setError(null);
                    }
                    else {
                        mUserGit.setError("Введите в формате github.com/XXX");
                    }
                }
            });

        } else {

            if(mUserPhone.getError() != null
                    || mUserVK.getError() != null
                    || mUserGit.getError() != null
                    || mUserMail.getError() != null)
            {
                Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_LONG).show();
                return;
            }

            mFloatingActionButton.setImageResource(R.drawable.ic_mode_edit_white_24dp);

            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_white));
                saveUserFields();
            }
        }
    }

    private boolean CheckPhone(String phone) {
        Pattern pattern = Pattern.compile("^((8|\\+7))([\\- ]?)((\\(\\s\\d{3}\\s\\))|(\\(\\d{3}\\))|(\\d{3}))([\\- ]?)(\\d{3})([\\- ]?)(\\d{2})([\\- ]?)(\\d{2})$" );
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean CheckEmail(String email) {
        Pattern pattern = Pattern.compile("^((\\w|\\.){3,}?)@(\\w{2,}?).(\\w{2,}?)$" );
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean CheckVkProfile(String vkProfile) {
        Pattern pattern = Pattern.compile("^vk.com/((\\w){3,}?)$" );
        Matcher matcher = pattern.matcher(vkProfile);
        return matcher.matches();
    }

    private boolean CheckGithubProfile(String githubProfile) {
        Pattern pattern = Pattern.compile("^github.com/((\\w)+)$" );
        Matcher matcher = pattern.matcher(githubProfile);
        return matcher.matches();
    }

    private void initUserFields(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserFields(){
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView  : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValues(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++){
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    /**
     *  метод загружает изображение из галереи
     */
    private void loadPhotoFromGallery(){
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALLERY_PICTURE );
    }

    /**
     *  метод запускает камеру для получения фото
     */
    private void loadPhotoFromCamera(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                //// TODO: 7/5/2016 обработать ошибку
            }
            if (mPhotoFile != null){
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2 ){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //// TODO: 7/5/2016 тут обрабатываем разрешение (разрешение получено)
            }
        }

        if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
            //// TODO: 7/5/2016 тут обрабатываем разрешение (разрешение получено)
        }
    }

    /**
     *  метод скрывающий Placeholder
     */
    private void hideProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * метод показывающий Placeholder
     */
    private void showProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     *  метод запрещает Toolbar сворачиваться (блокирует)
     */
    private void lockToolbar(){
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * метод разрещает Toolbar сворачиваться (разблокировывает)
     */
    private void unlockToolbar(){
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int choiceItem) {
                        switch (choiceItem){
                            case 0:
                                loadPhotoFromGallery();
                                //showSnackBar("загрузить из галереи");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                //showSnackBar("загрузить из камеры");
                                break;
                            case 2:
                                dialogInterface.cancel();
                                //showSnackBar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    /**
     *  метод размещает
     *  @param selectedImage
     */

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    /**
     *  метод открывает активность настроек приложения
     */
    public void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
}


