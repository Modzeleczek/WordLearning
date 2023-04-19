package com.modzel.wordlearning.quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.modzel.wordlearning.R;
import com.modzel.wordlearning.ResultingActivity;
import com.modzel.wordlearning.database.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public abstract class QuizActivity extends ResultingActivity {
    protected static Random random = new Random();
    protected static final String KEY_CORRECT_ANSWER_ID = "correctAnswerId";
    protected static final String KEY_ANSWER_IDS = "answerIds";
    protected long correctAnswerId;
    protected long[] answerIds;
    protected TextView questionContent;
    protected Button[] answerButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        TextView questionLabel = findViewById(R.id.quiz_question_label);
        questionLabel.setText(getQuestionLabelStringId());
        questionContent = findViewById(R.id.quiz_question_content);
        answerButtons = new Button[] {
                findViewById(R.id.quiz_answer_button_0),
                findViewById(R.id.quiz_answer_button_1),
                findViewById(R.id.quiz_answer_button_2),
                findViewById(R.id.quiz_answer_button_3)
        };
        setupButtons();
        if (savedInstanceState != null) { // wracamy po obróceniu ekranu
            setupFrom(savedInstanceState);
            return;
        }
        // przychodzimy z głównego menu
        setupNewAsync();
    }

    protected abstract int getQuestionLabelStringId();

    private void setupButtons() {
        for (int i = 0; i < answerButtons.length; ++i) {
            int index = i;
            answerButtons[i].setOnClickListener((v) -> buttonClick(index));
        }
    }

    private void buttonClick(int index) {
        int correctButtonIndex = -1;
        for (int i = 0; i < answerButtons.length; ++i) {
            if (answerIds[i] == correctAnswerId)
                correctButtonIndex = i;
        }
        String message;
        if (index != correctButtonIndex) {
            message = getString(R.string.incorrect_answer) + " (" + answerButtons[correctButtonIndex].getText() + ")";
            noteIncorrectAnswerAndSetupNewAsync();
        } else {
            message = getString(R.string.correct_answer);
            noteCorrectAnswerAndSetupNewAsync();
        }
        Snackbar.make(findViewById(R.id.quiz_layout), message, 500).show();
    }

    protected void noteCorrectAnswerAndSetupNewAsync() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(this.getApplication());
            noteCorrectAnswer(repo);
            setupNew(uiHandler, repo);
        });
    }

    protected abstract void noteCorrectAnswer(Repository repo);

    protected void noteIncorrectAnswerAndSetupNewAsync() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(this.getApplication());
            noteIncorrectAnswer(repo);
            setupNew(uiHandler, repo);
        });
    }

    protected abstract void noteIncorrectAnswer(Repository repo);

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_CORRECT_ANSWER_ID, correctAnswerId);
        outState.putLongArray(KEY_ANSWER_IDS, answerIds);
    }

    protected void setupFrom(Bundle state) {
        correctAnswerId = state.getLong(KEY_CORRECT_ANSWER_ID, -1);
        answerIds = state.getLongArray(KEY_ANSWER_IDS);
    }

    private void setupNewAsync() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Repository repo = new Repository(this.getApplication());
        Executors.newSingleThreadExecutor().execute(() -> setupNew(uiHandler, repo));
    }

    protected abstract void setupNew(Handler uiHandler, Repository repo);

    // https://stackoverflow.com/a/35278327
    protected <E> List<E> pickNRandomElements(List<E> list, int n, Random r) {
        int length = list.size();
        if (length < n) return null;
        for (int i = length - 1; i >= length - n; --i) //We don't need to shuffle the whole list
            Collections.swap(list, i , r.nextInt(i + 1));
        return list.subList(length - n, length);
    }

    protected <E> List<E> pickNRandomElements(List<E> list, int n) {
        return pickNRandomElements(list, n, ThreadLocalRandom.current());
    }
}
