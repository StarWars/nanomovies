package pl.michalstawarz.projectone_v2.Helpers;

import android.app.Application;
import android.content.Context;

/**
 * Created by propr_000 on 18.08.2015.
 */
public class MoviesApp extends Application {
    private static MoviesApp sInstance;

    public static Context getAppContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
