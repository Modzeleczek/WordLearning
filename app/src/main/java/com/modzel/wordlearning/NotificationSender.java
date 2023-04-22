package com.modzel.wordlearning;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.modzel.wordlearning.database.Repository;
import com.modzel.wordlearning.database.entity.Word;

import java.util.List;
import java.util.concurrent.Executors;

// https://www.youtube.com/watch?v=1fV9NmvxXJo
public class NotificationSender extends BroadcastReceiver {
    public static final String DAILY_NOTIFICATION_ACTION = "com.modzel.wordlearning.DAILY_NOTIFICATION_ACTION";
    public static final int DAILY_NOTIFICATION_ID = 100;
    public static final String WORD_LEARNING_CHANNEL_ID = "notifyWordLearning";

    @Override
    public void onReceive(Context context, Intent receivedIntent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent selectModeStarter = new Intent(context, MainActivity.class);
        /* If the user has an open WordListActivity on top or somewhere below,
        then it will be moved to the top. */
        selectModeStarter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        /* Create PendingIntent (for later execution) with some other Intent -
        selectModeStarter which we will set using a builder in the created
        notification. Thanks to that, after clicking the notification,
        selectModeStarter Intent will be executed. */
        PendingIntent pendingIntent = PendingIntent.getActivity(context, DAILY_NOTIFICATION_ID,
                selectModeStarter, PendingIntent.FLAG_UPDATE_CURRENT);
        Executors.newSingleThreadExecutor().execute(() -> {
            Repository repo = new Repository(context);
            String message = null;
            List<Word> words = repo.getAllWords();
            if (words.isEmpty())
                message = context.getString(R.string.no_words_please_download_some);
            else {
                int learned = 0;
                for (Word w : words) {
                    if (w.getGoodAnswers() >= 3)
                        ++learned;
                }
                if (learned < words.size())
                    message = context.getString(R.string.known_words) +
                            " " + learned + "/" + words.size() + context.getString(R.string.learn_them_all);
                // The user learned all words currently stored in the database.
                else
                    message = context.getString(R.string.you_already_know_all) + " " + learned + " " +
                            context.getString(R.string.available_words_please_download_some_new_ones);
            }
            // Create the notification in multiple builder steps.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                    WORD_LEARNING_CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true);
            if (receivedIntent.getAction().equals(DAILY_NOTIFICATION_ACTION))
                manager.notify(DAILY_NOTIFICATION_ID, builder.build());
        });
    }
}
