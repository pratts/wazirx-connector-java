package wazirx.connector.java.handlers;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

public class WazirxResponseHandler extends AbstractResponseHandler<String>{
	@Override
	public String handleEntity(HttpEntity entity) throws IOException {
		return EntityUtils.toString(entity);
	}
}
