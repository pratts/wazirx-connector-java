package wazirx.connector.java;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class BaseClient {
	private final String BASE_URL = "https://api.wazirx.com";
	private String apiKey = null;
	private String secretKey = null;
	private JsonObject mappings = null;
	
	public BaseClient(final String apiKey, final String secretKey) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		mappings = this.readApiMapperJson();
	}
	
	public JsonObject readApiMapperJson() {
		System.out.println("Reading json file");
		JsonObject mappings = null;
	    try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/api_mapper.json"))) {
	        mappings = new Gson().fromJson(reader, JsonObject.class);
	        System.out.println(mappings);
	        return mappings;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return mappings;
	}
	
	public Map<String, Object> getHeaders(String clientType) {
		Map<String, Object> headers = Map.of(
			"Content-Type", "application/x-www-form-urlencoded"
		);
		if(clientType == "signed") {
			headers.put("X-Api-Key", this.getApiKey());
		}
		return headers;
	}
	
	public JsonObject get(String api, Map<String, Object> params) {
		return mappings;
	}
	
	public JsonObject post(String api, Map<String, Object> params) {
		return mappings;
	}
	
	public JsonObject put(String api, Map<String, Object> params) {
		return mappings;
	}
	
	public JsonObject delete(String api, Map<String, Object> params) {
		return mappings;
	}
	
	public String getBASE_URL() {
		return BASE_URL;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public JsonObject getMappings() {
		return mappings;
	}
}
