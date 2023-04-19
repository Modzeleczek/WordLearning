package com.modzel.wordlearning;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;

import com.modzel.wordlearning.api.random_words.RandomWordRetrofit;
import com.modzel.wordlearning.api.word_details.Definition;
import com.modzel.wordlearning.api.word_details.Meaning;
import com.modzel.wordlearning.api.word_details.Word;
import com.modzel.wordlearning.api.word_details.WordDetailsRetrofit;
import com.modzel.wordlearning.api.word_details.WordDetailsService;
import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Synonym;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadActivity extends ResultingActivity {
    private static final int DOWNLOADED_WORDS = 50;
    private AtomicBoolean canceled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgressTintList(ColorStateList.valueOf(getColor(R.color.progress_bar_color)));
        /* progressBar.getProgressDrawable().setColorFilter(getColor(R.color.progress_bar_color),
                PorterDuff.Mode.SRC_IN); pasek w czerwonym filtrem, czyli pusta część paska jest
                przezroczysta czerwona */
        progressBar.setMin(0);
        progressBar.setMax(DOWNLOADED_WORDS);
        progressBar.setProgress(0);

        canceled = new AtomicBoolean(false);
        Button cancelButton = findViewById(R.id.download_cancel_button);
        cancelButton.setOnClickListener(v -> {
            canceled.set(true);
            finishWithError(R.string.download_canceled);
        });

        // https://stackoverflow.com/a/64969640
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(new Downloader(uiHandler, progressBar, canceled));
    }

    @Override
    public void onBackPressed() {
        canceled.set(true);
        finishWithError(R.string.download_canceled);
        super.onBackPressed();
    }

    private class Downloader implements Runnable {
        private final Handler uiHandler;
        private final ProgressBar progressBar;
        private final AtomicBoolean canceled;

        public Downloader(Handler uiHandler, ProgressBar progressBar, AtomicBoolean canceled) {
            this.uiHandler = uiHandler;
            this.progressBar = progressBar;
            this.canceled = canceled;
        }

        @Override
        public void run() { //Background work here
            List<String> words = RandomWordRetrofit.getRandomWords(DOWNLOADED_WORDS);
            if (words == null) { // błąd podczas pobierania losowych słów
                if (!canceled.get()) finishWithError(R.string.random_words_download_failure);
                return;
            }
            WordDetailsService service = WordDetailsRetrofit.createService();
            LinkedList<Word> detailedWords = new LinkedList<>();
            for (String word : words) {
                if (canceled.get()) return;
                try {
                    Word detailedWord = WordDetailsRetrofit.getFirstHomonym(service, word);
                    if (detailedWord != null) // jeżeli słowo nie spełnia warunków aplikacji, to je pomijamy
                        detailedWords.addLast(detailedWord);
                } catch (IOException ignored) {} // jeżeli nie udało się pobrać słowa, to je pomijamy
                uiHandler.post(() -> progressBar.incrementProgressBy(1)); //UI Thread work here
            }
            // if (detailedWords.size() < DOWNLOADED_WORDS / 4) {
            if (detailedWords.isEmpty()) {
                if (!canceled.get()) finishWithError(R.string.word_details_download_failure);
                return;
            }
            if (canceled.get()) return;
            replaceInDatabase(detailedWords);
            finishWithSuccess(R.string.new_words_download_success);
        }
    }

    private void replaceInDatabase(List<Word> detailedWords) {
        Repository repo = new Repository(this.getApplication());
        repo.deleteAllSynonyms();
        repo.deleteAllWords();
        for (Word dw : detailedWords) {
            String word = dw.getWord(); // zapisujemy faktyczną "wartość" słowa
            Meaning m = dw.getMeanings().get(0); // bierzemy tylko pierwsze znaczenie
            String partOfSpeech = m.getPartOfSpeech(); // zapisujemy nazwę części mowy
            Definition d = m.getDefinitions().get(0); // bierzemy tylko pierwszą definicję
            String definition = d.getDefinition(); // zapisujemy definicję
            String example = d.getExample(); // zapisujemy przykład wybranej definicji lub null, jeżeli go nie ma
            List<String> synonyms = d.getSynonyms();
            long insertedId = repo.insert(new com.modzel.wordlearning.database.entity.Word(
                    word, partOfSpeech, definition, example));
            repo.incrementStatistic(WordLearning.DOWNLOADED_WORDS);
            for (String synonym : synonyms) {
                repo.insertSynonymForWord(insertedId, new Synonym(synonym));
            }
        }
    }
}