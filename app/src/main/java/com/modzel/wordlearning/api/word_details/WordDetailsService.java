package com.modzel.wordlearning.api.word_details;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WordDetailsService {
    @GET("/api/v2/entries/en/{word}")
    Call<List<Word>> getHomonyms(@Path("word") String word);
}
