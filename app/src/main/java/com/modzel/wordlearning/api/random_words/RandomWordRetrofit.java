package com.modzel.wordlearning.api.random_words;

import com.modzel.wordlearning.api.CommonRetrofit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Retrofit;

public class RandomWordRetrofit extends CommonRetrofit {
    // któreś z tych API miały być użyte do pobierania losowych angielskich słów, ale zwracały jakieś dziwne formy, których nie rozumiało drugie API słownikowe do definicji słów (ciąg dalszy nad polem index)
    private static final String RANDOM_WORD_API_URL = "https://random-word-api.herokuapp.com/";
    // private static final String RANDOM_WORD_API_URL = "https://random-word-form.herokuapp.com/";

    private static Retrofit getInstance() {
        return CommonRetrofit.getInstance(RANDOM_WORD_API_URL);
    }

    /*public static List<String> getRandomWords(int count) {
        RandomWordService service = getInstance().create(RandomWordService.class);
        Call<List<String>> apiCall = service.getWords(count);
        try {
            return apiCall.execute().body(); // pobieramy synchronicznie
        } catch (IOException e) {
            return null;
        }
    }*/

    // więc ostatecznie zhardcodowałem 50 słów, w tej metodzie udającej request do API; w każdym razie w klasie WordDetailsRetrofit jest drugie, działające API do pobierania definicji słów
    private static int index = 0;
    public static List<String> getRandomWords(int count) {
        String[] hardCodedWords = {
                "bear", "arouse", "celery", "roof", "crown", "week", "crib", "tax", "money", "twig", "doctor",
                "stem", "string", "baseball", "kneel", "fax", "sip", "stroke", "bathe", "walk", "jam", "march",
                "hug", "strip", "cycle", "welcome", "goofy", "husky", "elderly", "eminent", "willing", "regular",
                "loutish", "merciful", "abounding", "light", "waggish", "gaping", "devotedly", "very", "safely",
                "ahead", "eventually", "punctually", "blissfully", "frankly", "hopefully", "somewhat", "personally",
                "else" };
        if (count > hardCodedWords.length)
            return null;
        LinkedList<String> ret = new LinkedList<>();
        for (int i = 0; i < count; ++i) {
            ret.addLast(hardCodedWords[index]);
            index = (index + 1) % hardCodedWords.length;
        }
        return ret;
    }
}
