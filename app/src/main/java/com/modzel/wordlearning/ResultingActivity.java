package com.modzel.wordlearning;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ResultingActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_ID = "com.modzel.MESSAGE_ID";

    protected void finishWithSuccess(int successMessageId) {
        finishWithMessage(RESULT_OK, successMessageId);
    }

    protected void finishWithError(int errorMessageId) {
        finishWithMessage(RESULT_CANCELED, errorMessageId);
    }

    private void finishWithMessage(int resultCode, int messageId) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_MESSAGE_ID, messageId);
        setResult(resultCode, replyIntent);
        finish();
    }
}
