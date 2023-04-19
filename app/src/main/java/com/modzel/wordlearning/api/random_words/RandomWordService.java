package com.modzel.wordlearning.api.random_words;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RandomWordService {
    /* @GET("/word?swear=0")
    Call<List<String>> getWords(@Query("number") int number); */
    @GET("/random/noun")
    Call<List<String>> getWords(@Query("count") int count);
}
