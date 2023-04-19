package com.modzel.wordlearning.api.word_details;

import com.modzel.wordlearning.api.CommonRetrofit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class WordDetailsRetrofit extends CommonRetrofit {
    private static final String WORD_DEFINITION_API_URL = "https://api.dictionaryapi.dev/";

    private static Retrofit getInstance() {
        return CommonRetrofit.getInstance(WORD_DEFINITION_API_URL);
    }

    public static WordDetailsService createService() {
        return getInstance().create(WordDetailsService.class);
    }

    public static List<Word> getDetailsForEligibleWords(List<String> words) throws IOException {
        WordDetailsService service = createService();
        LinkedList<Word> list = new LinkedList<>();
        for (String word : words) {
            Word detailedWord = getFirstHomonym(service, word);
            if (detailedWord != null) // udało się pobrać przynajmniej 1 odpowiedni homonim słowa
                list.addLast(detailedWord);
        }
        return list;
    }

    public static Word getFirstHomonym(WordDetailsService service, String word) throws IOException {
        Call<List<Word>> apiCall = service.getHomonyms(word);
        List<Word> homonyms = apiCall.execute().body(); // dla jednego stringa, np. "bear", API może
        // zwrócić kilka słów (homonimów); jeżeli nie udało się pobrać słowa, to execute wyrzuca IOException
        return isWordEligible(homonyms) ? homonyms.get(0) : null;
    }

    private static boolean isWordEligible(List<Word> homonyms) {
        if (homonyms == null || homonyms.size() < 1) // słowo nie zostało pobrane lub nie istnieje
            // żaden jego homonim w słowniku
            return false;
        Word dw = homonyms.get(0); // bierzemy tylko pierwszy homonim z możliwych kilku
        if (isNullOrEmpty(dw.getWord())) // faktyczne słowo
            return false;
        List<Meaning> meanings = dw.getMeanings();
        if (meanings == null || meanings.size() < 1) // słowo nie ma żadnych znaczeń w słowniku
            return false;
        Meaning m = meanings.get(0); // bierzemy tylko pierwsze znaczenie
        if (isNullOrEmpty(m.getPartOfSpeech())) // nazwa części mowy
            return false;
        List<Definition> definitions = m.getDefinitions();
        if (definitions == null || definitions.size() < 1) // wybrane pierwsze znaczenie nie ma
            // żadnych definicji w słowniku
            return false;
        Definition d = definitions.get(0); // bierzemy tylko pierwszą definicję
        if (isNullOrEmpty(d.getDefinition())) // faktyczna definicja
            return false;
        // d.getExample(); // przykładu wybranej definicji może nie być (wtedy null)
        /* List<String> synonyms = d.getSynonyms();
        if (synonyms == null || synonyms.size() < 1) // słowo może nie mieć synonimów
            return false; */
        return true;
    }

    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
