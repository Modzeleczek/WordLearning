package com.modzel.wordlearning.api.word_details;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Definition {
    @SerializedName("definition")
    private String definition;
    @SerializedName("example")
    private String example;
    @SerializedName("synonyms")
    private List<String> synonyms;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
