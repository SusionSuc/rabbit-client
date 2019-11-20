package com.susion.devtools;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.config.RabbitConfig;

/**
 * susionwang at 2019-11-14
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RabbitConfig myConfig = new RabbitConfig();

        RabbitConfig.TraceConfig traceConfig = new RabbitConfig.TraceConfig();
        traceConfig.setHomeActivityName(MainActivity.class.getName());
        myConfig.setTraceConfig(traceConfig);
        Rabbit.init(this,myConfig);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("attachBaseContext", "called !!!");
    }

}
