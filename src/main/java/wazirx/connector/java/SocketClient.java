package wazirx.connector.java;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonObject;

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
		// TODO Auto-generated method stub
		
		if(this.messageHandler != null) {
			this.messageHandler.handleMessage(message);
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		this.sendPing = false;
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		this.sendPing = false;
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
