package com.modzel.wordlearning.api.word_details;

import com.modzel.wordlearning.api.CommonRetrofit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;

public class WordDetailsRetrofit extends CommonRetrofit {
    private final WordDetailsService service =
            createRetrofit("https://api.dictionaryapi.dev/")
                    .create(WordDetailsService.class);

    public List<Word> getDetailsForEligibleWords(List<String> words) throws IOException {
        LinkedList<Word> list = new LinkedList<>();
        for (String word : words) {
            Word detailedWord = getFirstHomonym(word);
            // We managed to download at least 1 eligible homonym of the word.
            if (detailedWord != null)
                list.addLast(detailedWord);
        }
        return list;
    }

    public Word getFirstHomonym(String word) throws IOException {
        Call<List<Word>> apiCall = service.getHomonyms(word);
        /* For one string, e.g. 'bear', API can return multiple words
        (homonyms). If could not download the word, execute throws
        IOException. */
        List<Word> homonyms = apiCall.execute().body();
        return isWordEligible(homonyms) ? homonyms.get(0) : null;
    }

    private boolean isWordEligible(List<Word> homonyms) {
        // Word was not downloaded or no its homonym exists in the dictionary.
        if (homonyms == null || homonyms.size() < 1)
            return false;
        // Take only the first homonym from multiple possible ones.
        Word dw = homonyms.get(0);
        if (isNullOrEmpty(dw.getWord())) // Actual word
            return false;
        List<Meaning> meanings = dw.getMeanings();
        // Word has no meanings in the dictionary.
        if (meanings == null || meanings.size() < 1)
            return false;
        Meaning m = meanings.get(0); // Take only the first meaning.
        if (isNullOrEmpty(m.getPartOfSpeech())) // Part of speech name
            return false;
        List<Definition> definitions = m.getDefinitions();
        /* The selected (first) meaning has no definitions in the
        dictionary. */
        if (definitions == null || definitions.size() < 1)
            return false;
        Definition d = definitions.get(0); // Take only the first definition.
        if (isNullOrEmpty(d.getDefinition())) // Actual definition
            return false;
        /* There may be no example of the the selected definition (null
        then). */
        // d.getExample();
        /* List<String> synonyms = d.getSynonyms();
        // Word may have no synonyms.
        if (synonyms == null || synonyms.size() < 1)
            return false; */
        return true;
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
