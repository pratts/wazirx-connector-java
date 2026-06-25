package wazirx.connector.java;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import wazirx.connector.java.exception.WazirxApiException;
import wazirx.connector.java.exception.WazirxClientException;
import wazirx.connector.java.handlers.WazirxResponseHandler;

public class BaseClient implements Closeable {

    private static final String BASE_URL = "https://api.wazirx.com/sapi";
    static final int RECV_WINDOW_DEFAULT = 10_000;
    static final int RECV_WINDOW_TRADING = 60_000;

    private final String apiKey;
    private final String secretKey;
    private final CloseableHttpClient httpClient;
    private final Map<String, APIDetails> apiDetails;

    public BaseClient(final String apiKey, final String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.httpClient = HttpClients.createDefault();
        this.apiDetails = loadApiDetails();
    }

    private static Map<String, APIDetails> loadApiDetails() {
        try (Reader reader = new InputStreamReader(
                BaseClient.class.getResourceAsStream("/api_mapper.json"))) {
            Type type = new TypeToken<Map<String, APIDetails>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load api_mapper.json from classpath", e);
        }
    }

    private List<Header> getHeaders(String clientType) {
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        if ("signed".equalsIgnoreCase(clientType)) {
            headers.add(new BasicHeader("X-Api-Key", this.apiKey));
        }
        return headers;
    }

    private String generateSignature(List<NameValuePair> params) {
        String encoded = URLEncodedUtils.format(params, StandardCharsets.UTF_8);
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, this.secretKey).hmacHex(encoded);
    }

    private List<NameValuePair> toNameValuePairs(Map<String, Object> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return pairs;
    }

    protected String call(String name, Map<String, Object> params) {
        APIDetails detail = this.apiDetails.get(name);
        if (detail == null) {
            throw new WazirxClientException("Unknown API endpoint: " + name);
        }
        if (params == null) {
            params = Map.of();
        }
        List<NameValuePair> pairs = this.toNameValuePairs(params);
        if ("signed".equalsIgnoreCase(detail.getClient())) {
            pairs.add(new BasicNameValuePair("signature", this.generateSignature(pairs)));
        }
        switch (detail.getAction()) {
            case "get":    return get(detail, pairs);
            case "post":   return post(detail, pairs);
            case "delete": return delete(detail, pairs);
            default: throw new WazirxClientException("Unsupported HTTP method: " + detail.getAction());
        }
    }

    private String get(APIDetails detail, List<NameValuePair> params) {
        String url = BASE_URL + detail.getUrl();
        try {
            URI uri = new URIBuilder(url).addParameters(params).build();
            HttpGet request = new HttpGet(uri);
            request.setHeaders(getHeaders(detail.getClient()).toArray(new Header[0]));
            return httpClient.execute(request, new WazirxResponseHandler());
        } catch (IOException | URISyntaxException e) {
            throw new WazirxApiException("GET request failed: " + url, e);
        }
    }

    private String post(APIDetails detail, List<NameValuePair> params) {
        String url = BASE_URL + detail.getUrl();
        try {
            HttpPost request = new HttpPost(url);
            request.setHeaders(getHeaders(detail.getClient()).toArray(new Header[0]));
            request.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            return httpClient.execute(request, new WazirxResponseHandler());
        } catch (IOException e) {
            throw new WazirxApiException("POST request failed: " + url, e);
        }
    }

    private String delete(APIDetails detail, List<NameValuePair> params) {
        String url = BASE_URL + detail.getUrl();
        try {
            URI uri = new URIBuilder(url).addParameters(params).build();
            HttpDelete request = new HttpDelete(uri);
            request.setHeaders(getHeaders(detail.getClient()).toArray(new Header[0]));
            return httpClient.execute(request, new WazirxResponseHandler());
        } catch (IOException | URISyntaxException e) {
            throw new WazirxApiException("DELETE request failed: " + url, e);
        }
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
