package com.modzel.wordlearning.quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.modzel.wordlearning.R;
import com.modzel.wordlearning.WordLearning;
import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Word;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class DefinitionQuizActivity extends QuizActivity {
    @Override
    protected int getQuestionLabelStringId() {
        return R.string.pick_the_described_word;
    }

    @Override
    protected void setupFrom(Bundle state) {
        super.setupFrom(state);
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(this.getApplication());
            Word question = repo.getWord(correctAnswerId);
            LinkedList<Word> answers = new LinkedList<>();
            for (long id : answerIds)
                answers.addLast(repo.getWord(id));
            saveIds(question, answers);
            uiHandler.post(() -> fillWidgets(question, answers));
        });
    }

    private void saveIds(Word question, List<Word> answers) {
        correctAnswerId = question.getId();
        answerIds = new long[answers.size()];
        int i = 0;
        for (Word word : answers)
            answerIds[i++] = word.getId();
    }

    private void fillWidgets(Word question, List<Word> answers) {
        questionContent.setText(question.getDefinition());
        int i = 0;
        for (Word answer : answers)
            answerButtons[i++].setText(answer.getWord());
    }

    @Override
    protected void noteCorrectAnswer(Repository repo) {
        repo.incrementStatistic(WordLearning.CORRECT_DEFINITION_MATCHES);
        if (repo.incrementGoodAnswers(correctAnswerId) == 3)
            repo.incrementStatistic(WordLearning.LEARNED_WORDS);
    }

    @Override
    protected void noteIncorrectAnswer(Repository repo) {
        repo.resetGoodAnswers(correctAnswerId);
    }

    protected void setupNew(Handler uiHandler, Repository repo) {
        List<Word> unknownWords = repo.getUnknownWords();
        if (unknownWords.isEmpty()) {
            finishWithError(R.string.no_unknown_words);
            return;
        }
        Word question = pickNRandomElements(unknownWords, 1, random).get(0);
        List<Word> allWords = repo.getAllWords();
        if (allWords.size() < 4) { // w bazie danych nie ma nawet 4 słów
            finishWithError(R.string.no_words_for_answers);
            return;
        }
        List<Word> answers = generateAnswers(question, allWords);
        saveIds(question, answers);
        uiHandler.post(() -> fillWidgets(question, answers));
    }

    private List<Word> generateAnswers(Word question, List<Word> from) {
        from.remove(question);
        List<Word> answers = pickNRandomElements(from, 3, random);
        int correctAnswerIndex = random.nextInt(4);
        answers.add(correctAnswerIndex, question);
        return answers;
    }
}