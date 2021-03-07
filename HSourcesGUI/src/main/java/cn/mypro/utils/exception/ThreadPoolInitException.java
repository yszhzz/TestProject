package cn.mypro.utils.exception;

public class ThreadPoolInitException extends Exception {
    public ThreadPoolInitException() {
    }

    public ThreadPoolInitException(String message) {
        super(message);
    }

    public ThreadPoolInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreadPoolInitException(Throwable cause) {
        super(cause);
    }

    public ThreadPoolInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

