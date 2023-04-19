package com.modzel.wordlearning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.modzel.wordlearning.database.Repository;

import java.util.concurrent.Executors;

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        TextView[] labels = {
                view.findViewById(R.id.correct_definition_matches_label),
                view.findViewById(R.id.learned_words_label),
                view.findViewById(R.id.correct_synonym_matches_label),
                view.findViewById(R.id.downloaded_words_label)
        };
        labels[0].setText(R.string.correct_definition_matches);
        labels[1].setText(R.string.learned_words);
        labels[2].setText(R.string.correct_synonym_matches);
        labels[3].setText(R.string.downloaded_words);
        TextView[] contents = {
                view.findViewById(R.id.correct_definition_matches_content),
                view.findViewById(R.id.learned_words_content),
                view.findViewById(R.id.correct_synonym_matches_content),
                view.findViewById(R.id.downloaded_words_content)
        };
        Handler uiHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(getActivity());
            long[] values = {
                    repo.getStatistic(WordLearning.CORRECT_DEFINITION_MATCHES).getValue(),
                    repo.getStatistic(WordLearning.LEARNED_WORDS).getValue(),
                    repo.getStatistic(WordLearning.CORRECT_SYNONYM_MATCHES).getValue(),
                    repo.getStatistic(WordLearning.DOWNLOADED_WORDS).getValue()
            };
            uiHandler.post(() -> {
                int i = 0;
                for (TextView tv : contents)
                    tv.setText(String.valueOf(values[i++]));
            });
        });
        return view;
    }
}
