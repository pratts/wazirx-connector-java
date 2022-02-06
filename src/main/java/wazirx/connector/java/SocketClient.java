package wazirx.connector.java;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import wazirx.connector.java.handlers.IMessageHandler;

public class SocketClient extends WebSocketClient {
	private final static String BASE_URL = "wss://stream.wazirx.com/stream";
	private Client client = null;
	private PingMessage pingMessage = null;
	private boolean sendPing = false;
	private IMessageHandler messageHandler = null;
	private JsonObject authToken = null;
	
	public SocketClient(final String apiKey, final String secretKey, IMessageHandler messageHandler) throws URISyntaxException {
		super(new URI(BASE_URL));
		this.client = new Client(apiKey, secretKey);
		this.messageHandler = messageHandler;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		this.sendPing = true;
		this.pingMessage = new PingMessage(this);
        Thread t = new Thread(this.pingMessage);
        t.start();
	}

	@Override
	public void onMessage(String message) {
		if(this.messageHandler != null) {
			this.messageHandler.handleMessage(message);
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		this.sendPing = false;
	}

	@Override
	public void onError(Exception ex) {
		this.sendPing = false;
	}
	
	private JsonObject getAuthToken() throws Exception {
		String authData = this.client.createAuthToken();
		JsonObject auth = JsonParser.parseString(authData).getAsJsonObject();
		if(!auth.has("auth_key")) {
			throw new Exception("No authentication token provided !");
		}
		auth.addProperty("timestamp", System.currentTimeMillis());
		return auth;
	}

	private void sendMessage(String streamName, String[] streams, boolean isAuth) throws Exception {
		JsonObject message = new JsonObject();
		message.addProperty("event", streamName);
		JsonArray streamsList = new JsonArray();
		for(int i=0; i<streams.length; i++) {
			streamsList.add(streams[i]);
		}
		message.add("streams", streamsList);
		if(isAuth) {
			boolean isNewTokenNeeded = true;
			if(this.authToken != null) {
				int timeout = this.authToken.get("timeout_duration").getAsInt();
				long initTime = this.authToken.get("timestamp").getAsLong();
				if((System.currentTimeMillis() - initTime) < (timeout*1000)) {
					isNewTokenNeeded = false;
				}
			}
			if(isNewTokenNeeded) {
				this.authToken = this.getAuthToken();
			}
			message.add("auth_key", this.authToken.get("auth_key"));
		}
		this.send(message.toString());
	}

	private void subscribe(String[] streams, boolean isAuth) throws Exception {
		this.sendMessage("subscribe", streams, isAuth);
	}

	public void unsubscribe(String[] streams, boolean isAuth) throws Exception {
		this.sendMessage("unsubscribe", streams, isAuth);
	}

	public boolean isSendPing() {
		return this.sendPing;
	}

	public void subToSymbolTrade(String symbolName) throws Exception {
		this.subscribe(new String[] {symbolName+"@trades"}, false);
	}

	public void subToMarket() throws Exception {
		this.subscribe(new String[] {"!ticker@arr"}, false);
	}

	public void subToMarketDepth(String symbolName) throws Exception {
		this.subscribe(new String[] {symbolName+"@depth"}, false);
	}

	public void subToAccountUpdate() throws Exception {
		this.subscribe(new String[] {"outboundAccountPosition"}, true);
	}

	public void subToOrderUpdate() throws Exception {
		this.subscribe(new String[] {"orderUpdate"}, true);
	}

	public void subToOwnTrade() throws Exception {
		this.subscribe(new String[] {"ownTrade"}, true);
	}
}
