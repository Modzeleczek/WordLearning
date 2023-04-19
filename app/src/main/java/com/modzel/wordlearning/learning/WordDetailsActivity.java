package com.modzel.wordlearning.learning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.modzel.wordlearning.R;
import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Synonym;
import com.modzel.wordlearning.database.entity.Word;

import java.util.List;
import java.util.concurrent.Executors;

public class WordDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_WORD_ID = "com.modzel.WORD_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);
        // setTitle(R.string.word_details);

        Intent starter = getIntent();
        long id = starter.getLongExtra(EXTRA_WORD_ID, -1);
        if (id == -1) {
            finish();
            return;
        }

        TextView wordLabel = findViewById(R.id.word_label);
        wordLabel.setText(R.string.word);
        TextView wordTextView = findViewById(R.id.word_text_view);

        TextView partOfSpeechLabel = findViewById(R.id.part_of_speech_label);
        partOfSpeechLabel.setText(R.string.part_of_speech);
        TextView partOfSpeechTextView = findViewById(R.id.part_of_speech_text_view);

        TextView definitionLabel = findViewById(R.id.definition_label);
        definitionLabel.setText(R.string.definition);
        TextView definitionTextView = findViewById(R.id.definition_text_view);

        TextView exampleLabel = findViewById(R.id.example_label);
        exampleLabel.setText(R.string.example);
        TextView exampleTextView = findViewById(R.id.example_text_view);

        TextView synonymsLabel = findViewById(R.id.synonyms_label);
        synonymsLabel.setText(R.string.synonyms);
        TextView synonymsTextView = findViewById(R.id.synonyms_text_view);

        Button openInDictionaryButton = findViewById(R.id.open_in_dictionary_button);

        Executors.newSingleThreadExecutor().execute(() ->{
            Repository repo = new Repository(this.getApplication());
            Word word = repo.getWord(id);
            List<Synonym> synonyms = repo.getSynonymsFor(word.getId());
            runOnUiThread(() -> {
                wordTextView.setText(word.getWord());
                partOfSpeechTextView.setText(word.getPartOfSpeech());
                definitionTextView.setText(word.getDefinition());
                if (word.getExample() != null) // jeżeli słowo ma przykład
                    exampleTextView.setText(word.getExample());
                else {
                    exampleLabel.setVisibility(View.GONE);
                    exampleTextView.setVisibility(View.GONE);
                }
                if (!synonyms.isEmpty()) // jeżeli słowo ma synonimy
                    synonymsTextView.setText(joinSynonyms(", ", synonyms));
                else {
                    synonymsLabel.setVisibility(View.GONE);
                    synonymsTextView.setVisibility(View.GONE);
                }
                openInDictionaryButton.setOnClickListener((v) -> openInDictionary(word.getWord()));
            });
        });
    }

    private String joinSynonyms(String delimiter, List<Synonym> synonyms) {
        StringBuilder sb = new StringBuilder();
        for (Synonym s : synonyms) {
            sb.append(s.getSynonym());
            sb.append(delimiter);
        }
        int length = sb.length();
        sb.delete(length - delimiter.length(), length - 1);
        return sb.toString();
    }

    private void openInDictionary(String word) {
        String url = "https://dictionary.cambridge.org/dictionary/english/" + word;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}