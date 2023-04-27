package com.modzel.wordlearning.api.word_details;

import com.modzel.wordlearning.api.CommonRetrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;

public class WordDetailsRetrofit extends CommonRetrofit {
    // An IP address can get 450 word definitions per 5 minutes.
    private final WordDetailsService service =
            createRetrofit("https://api.dictionaryapi.dev/")
                    .create(WordDetailsService.class);

    public List<Word> getDetailsForEligibleWords(List<String> words) throws IOException {
        LinkedList<Word> list = new LinkedList<>();
        for (String word : words) {
            Word detailedWord = getFirstEligibleHomonym(word);
            // We managed to download at least 1 eligible homonym of the word.
            if (detailedWord != null)
                list.addLast(detailedWord);
        }
        return list;
    }

    public Word getFirstEligibleHomonym(String word) throws IOException {
        Call<List<Word>> apiCall = service.getHomonyms(word);
        /* For one string, e.g. 'bear', API can return multiple words
        (homonyms). If could not download the word, execute throws
        IOException. */
        List<Word> homonyms = apiCall.execute().body();

        // Word was not downloaded or no its homonym exists in the dictionary.
        if (homonyms == null || homonyms.size() < 1)
            return null;

        Word result = new Word();
        Meaning meaning = null;
        for (Word homonym : homonyms) {
            result.setWord(homonym.getWord());
            meaning = getFirstEligibleMeaning(homonym);
            if (meaning != null) {
                ArrayList<Meaning> list = new ArrayList<>(1);
                list.add(meaning);
                result.setMeanings(list);
                return result;
            }
        }
        // Word has no eligible homonym.
        return null;
    }

    private Meaning getFirstEligibleMeaning(Word homonym) {
        List<Meaning> meanings = homonym.getMeanings();
        // Word has no meanings in the dictionary.
        if (meanings == null || meanings.size() < 1)
            return null;

        for (Meaning meaning : meanings) {
            Definition definition = getFirstEligibleDefinition(meaning);
            if (definition != null) {
                ArrayList<Definition> list = new ArrayList<>(1);
                list.add(definition);
                meaning.setDefinitions(list);
                return meaning;
            }
        }
        // Homonym has no eligible meaning.
        return null;
    }

    private Definition getFirstEligibleDefinition(Meaning meaning) {
        if (isNullOrEmpty(meaning.getPartOfSpeech())) // Part of speech name
            return null;

        List<Definition> definitions = meaning.getDefinitions();
        // The meaning has no definitions in the dictionary.
        if (definitions == null || definitions.size() < 1)
            return null;

        for (Definition definition : definitions) {
            if (isNullOrEmpty(definition.getDefinition())) // Actual definition
                continue;
            /* There may be no example of the the selected definition (null
            then). */
            List<String> synonyms = definition.getSynonyms();
            // Word must have synonyms.
            if (synonyms == null || synonyms.size() < 1)
                continue;
            return definition;
        }
        // Meaning has no eligible definition.
        return null;
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
