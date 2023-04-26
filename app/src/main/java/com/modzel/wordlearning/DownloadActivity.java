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
        /* // Progress bar with red filter so its empty part is transparent red
        progressBar.getProgressDrawable().setColorFilter(getColor(R.color.progress_bar_color),
                PorterDuff.Mode.SRC_IN); */
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
            RandomWordRetrofit randomRF = new RandomWordRetrofit();
            List<String> words = randomRF.getRandomWords(DOWNLOADED_WORDS);
            // Error occurred while downloading random words.
            if (words == null) {
                if (!canceled.get()) finishWithError(R.string.random_words_download_failure);
                return;
            }

            WordDetailsRetrofit detailsRF = new WordDetailsRetrofit();
            LinkedList<Word> detailedWords = new LinkedList<>();
            for (String word : words) {
                if (canceled.get()) return;
                try {
                    Word detailedWord = detailsRF.getFirstHomonym(word);
                    /* If the word does not satisfy application's conditions,
                    skip it. */
                    if (detailedWord != null)
                        detailedWords.addLast(detailedWord);
                    // If the word could not be downloaded, skip it.
                } catch (IOException ignored) {}
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
            String word = dw.getWord(); // Save the word's content.
            Meaning m = dw.getMeanings().get(0); // Take only the first meaning.
            // Save part of speech name.
            String partOfSpeech = m.getPartOfSpeech();
            // Take only the first definition.
            Definition d = m.getDefinitions().get(0);
            String definition = d.getDefinition(); // Save the definition.
            /* Save an example of the selected definition or null if no
            example exists. */
            String example = d.getExample();
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