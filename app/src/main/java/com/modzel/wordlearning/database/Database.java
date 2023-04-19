package com.modzel.wordlearning.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.modzel.wordlearning.database.dao.StatisticDao;
import com.modzel.wordlearning.database.dao.SynonymDao;
import com.modzel.wordlearning.database.dao.WordDao;
import com.modzel.wordlearning.database.dao.WordSynonymDao;
import com.modzel.wordlearning.database.entity.Statistic;
import com.modzel.wordlearning.database.entity.Synonym;
import com.modzel.wordlearning.database.entity.Word;
import com.modzel.wordlearning.database.entity.WordSynonym;

@androidx.room.Database(entities = { Word.class, WordSynonym.class, Synonym.class, Statistic.class },
        version = 3, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static volatile Database INSTANCE;

    public abstract WordDao getWordDao();
    public abstract WordSynonymDao getWordSynonymDao();
    public abstract SynonymDao getSynonymDao();
    public abstract StatisticDao getStatisticDao();

    static Database getInstance(final Context applicationContext) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(applicationContext,
                    Database.class,
                    "word_database")
                    .fallbackToDestructiveMigration()
                    .build();
        return INSTANCE;
    }
}
