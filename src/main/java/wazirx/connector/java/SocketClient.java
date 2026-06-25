package wazirx.connector.java;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import wazirx.connector.java.exception.WazirxApiException;
import wazirx.connector.java.handlers.IMessageHandler;

public class SocketClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(SocketClient.class);
    private static final String BASE_URL = "wss://stream.wazirx.com/stream";

    private final Client client;
    private final IMessageHandler messageHandler;

    private volatile boolean sendPing = false;
    private Thread pingThread;
    private JsonObject authToken = null;

    public SocketClient(final String apiKey, final String secretKey, IMessageHandler messageHandler)
            throws URISyntaxException {
        super(new URI(BASE_URL));
        this.client = new Client(apiKey, secretKey);
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        sendPing = true;
        pingThread = new Thread(new PingMessage(this));
        pingThread.setDaemon(true);
        pingThread.start();
    }

    @Override
    public void onMessage(String message) {
        if (messageHandler != null) {
            messageHandler.handleMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.debug("WebSocket closed: {} (code={})", reason, code);
        stopPing();
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket error", ex);
        stopPing();
    }

    private void stopPing() {
        sendPing = false;
        if (pingThread != null) {
            pingThread.interrupt();
        }
    }

    private synchronized JsonObject refreshAuthTokenIfNeeded() {
        if (authToken != null) {
            int timeout = authToken.get("timeout_duration").getAsInt();
            long initTime = authToken.get("timestamp").getAsLong();
            if ((System.currentTimeMillis() - initTime) < ((long) timeout * 1000)) {
                return authToken;
            }
        }
        String authData = client.createAuthToken();
        JsonObject auth = JsonParser.parseString(authData).getAsJsonObject();
        if (!auth.has("auth_key")) {
            throw new WazirxApiException("Auth token response missing auth_key");
        }
        auth.addProperty("timestamp", System.currentTimeMillis());
        authToken = auth;
        return authToken;
    }

    private synchronized void sendMessage(String streamName, String[] streams, boolean isAuth) {
        JsonObject message = new JsonObject();
        message.addProperty("event", streamName);
        JsonArray streamsList = new JsonArray();
        for (String stream : streams) {
            streamsList.add(stream);
        }
        message.add("streams", streamsList);
        if (isAuth) {
            JsonObject token = refreshAuthTokenIfNeeded();
            message.add("auth_key", token.get("auth_key"));
        }
        this.send(message.toString());
    }

    private void subscribe(String[] streams, boolean isAuth) {
        sendMessage("subscribe", streams, isAuth);
    }

    public void unsubscribe(String[] streams, boolean isAuth) {
        sendMessage("unsubscribe", streams, isAuth);
    }

    public boolean isSendPing() {
        return sendPing;
    }

    public void subToSymbolTrade(String symbolName) {
        subscribe(new String[]{symbolName + "@trades"}, false);
    }

    public void subToMarket() {
        subscribe(new String[]{"!ticker@arr"}, false);
    }

    public void subToMarketDepth(String symbolName) {
        subscribe(new String[]{symbolName + "@depth"}, false);
    }

    public void subToAccountUpdate() {
        subscribe(new String[]{"outboundAccountPosition"}, true);
    }

    public void subToOrderUpdate() {
        subscribe(new String[]{"orderUpdate"}, true);
    }

    public void subToOwnTrade() {
        subscribe(new String[]{"ownTrade"}, true);
    }
}
