package com.softdesing.devintensive.ui.activities;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.softdesing.devintensive.R;
import com.softdesing.devintensive.utils.ConstantManager;

public class BaseActivity extends AppCompatActivity{

    static final String TAG = ConstantManager.TAG_PREFIX + "BaseActivity";
    protected ProgressDialog mProgressDialog;

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */

    public void showProgress(){
        Log.d(TAG, "Show progress");

        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this, R.style.custom_dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.login_bg));
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        } else {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        }
    }

    public void hideProgress(){
        Log.d(TAG, "Hide progress");

        if (mProgressDialog != null){
            if (mProgressDialog.isShowing()){
                mProgressDialog.hide();
            }
        }
    }

    public void showErrors(String message, Exception error){
        showToast(message);
        Log.e(TAG, String.valueOf(error));
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
