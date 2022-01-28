package wazirx.connector.java;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WazirxResponseHandler extends AbstractResponseHandler<JsonObject>{
	@Override
	public JsonObject handleEntity(HttpEntity entity) throws IOException {
		return new Gson().fromJson(EntityUtils.toString(entity), JsonObject.class);
	}
}
