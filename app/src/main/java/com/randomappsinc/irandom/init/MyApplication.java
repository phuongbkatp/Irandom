package com.randomappsinc.irandom.init;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.randomappsinc.irandom.theme.ThemeManager;
import com.randomappsinc.irandom.utils.PreferencesManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule())
                .with(new FontAwesomeModule());
        PreferencesManager preferencesManager = new PreferencesManager(this);
        ThemeManager.applyTheme(preferencesManager.getThemeMode());
    }
}
