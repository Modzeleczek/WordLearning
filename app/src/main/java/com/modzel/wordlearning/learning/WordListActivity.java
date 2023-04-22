package com.modzel.wordlearning.learning;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.modzel.wordlearning.R;
import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Word;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class WordListActivity extends AppCompatActivity {
    private static final String KEY_SEARCHED_QUERY = "searchedQuery";
    private String searchedQuery;
    private RecyclerView recyclerView;
    private Repository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        // setTitle(R.string.words);
        /* savedInstanceState != null means that the state of the activity was
        saved in method onSaveInstanceState, then it was destroyed and now is
        being recreated after screen rotation. */
        if (savedInstanceState != null)
            searchedQuery = savedInstanceState.getString(KEY_SEARCHED_QUERY);
        else /* savedInstanceState == null means that the user destroyed the
            activity using the back button or he/she has not been in the
            activity yet so we do not have any previous state of the activity to
            be restored. */
            searchedQuery = "";

        recyclerView = findViewById(R.id.word_list_recycler_view);
        repo = new Repository(this.getApplication());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCHED_QUERY, searchedQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu_item);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchedQuery = query;
                filterWords(searchedQuery);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // https://stackoverflow.com/a/65175943
        int closeButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(closeButtonId);
        closeButton.setOnClickListener((view) -> {
            searchedQuery = "";
            filterWords(searchedQuery);
            searchView.onActionViewCollapsed();
        });
        /* onCreateOptionsMenu is called after onCreate so only here put the
        search bar into read state. */
        if (!searchedQuery.equals("")) {
            searchView.setQuery(searchedQuery, true);
            searchView.setIconified(false);
        } else
            filterWords(searchedQuery);
        searchView.clearFocus();
        return super.onCreateOptionsMenu(menu);
    }

    private void filterWords(String query) {
        Executors.newSingleThreadExecutor().execute((Runnable) () -> {
            List<Word> words = repo.getAllWords();
            LinkedList<Word> eligibleWords = new LinkedList<>();
            for (Word word : words) { // Find all words containing query.
                if (word.getWord().contains(query))
                    eligibleWords.addLast(word);
            }
            runOnUiThread(() -> {
                updateList(eligibleWords);
            });
        });
    }

    private void updateList(List<Word> words) {
        final WordAdapter adapter = new WordAdapter();
        adapter.setWords(words);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private static final int[] colorMap = { // Word knowledge levels
            /* Red if the user correctly matched word definitions 0 times in a
            row. */
            Color.rgb(255/1, 255/4, 0),
            Color.rgb(255/2, 255/3, 0), // 1 time
            Color.rgb(255/3, 255/2, 0), // 2 times
            Color.rgb(255/4, 255/1, 0) // 3 times
    };

    private class WordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView wordTextView;
        private final View levelIndicatorView; // Word knowledge indicator
        private Word word;

        public WordHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.word_list_item, parent, false));
            itemView.setOnClickListener(this);
            wordTextView = itemView.findViewById(R.id.word_text_view);
            levelIndicatorView = itemView.findViewById(R.id.level_indicator_view);
        }

        public void bind(Word word) {
            if (word != null && !isNullOrEmpty(word.getWord())) {
                this.word = word;
                wordTextView.setText(word.getWord());
                // Limit colorIndex to colorMap.length - 1.
                int colorIndex = Math.min(word.getGoodAnswers(), colorMap.length - 1);
                levelIndicatorView.setBackgroundColor(colorMap[colorIndex]);
            }
        }

        private boolean isNullOrEmpty(String string) {
            return string == null || string.isEmpty();
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(WordListActivity.this, WordDetailsActivity.class);
            intent.putExtra(WordDetailsActivity.EXTRA_WORD_ID, word.getId());
            startActivity(intent);
        }
    }

    private class WordAdapter extends RecyclerView.Adapter<WordHolder> {
        private List<Word> words;

        @NonNull
        @Override
        public WordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WordHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull WordHolder holder, int position) {
            if (words == null) {
                Snackbar.make(findViewById(R.id.word_list_layout), R.string.no_words, Snackbar.LENGTH_LONG).show();
                return;
            }
            Word word = words.get(position);
            holder.bind(word);
        }

        @Override
        public int getItemCount() {
            if (words != null)
                return words.size();
            return 0;
        }

        public void setWords(List<Word> words) {
            this.words = words;
            notifyDataSetChanged();
        }
    }
}