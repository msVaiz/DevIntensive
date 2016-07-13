package com.softdesing.devintensive.data.network.interceptors;


import com.softdesing.devintensive.data.managers.DataManager;
import com.softdesing.devintensive.data.managers.PreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {

        PreferencesManager pm = DataManager.getINSTANCE().getPreferencesManager();

        Request original = chain.request();

        Request.Builder requestBulder = original.newBuilder()
                .header("X-Access-Token", pm.getAuthToken())
                .header("Request-User-Id", pm.getUserId())
                .header("User-Agent", "DevIntensiveApp");

        Request request = requestBulder.build();

        return chain.proceed(request);
    }
}
