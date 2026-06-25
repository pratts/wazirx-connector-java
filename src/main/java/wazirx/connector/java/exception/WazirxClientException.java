package wazirx.connector.java.exception;

public class WazirxClientException extends WazirxApiException {

    public WazirxClientException(String message) {
        super(message);
    }

    public WazirxClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
