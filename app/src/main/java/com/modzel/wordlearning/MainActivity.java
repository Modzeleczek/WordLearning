package com.modzel.wordlearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    public static final int DOWNLOAD_ACTIVITY_REQUEST_CODE = 1;
    private Fragment selectMode;
    private Fragment statistics;
    private static final String KEY_IS_SELECTED_MODE_ACTIVE = "isSelectedModeActive";
    private boolean isSelectModeActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set this as android:label in manifest.
        // setTitle(R.string.app_name);
        if (savedInstanceState == null)
            isSelectModeActive = true;
        else
            isSelectModeActive = savedInstanceState.getBoolean(KEY_IS_SELECTED_MODE_ACTIVE);

        selectMode = new SelectModeFragment();
        statistics = new StatisticsFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_frame_layout, isSelectModeActive ? selectMode : statistics)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_SELECTED_MODE_ACTIVE, isSelectModeActive);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_mode_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statistics_menu_item:
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                // https://stackoverflow.com/a/17488542
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                // Statistics fragment is invisible so show it.
                if (isSelectModeActive)
                    ft.replace(R.id.fragment_frame_layout, statistics);
                else // Statistics fragment is visible so hide it.
                    ft.replace(R.id.fragment_frame_layout, selectMode);
                isSelectModeActive = !isSelectModeActive;
                /* Do not add to back stack so back button does not restore the
                previous fragment. */
                // ft.addToBackStack(null);
                ft.commit();
                return true;
            case R.id.download_new_words_menu_item:
                askWhetherDownload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void askWhetherDownload() {
        AlertDialog.Builder aDB = new AlertDialog.Builder(MainActivity.this);
        aDB.setMessage(R.string.word_replace_warning);
        aDB.setCancelable(true);
        aDB.setPositiveButton(R.string.ok, (dialog, id) -> {
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivityForResult(intent, DOWNLOAD_ACTIVITY_REQUEST_CODE);
            dialog.cancel();
        });
        aDB.setNegativeButton(R.string.cancel, (dialog, id) -> {
            dialog.cancel();
        });
        AlertDialog dialog = aDB.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /* This method is called when an activity started from MainActivity
        (this) using startActivityForResult returns a result. */
        super.onActivityResult(requestCode, resultCode, data);
        /* If the returning activity is not subclass of ResultingActivity
        so it does not end with a replyIntent (see
        ResultingActivity.finishWithMessage). */
        if (data == null)
            return;
        int messageId = data.getIntExtra(ResultingActivity.EXTRA_MESSAGE_ID, -1);
        Snackbar.make(findViewById(R.id.main_activity_layout), messageId, Snackbar.LENGTH_LONG).show();
    }
}