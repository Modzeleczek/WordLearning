package com.modzel.wordlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.modzel.wordlearning.learning.WordListActivity;
import com.modzel.wordlearning.quiz.DefinitionQuizActivity;
import com.modzel.wordlearning.quiz.SynonymQuizActivity;

public class SelectModeFragment extends Fragment {
    public static final int DEFINITION_QUIZ_ACTIVITY_REQUEST_CODE = 2;
    public static final int SYNONYM_QUIZ_ACTIVITY_REQUEST_CODE = 3;

    public SelectModeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_mode, container, false);
        Button learningButton = view.findViewById(R.id.learning_button);
        learningButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WordListActivity.class);
            startActivity(intent);
        });
        Button definitionQuizButton = view.findViewById(R.id.definition_quiz_button);
        definitionQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DefinitionQuizActivity.class);
            startActivityForResult(intent, DEFINITION_QUIZ_ACTIVITY_REQUEST_CODE);
        });
        Button synonymQuizButton = view.findViewById(R.id.synonym_quiz_button);
        synonymQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SynonymQuizActivity.class);
            startActivityForResult(intent, SYNONYM_QUIZ_ACTIVITY_REQUEST_CODE);
        });
        return view;
    }
}
