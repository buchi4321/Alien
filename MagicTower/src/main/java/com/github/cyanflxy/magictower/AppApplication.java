package com.github.cyanflxy.magictower;

import android.app.Application;
import android.content.Context;

import com.cyanflxy.game.widget.FightResultToast;
import com.cyanflxy.game.widget.MessageToast;
import com.squareup.leakcanary.LeakCanary;

public class AppApplication extends Application {

    public static Context baseContext;

    @Override
    public void onCreate() {
        super.onCreate();
        baseContext = this.getApplicationContext();
        LeakCanary.install(this);

        MessageToast.initToast();
        FightResultToast.initToast();
    }
}
