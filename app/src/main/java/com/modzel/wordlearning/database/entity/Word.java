package com.modzel.wordlearning.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Word")
public class Word {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String word;
    private String partOfSpeech;
    private String definition;
    private String example; // Null if the word does not have an example.
    // How many times the user correctly matched this word's definition.
    private int goodAnswers;

    public Word(String word, String partOfSpeech, String definition, String example) {
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
        this.example = example;
        goodAnswers = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

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

    public int getGoodAnswers() {
        return goodAnswers;
    }

    public void setGoodAnswers(int goodAnswers) {
        this.goodAnswers = goodAnswers;
    }
}
