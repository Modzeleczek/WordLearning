package com.modzel.wordlearning.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.modzel.wordlearning.database.entity.Synonym;
import com.modzel.wordlearning.database.entity.Word;
import com.modzel.wordlearning.database.entity.WordSynonym;

import java.util.List;

@Dao
public interface WordSynonymDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WordSynonym wordSynonyms);

    @Query("SELECT * FROM WordSynonym WHERE wordId = :wordId AND synonymId = :synonymId")
    WordSynonym get(long wordId, long synonymId);

    @Query("SELECT * FROM WordSynonym ws, Synonym s WHERE ws.wordId = :wordId AND s.id = ws.synonymId")
    List<Synonym> getSynonymsFor(long wordId);

    @Query("SELECT * FROM WordSynonym ws, Synonym s WHERE ws.wordId != :wordId AND s.id = ws.synonymId")
    List<Synonym> getSynonymsNotFor(long wordId);

    @Query("SELECT * FROM Word WHERE id IN (SELECT wordId FROM WordSynonym)")
    List<Word> getWordsWithSynonyms();

    @Update
    void update(WordSynonym wordSynonym);

    @Query("DELETE FROM WordSynonym")
    void deleteAll();
}
