package cn.mypro.utils.exception;

public class GetMCodeException extends Exception {
    public GetMCodeException() {
    }

    public GetMCodeException(String message) {
        super(message);
    }

    public GetMCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetMCodeException(Throwable cause) {
        super(cause);
    }

    public GetMCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
