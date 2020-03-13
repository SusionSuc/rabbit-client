package com.susion.rabbit.demo;

import android.app.Application;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.base.config.RabbitConfig;

/**
 * susionwang at 2020-03-12
 */
public class TestApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        RabbitConfig config = new RabbitConfig();

        Rabbit.INSTANCE.init(this, config);
    }
}
