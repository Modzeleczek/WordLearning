package com.modzel.wordlearning.database.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.modzel.wordlearning.database.entity.Statistic;

import java.util.List;

@Dao
public interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Statistic statistic);

    @Query("SELECT * FROM Statistic WHERE name = :name")
    Statistic get(@NonNull String name);

    @Update
    void update(Statistic statistic);

    @Query("UPDATE Statistic SET value = value + 1 WHERE name = :name")
    void increment(String name);

    @Query("UPDATE Statistic SET value = 0 WHERE name = :name")
    void reset(String name);

    @Query("DELETE FROM Statistic")
    void deleteAll();
}
