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
	
	public SocketClient(final String apiKey, final String secretKey) throws URISyntaxException {
		super(new URI(BASE_URL));
		this.client = new Client(apiKey, secretKey);
	}
	
	public void connect() {
        super.connect();
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		this.pingMessage = new PingMessage(this);
        Thread t = new Thread(this.pingMessage);
        t.start();
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		
	}
	
	private class PingMessage implements Runnable {
		private SocketClient socketClient = null;
		public PingMessage(SocketClient client) {
			this.socketClient = client;
		}
		
		public void run() {
			this.socketClient.sendPing();
			JsonObject pingMessage = new JsonObject();
			pingMessage.addProperty("event", "ping");
			while(true) {
				this.socketClient.send(pingMessage.getAsString());
				try {
					Thread.sleep(5 * 60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
