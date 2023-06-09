package com.modzel.wordlearning.api.random_words;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RandomWordService {
    @GET("/api")
    Call<List<String>> getWords(@Query("words") int words);
}
