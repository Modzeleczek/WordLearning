package com.modzel.wordlearning.database;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.modzel.wordlearning.database.dao.StatisticDao;
import com.modzel.wordlearning.database.dao.SynonymDao;
import com.modzel.wordlearning.database.dao.WordDao;
import com.modzel.wordlearning.database.dao.WordSynonymDao;
import com.modzel.wordlearning.database.entity.Statistic;
import com.modzel.wordlearning.database.entity.Synonym;
import com.modzel.wordlearning.database.entity.Word;
import com.modzel.wordlearning.database.entity.WordSynonym;

import java.util.List;

public class Repository {
    private final WordDao wordDao;
    private final WordSynonymDao wordSynonymDao;
    private final SynonymDao synonymDao;
    private final StatisticDao statisticDao;

    public Repository(Context context) {
        Database database = Database.getInstance(context);
        wordDao = database.getWordDao();
        wordSynonymDao = database.getWordSynonymDao();
        synonymDao = database.getSynonymDao();
        statisticDao = database.getStatisticDao();
    }

    public void deleteEverything() {
        wordSynonymDao.deleteAll();
        wordDao.deleteAll();
        synonymDao.deleteAll();
        statisticDao.deleteAll();
    }

    public Word getWord(long id) {
        return wordDao.get(id);
    }

    public List<Word> getAllWords() {
        return wordDao.getAll();
    }

    public List<Word> getWordsWithSynonyms() {
        return wordSynonymDao.getWordsWithSynonyms();
    }

    public long insert(Word word) {
        if (!wordDao.find(word.getWord()).isEmpty()) // słowo już istnieje
            return -1;
        return wordDao.insert(word); // wywołujemy synchronicznie, czyli bez databaseWriteExecutorów
    }

    public void update(Word word) {
        wordDao.update(word);
    }

    public void deleteAllWords() {
        wordDao.deleteAll();
    }

    public List<Word> getUnknownWords() {
        return wordDao.getWithLessThan(3);
    }

    public int incrementGoodAnswers(long id) { // zwraca nową wartość
        if (wordDao.get(id).getGoodAnswers() < 3)
            wordDao.incrementGoodAnswers(id);
        return wordDao.get(id).getGoodAnswers();
    }

    public void resetGoodAnswers(long id) {
        wordDao.resetGoodAnswers(id);
    }

    public Synonym getSynonym(long id) {
        return synonymDao.get(id);
    }

    public List<Synonym> getSynonymsFor(long wordId) {
        return wordSynonymDao.getSynonymsFor(wordId);
    }

    public List<Synonym> getSynonymsNotFor(long wordId) {
        return wordSynonymDao.getSynonymsNotFor(wordId);
    }

    public long insertSynonymForWord(long wordId, Synonym synonym) { // zwraca id synonimu
        List<Synonym> found = synonymDao.find(synonym.getSynonym());
        long synonymId = -1;
        if (found.isEmpty()) // jeszcze nie ma tego synonimu
            synonymId = synonymDao.insert(synonym);
        else // już jest
            synonymId = found.get(0).getId(); // powinien być maksymalnie 1
        wordSynonymDao.insert(new WordSynonym(wordId, synonymId));
        return synonymId;
    }

    public void deleteAllSynonyms() {
        synonymDao.deleteAll();
    }

    public Statistic getStatistic(@NonNull String name) {
        return statisticDao.get(name);
    }

    public void insert(Statistic statistic) {
        statisticDao.insert(statistic);
    }

    public void incrementStatistic(String name) {
        statisticDao.increment(name);
    }

    public void resetStatistic(String name) {
        statisticDao.reset(name);
    }
}
