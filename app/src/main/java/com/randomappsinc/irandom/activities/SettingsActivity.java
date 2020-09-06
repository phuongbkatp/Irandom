package com.randomappsinc.irandom.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.irandom.R;
import com.randomappsinc.irandom.adapters.SettingsAdapter;
import com.randomappsinc.irandom.persistence.PreferencesManager;
import com.randomappsinc.irandom.theme.ThemeManager;
import com.randomappsinc.irandom.utils.SimpleDividerItemDecoration;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends StandardActivity implements SettingsAdapter.ItemSelectionListener {

    public static final String SUPPORT_EMAIL = "RandomAppsInc61@gmail.com";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/dev?id=9093438553713389916";
    public static final String REPO_URL = "https://github.com/Gear61/Random-Number-Generator";

    @BindView(R.id.settings_options)
    RecyclerView settingsOptions;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    private PreferencesManager preferencesManager;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarColors();

        preferencesManager = new PreferencesManager(this);
        preferencesManager.setShouldTeachAboutDarkMode(false);
        themeManager = ThemeManager.get();

        settingsOptions.addItemDecoration(new SimpleDividerItemDecoration(this));
        settingsOptions.setAdapter(new SettingsAdapter(this, this));
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                View firstCell = settingsOptions.getChildAt(0);
                Switch shakeToggle = firstCell.findViewById(R.id.toggle);
                boolean shakeStatus = shakeToggle.isChecked();
                shakeToggle.setChecked(!shakeStatus);
                preferencesManager.setShakeEnabled(!shakeStatus);
                return;
            case 1:
                View secondCell = settingsOptions.getChildAt(1);
                Switch soundToggle = secondCell.findViewById(R.id.toggle);
                boolean soundsEnabled = soundToggle.isChecked();
                soundToggle.setChecked(!soundsEnabled);
                preferencesManager.setPlaySounds(!soundsEnabled);
                return;
            case 2:
                View thirdCell = settingsOptions.getChildAt(2);
                Switch darkThemeToggle = thirdCell.findViewById(R.id.toggle);
                boolean darkThemeEnabled = darkThemeToggle.isChecked();
                darkThemeToggle.setChecked(!darkThemeEnabled);
                themeManager.setDarkModeEnabled(this, !darkThemeEnabled);
                return;
            case 3:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 4:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 5:
                Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    Toast.makeText(this, R.string.play_store_error, Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case 6:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        startActivity(intent);
    }
}
