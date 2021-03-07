package cn.mypro.utils.exception;

import java.sql.SQLException;

public class ConnectException extends SQLException {
    public ConnectException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }

    public ConnectException(String reason, String SQLState) {
        super(reason, SQLState);
    }

    public ConnectException(String reason) {
        super(reason);
    }

    public ConnectException() {
    }

    public ConnectException(Throwable cause) {
        super(cause);
    }

    public ConnectException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public ConnectException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public ConnectException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
