package com.modzel.wordlearning.api.word_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Meaning {
    @SerializedName("partOfSpeech")
    private String partOfSpeech;
    @SerializedName("definitions")
    private List<Definition> definitions;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }
}
