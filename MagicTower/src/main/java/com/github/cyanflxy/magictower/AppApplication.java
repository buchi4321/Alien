package com.github.cyanflxy.magictower;

import android.app.Application;
import android.content.Context;

public class AppApplication extends Application {

    public static Context baseContext;

    @Override
    public void onCreate() {
        super.onCreate();
        baseContext = this.getApplicationContext();
    }
}
