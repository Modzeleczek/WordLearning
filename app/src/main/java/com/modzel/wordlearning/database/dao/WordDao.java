package com.modzel.wordlearning.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.modzel.wordlearning.database.entity.Word;

import java.util.List;

@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Word word); // zwraca klucz główny wstawionego rekordu

    @Query("SELECT * FROM Word WHERE id = :id")
    Word get(long id);

    @Query("SELECT * FROM Word ORDER BY word")
    List<Word> getAll();

    @Query("SELECT * FROM Word WHERE goodAnswers < :goodAnswers")
    List<Word> getWithLessThan(int goodAnswers);

    @Query("SELECT * FROM Word WHERE word = :word")
    List<Word> find(String word);

    @Update
    void update(Word word);

    @Query("DELETE FROM Word")
    void deleteAll();

    @Query("UPDATE Word SET goodAnswers = goodAnswers + 1 WHERE id = :id")
    void incrementGoodAnswers(long id);

    @Query("UPDATE Word SET goodAnswers = 0 WHERE id = :id")
    void resetGoodAnswers(long id);
}
