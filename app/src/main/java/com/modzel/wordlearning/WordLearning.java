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
    // How many were there words with at least 3 correct definiton matches.
    public static final String LEARNED_WORDS = "LEARNED_WORDS";
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
            // If the statistic does not exist in the the database yet, add it.
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
        /* Since Android 8.0 (API 26), a channel must be created in order to
        send notifications. */
        createNotificationChannel();
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationSender.class);
        notificationIntent.setAction(NotificationSender.DAILY_NOTIFICATION_ACTION);
        /* Create PendingIntent (for later execution) with some other Intent -
        notificationIntent which we will set as a repetitive action
        in AlarmManager. Thanks to that, notificationIntent will be executed
        every specified time (interval). */
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                NotificationSender.DAILY_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        /* A point in time in milliseconds, from which intervals should be
        measured. */
        long start = System.currentTimeMillis();
        /* Every how many milliseconds a broadcast should be sent to
        WordLearning application. */
        long interval = 30 * 1000; // 30 seconds
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, start, interval, pendingIntent);
        /* AlarmManager periodically executes the operation defined by
        PendingIntent. */
    }
}