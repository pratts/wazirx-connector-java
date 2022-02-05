package wazirx.connector.java;

import com.google.gson.JsonObject;

public class PingMessage implements Runnable {
	private SocketClient socketClient = null;
	public PingMessage(SocketClient client) {
		System.out.println("Creating ping message thread");
		this.socketClient = client;
	}

	public void run() {
		JsonObject pingMessage = new JsonObject();
		pingMessage.addProperty("event", "ping");
		while(this.socketClient.isSendPing()) {
			System.out.println("Sending ping message: " + pingMessage.toString());
			this.socketClient.send(pingMessage.toString());
			try {
				Thread.sleep(5 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
