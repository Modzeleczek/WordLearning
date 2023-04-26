package com.modzel.wordlearning.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommonRetrofit {
    private static OkHttpClient httpClient;
    static {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    protected Retrofit createRetrofit(String apiBaseUrl) {
        return new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }
}
