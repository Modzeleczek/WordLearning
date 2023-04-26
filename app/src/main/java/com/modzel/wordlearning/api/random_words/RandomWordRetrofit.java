package com.modzel.wordlearning.api.random_words;

import com.modzel.wordlearning.api.CommonRetrofit;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class RandomWordRetrofit extends CommonRetrofit {
    private final RandomWordService service =
            createRetrofit("https://random-word-api.herokuapp.com/")
                    .create(RandomWordService.class);

    public List<String> getRandomWords(int count) {
        Call<List<String>> apiCall = service.getWords(count);
        try {
            return apiCall.execute().body(); // Download synchronously.
        } catch (IOException e) {
            return null;
        }
    }
}
