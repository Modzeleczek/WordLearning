package com.modzel.wordlearning.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommonRetrofit {
    private static Retrofit INSTANCE;

    protected static Retrofit getInstance(String apiBaseUrl) {
        if (INSTANCE == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15,TimeUnit.SECONDS)
                    .build();
            INSTANCE = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return INSTANCE;
    }
}
