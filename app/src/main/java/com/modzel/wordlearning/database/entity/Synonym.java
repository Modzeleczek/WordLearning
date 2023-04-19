package com.modzel.wordlearning.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Synonym")
public class Synonym {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String synonym;

    public Synonym(String synonym) {
        this.synonym = synonym;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }
}
