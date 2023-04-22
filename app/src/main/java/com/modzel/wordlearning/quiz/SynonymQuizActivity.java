package com.modzel.wordlearning.quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.modzel.wordlearning.R;
import com.modzel.wordlearning.WordLearning;
import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Synonym;
import com.modzel.wordlearning.database.entity.Word;

import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.Executors;

public class SynonymQuizActivity extends QuizActivity {
    private static final String KEY_QUESTION_ID = "questionId";
    private long questionId;

    @Override
    protected int getQuestionLabelStringId() {
        return R.string.pick_the_words_synonym;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_QUESTION_ID, questionId);
    }

    @Override
    protected void setupFrom(Bundle state) {
        super.setupFrom(state);
        questionId = state.getLong(KEY_QUESTION_ID, -1);
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(this.getApplication());
            Word question = repo.getWord(questionId);
            Synonym answer = repo.getSynonym(correctAnswerId);
            LinkedList<Synonym> answers = new LinkedList<>();
            for (long id : answerIds)
                answers.addLast(repo.getSynonym(id));
            saveIds(question, answer, answers);
            uiHandler.post(() -> fillWidgets(question, answers));
        });
    }

    private void saveIds(Word question, Synonym answer, List<Synonym> answers) {
        questionId = question.getId();
        correctAnswerId = answer.getId();
        answerIds = new long[answers.size()];
        int i = 0;
        for (Synonym synonym : answers)
            answerIds[i++] = synonym.getId();
    }

    private void fillWidgets(Word question, List<Synonym> answers) {
        questionContent.setText(question.getWord());
        int i = 0;
        for (Synonym answer : answers)
            answerButtons[i++].setText(answer.getSynonym());
    }

    @Override
    protected void noteCorrectAnswer(Repository repo) {
        repo.incrementStatistic(WordLearning.CORRECT_SYNONYM_MATCHES);
    }

    // Do not count incorrect answers.
    @Override
    protected void noteIncorrectAnswer(Repository repo) {}

    protected void setupNew(Handler uiHandler, Repository repo) {
        List<Word> allWordsWithSynonyms = repo.getWordsWithSynonyms();
        if (allWordsWithSynonyms.isEmpty()) {
            finishWithError(R.string.no_words);
            return;
        }
        Word question = pickNRandomElements(allWordsWithSynonyms, 1, random).get(0);
        List<Synonym> questionSynonyms = repo.getSynonymsFor(question.getId());
        Synonym answer = pickNRandomElements(questionSynonyms, 1, random).get(0);
        List<Synonym> notQuestionSynonyms = repo.getSynonymsNotFor(question.getId());
        /* The database does not contain even 4 synonyms not belonging to the
        question. */
        if (notQuestionSynonyms.size() < 4) {
            finishWithError(R.string.no_synonyms_for_answers);
            return;
        }
        List<Synonym> answers = generateAnswers(answer, notQuestionSynonyms);
        saveIds(question, answer, answers);
        uiHandler.post(() -> fillWidgets(question, answers));
    }

    private List<Synonym> generateAnswers(Synonym answer, List<Synonym> from) {
        List<Synonym> answers = pickNRandomElements(from, 3, random);
        int correctAnswerIndex = random.nextInt(4);
        answers.add(correctAnswerIndex, answer);
        return answers;
    }
}