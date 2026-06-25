package wazirx.connector.java.handlers;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import wazirx.connector.java.exception.WazirxApiException;

public class WazirxResponseHandler extends AbstractResponseHandler<String> {

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String body = entity != null ? EntityUtils.toString(entity) : "";
        if (status < 200 || status >= 300) {
            throw new WazirxApiException("HTTP " + status + ": " + body);
        }
        return body;
    }

    @Override
    public String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }
}
