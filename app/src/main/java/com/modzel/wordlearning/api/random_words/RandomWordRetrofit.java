package com.modzel.wordlearning.api.random_words;

import com.modzel.wordlearning.api.CommonRetrofit;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class RandomWordRetrofit extends CommonRetrofit {
    private static final String RANDOM_WORD_API_URL = "https://random-word-api.herokuapp.com/";

    private static Retrofit getInstance() {
        return CommonRetrofit.getInstance(RANDOM_WORD_API_URL);
    }

    public static List<String> getRandomWords(int count) {
        RandomWordService service = getInstance().create(RandomWordService.class);
        Call<List<String>> apiCall = service.getWords(count);
        try {
            return apiCall.execute().body(); // Download synchronously.
        } catch (IOException e) {
            return null;
        }
    }
}
