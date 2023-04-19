package com.modzel.wordlearning.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.modzel.wordlearning.database.entity.Synonym;

import java.util.List;

@Dao
public interface SynonymDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Synonym synonym);

    @Query("SELECT * FROM Synonym WHERE id = :id")
    Synonym get(long id);

    @Query("SELECT * FROM Synonym")
    List<Synonym> getAll();

    @Query("SELECT * FROM Synonym WHERE synonym = :synonym")
    List<Synonym> find(String synonym);

    @Update
    void update(Synonym synonym);

    @Query("DELETE FROM Synonym")
    void deleteAll();
}
