package wazirx.connector.java;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import wazirx.connector.java.handlers.IMessageHandler;
public class SocketClient extends WebSocketClient {
	private final static String BASE_URL = "wss://stream.wazirx.com/stream";
	private Client client = null;
	private PingMessage pingMessage = null;
	private boolean sendPing = false;
	private IMessageHandler messageHandler = null;
	
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
	
	public void subscribe(String[] streams) {
		JsonObject message = new JsonObject();
		message.addProperty("event", "subscribe");
		JsonArray streamsList = new JsonArray();
		for(int i=0; i<streams.length; i++) {
			streamsList.add(streams[i]);
		}
		message.add("streams", streamsList);
		this.send(message.toString());
	}

	public void unsubscribe(String[] streams) {
		JsonObject message = new JsonObject();
		message.addProperty("event", "unsubscribe");
		JsonArray streamsList = new JsonArray();
		for(int i=0; i<streams.length; i++) {
			streamsList.add(streams[i]);
		}
		message.add("streams", streamsList);
		this.send(message.toString());
	}

	private class PingMessage implements Runnable {
		private SocketClient socketClient = null;
		public PingMessage(SocketClient client) {
			this.socketClient = client;
		}
		
		public void run() {
			JsonObject pingMessage = new JsonObject();
			pingMessage.addProperty("event", "ping");
			while(this.socketClient.sendPing) {
				this.socketClient.send(pingMessage.toString());
				try {
					Thread.sleep(5 * 60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
