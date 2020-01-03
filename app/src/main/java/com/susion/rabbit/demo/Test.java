package com.susion.rabbit.demo;

/**
 * susionwang at 2020-01-02
 */
public class Test {

    public int test() {

        return 0;
    }


    public static void testStaticFun() {
        int a = 0;
        int b = 1;
        int c = a + b;
    }

    public static void asyncMethod() throws Exception {
        int a = 0;
        int b = 1;
        int c = a + b;
        Thread.sleep(1000);
    }

    public static void syncMethod() throws Exception {
        int a = 0;
        int b = 1;
        int c = a + b;
        Thread.sleep(1000);
    }
}
