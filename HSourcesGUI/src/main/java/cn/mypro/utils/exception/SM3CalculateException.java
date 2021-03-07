package cn.mypro.utils.exception;

public class SM3CalculateException extends Exception {
    public SM3CalculateException() {
    }

    public SM3CalculateException(String message) {
        super(message);
    }

    public SM3CalculateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SM3CalculateException(Throwable cause) {
        super(cause);
    }

    public SM3CalculateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
