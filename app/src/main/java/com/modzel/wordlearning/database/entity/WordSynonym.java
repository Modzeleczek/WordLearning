package com.modzel.wordlearning.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "WordSynonym",
        primaryKeys = { "wordId", "synonymId" },
        foreignKeys = {
        @ForeignKey(entity = Word.class,
            parentColumns = "id",
            childColumns = "wordId",
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Synonym.class,
            parentColumns = "id",
            childColumns = "synonymId",
            onDelete = ForeignKey.CASCADE)})
public class WordSynonym {
    private long wordId;
    private long synonymId;

    public WordSynonym(long wordId, long synonymId) {
        this.wordId = wordId;
        this.synonymId = synonymId;
    }

    public long getWordId() {
        return wordId;
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    public long getSynonymId() {
        return synonymId;
    }

    public void setSynonymId(long synonymId) {
        this.synonymId = synonymId;
    }
}
