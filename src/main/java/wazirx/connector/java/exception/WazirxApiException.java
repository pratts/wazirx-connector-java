package wazirx.connector.java.exception;

public class WazirxApiException extends RuntimeException {

    public WazirxApiException(String message) {
        super(message);
    }

    public WazirxApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
