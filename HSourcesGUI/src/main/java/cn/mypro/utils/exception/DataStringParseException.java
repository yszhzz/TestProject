package cn.mypro.utils.exception;

public class DataStringParseException extends Exception {
    public DataStringParseException() {
    }

    public DataStringParseException(String message) {
        super(message);
    }

    public DataStringParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStringParseException(Throwable cause) {
        super(cause);
    }

    public DataStringParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

