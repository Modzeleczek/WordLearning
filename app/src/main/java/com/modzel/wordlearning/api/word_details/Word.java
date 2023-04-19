package com.modzel.wordlearning.api.word_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Word {
    @SerializedName("word")
    private String word;
    @SerializedName("meanings")
    private List<Meaning> meanings;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
}
