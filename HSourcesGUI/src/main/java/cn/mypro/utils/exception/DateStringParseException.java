package cn.mypro.utils.exception;

public class DateStringParseException extends Exception {
    public DateStringParseException() {
    }

    public DateStringParseException(String message) {
        super(message);
    }

    public DateStringParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateStringParseException(Throwable cause) {
        super(cause);
    }

    public DateStringParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

