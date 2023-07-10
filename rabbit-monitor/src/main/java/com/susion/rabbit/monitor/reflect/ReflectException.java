package com.susion.rabbit.monitor.reflect;

/**
 * wangpengcheng.wpc create at 2023/7/4
 */
public class ReflectException extends RuntimeException {

    private static final long serialVersionUID = -2243843843843438438L;

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException() {
        super();
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }
}
