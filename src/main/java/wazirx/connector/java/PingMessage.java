package wazirx.connector.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PingMessage implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingMessage.class);
    private static final long PING_INTERVAL_MS = 5 * 60 * 1000L;
    private static final String PING_PAYLOAD = "{\"event\":\"ping\"}";

    private final SocketClient socketClient;

    PingMessage(SocketClient client) {
        this.socketClient = client;
    }

    @Override
    public void run() {
        log.debug("Ping thread started");
        while (socketClient.isSendPing()) {
            log.debug("Sending ping");
            socketClient.send(PING_PAYLOAD);
            try {
                Thread.sleep(PING_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.debug("Ping thread stopped");
    }
}
