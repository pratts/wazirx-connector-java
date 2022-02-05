package wazirx.connector.java.handlers;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WazirxResponseHandler extends AbstractResponseHandler<JsonElement>{
	@Override
	public JsonElement handleEntity(HttpEntity entity) throws IOException {
		String responseString = EntityUtils.toString(entity);
		return JsonParser.parseString(responseString);
	}
}
