package com.susion.rabbit.demo;

import android.os.Bundle;
import android.view.View;

import com.susion.rabbit.base.ui.view.RabbitActionBar;

public class MethodCostTraceActivity extends RabbitBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_cost_trace);
        ((RabbitActionBar) findViewById(R.id.mSlowMethodActionBar)).setTitle("慢函数测试");
        ((RabbitActionBar) findViewById(R.id.mSlowMethodActionBar)).setActionListener(new RabbitActionBar.ActionListener() {
            @Override
            public void onBackClick() {
                MethodCostTraceActivity.this.finish();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    asyncMethod();
                } catch (Exception e) {

                }

            }
        }, "test-1");

        try {
            syncMethod();
        } catch (Exception e) {

        }
    }

    public void asyncMethod() throws Exception {
        int a = 0;
        int b = 1;
        int c = a + b;
        Thread.sleep(1000);
    }

    public void syncMethod() throws Exception {
        int a = 0;
        int b = 1;
        int c = a + b;
        Thread.sleep(1000);
    }

}
