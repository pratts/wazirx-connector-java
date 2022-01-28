package wazirx.connector.java;

public class APIDetails {
	private String client;
	private String action;
	private String endpoint;
	private String url;

	public APIDetails(String client, String action, String endpoint, String url) {
		this.client = client;
		this.action = action;
		this.endpoint = endpoint;
		this.url = url;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
