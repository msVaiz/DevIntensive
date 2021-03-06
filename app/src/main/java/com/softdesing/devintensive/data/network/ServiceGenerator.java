package com.softdesing.devintensive.data.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.softdesing.devintensive.data.network.interceptors.HeaderInterceptor;
import com.softdesing.devintensive.utils.AppConfig;
import com.softdesing.devintensive.utils.DevintensiveApplication;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder =
            new Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    /**
     * метод создает rest сервис
     * @param serviceClass - интерфейс который реализует retrofit запросы
     */
    public static <S> S createService(Class<S> serviceClass){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(new HeaderInterceptor());
        httpClient.addInterceptor(logging);
        httpClient.cache(new Cache(DevintensiveApplication.getContext().getCacheDir(), Integer.MAX_VALUE));
        httpClient.addNetworkInterceptor(new StethoInterceptor());

        Retrofit retrofit = sBuilder
                .client(httpClient.build())
                .build();

        return retrofit.create(serviceClass);
    }
}
