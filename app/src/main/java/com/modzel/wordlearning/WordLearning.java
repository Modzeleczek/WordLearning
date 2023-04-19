package com.modzel.wordlearning;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Statistic;

import java.util.concurrent.Executors;

public class WordLearning extends Application {
    public static final String CORRECT_DEFINITION_MATCHES = "CORRECT_DEFINITION_MATCHES";
    public static final String LEARNED_WORDS = "LEARNED_WORDS"; // (ile było słów z przynajmniej 3 prawidłowymi dopasowaniami)
    public static final String CORRECT_SYNONYM_MATCHES = "CORRECT_SYNONYM_MATCHES";
    public static final String DOWNLOADED_WORDS = "DOWNLOADED_WORDS";

    @Override
    public void onCreate() {
        super.onCreate();
        setupStatistics();
        setupDailyNotifications();
    }

    private void setupStatistics() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // dodajemy statystykę do bazy danych, jeżeli jeszcze jej nie ma
            Repository repo = new Repository(this);
            // repo.deleteEverything();
            String[] names = { CORRECT_DEFINITION_MATCHES, LEARNED_WORDS, CORRECT_SYNONYM_MATCHES, DOWNLOADED_WORDS };
            for (String name : names) {
                if (repo.getStatistic(name) == null)
                    repo.insert(new Statistic(name, 0));
            }
        });
    }

    // https://www.youtube.com/watch?v=nl-dheVpt8o
    private void createNotificationChannel() {
        String name = getString(R.string.notification_channel_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NotificationSender.WORD_LEARNING_CHANNEL_ID,
                name, importance);
        String description = getString(R.string.notification_channel_description);
        channel.setDescription(description);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    private void setupDailyNotifications() {
        createNotificationChannel(); // od Androida 8.0 (API 26), aby móc wysyłać powiadomienia, trzeba stworzyć kanał
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationSender.class);
        notificationIntent.setAction(NotificationSender.DAILY_NOTIFICATION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                NotificationSender.DAILY_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // tworzymy PendingIntent (do późniejszego wykonania) z innym Intentem - notificationIntentem, który
        // umieścimy jako powtarzalną akcję w AlarmManagerze, dzięki czemu będzie on wykonywany co określony interwał
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long start = System.currentTimeMillis(); // czas w milisekundach, od którego mają być mierzone interwały
        // long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES; // co ile milisekund broadcast ma być
        // wysyłany do naszej aplikacji
        long interval = 30 * 1000; // 30 sekund
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, start, interval, pendingIntent);
        // AlarmManager okresowo wykonuje operację definiowaną przez PendingIntent
    }
}