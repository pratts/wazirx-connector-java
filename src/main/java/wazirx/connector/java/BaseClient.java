package wazirx.connector.java;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class BaseClient {
	private final String BASE_URL = "https://api.wazirx.com";
	private String apiKey = null;
	private String secretKey = null;
	private Map<String, APIDetails> apiDetails = null;
	private final String GET = "get";
	private final String POST = "post";
	private final String DELETE = "delete";
	
	public BaseClient(final String apiKey, final String secretKey) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		apiDetails = this.readApiMapperJson();
	}
	
	public Map<String, APIDetails> readApiMapperJson() {
		System.out.println("Reading json file");
		Map<String, APIDetails> apiDetails = null;
	    try (Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/api_mapper.json"))) {
	    	Type apiDetailsType = new TypeToken<Map<String, APIDetails>>() {}.getType();
	    	apiDetails = new Gson().fromJson(reader, apiDetailsType);
	        System.out.println(apiDetails);
	        return apiDetails;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return apiDetails;
	}

	public APIDetails getAPIDetails(String name) {
		return this.apiDetails.get(name);
	}
	
	public List<Header> getHeaders(String clientType) {
		List<Header> headersList = new ArrayList<Header>();
		headersList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
		if(clientType == "signed") {
			headersList.add(new BasicHeader("X-Api-Key", this.apiKey));
		}
		return headersList;
	}
	
	public String generateSignature(Map<String, Object> params) {
		String encodedParams = this.getEncodedParams(params);
		String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, this.secretKey).hmacHex(encodedParams);
	    return hmac;
	}
	
	public List<NameValuePair> getValuePairs(Map<String, Object> params) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for(Map.Entry<String, Object> it: params.entrySet()) {
			pairs.add(new BasicNameValuePair(it.getKey(), String.valueOf(it.getValue())));
		}
		return pairs;
	}
	
	public String getEncodedParams(Map<String, Object> params) {
		return URLEncodedUtils.format(this.getValuePairs(params), "UTF-8");
	}
	
	public JsonObject call(APIDetails detail, Map<String, Object> params) throws Exception {
		JsonObject response = null;
		if(detail.getClient() == "signed") {
			String signature = this.generateSignature(params);
			params.put("signature", signature);
		}
		switch(detail.getAction()) {
			case GET: response = this.get(detail, params);
					break;
			case POST: response = this.post(detail, params);
					break;
			case DELETE: response = this.delete(detail, params);
				break;
			default: throw new Exception("Invalid API method");
		}
		return response;
	}
	
	public JsonObject get(APIDetails detail, Map<String, Object> params) throws IOException, URISyntaxException {
		List<Header> headers = this.getHeaders(detail.getClient());
		String url = this.BASE_URL + detail.getUrl();

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet request = new HttpGet(url);
            request.setHeaders((Header[])headers.toArray());
            URI uri = new URIBuilder(url+"?"+this.getEncodedParams(params)).build();
            request.setURI(uri);

            System.out.println("Executing request " + request.getMethod() + " " + request.getURI());
            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					return EntityUtils.toString(response.getEntity());
				}
            };
            final String responseBody = httpclient.execute(request, responseHandler);
            JsonObject response = new Gson().fromJson(responseBody, JsonObject.class);
            return response;
        } catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public JsonObject post(APIDetails detail, Map<String, Object> params) throws IOException, URISyntaxException {
		List<Header> headers = this.getHeaders(detail.getClient());
		String url = this.BASE_URL + detail.getUrl();

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost request = new HttpPost(url);
            request.setHeaders((Header[])headers.toArray());
            request.setEntity(new UrlEncodedFormEntity(this.getValuePairs(params)));
            URI uri = new URIBuilder(url+"?"+this.getEncodedParams(params)).build();
            request.setURI(uri);

            System.out.println("Executing request " + request.getMethod() + " " + request.getURI());
            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					return EntityUtils.toString(response.getEntity());
				}
            };
            final String responseBody = httpclient.execute(request, responseHandler);
            JsonObject response = new Gson().fromJson(responseBody, JsonObject.class);
            return response;
        } catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public JsonObject delete(APIDetails detail, Map<String, Object> params) throws IOException, URISyntaxException {
		List<Header> headers = this.getHeaders(detail.getClient());
		String url = this.BASE_URL + detail.getUrl();

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpRequestBase httpget = new HttpGet(url);
            httpget.setHeaders((Header[])headers.toArray());
            URI uri = new URIBuilder(url+"?"+this.getEncodedParams(params)).build();
            httpget.setURI(uri);

            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getURI());
            final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					return EntityUtils.toString(response.getEntity());
				}
            };
            final String responseBody = httpclient.execute(httpget, responseHandler);
            JsonObject response = new Gson().fromJson(responseBody, JsonObject.class);
            return response;
        } catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
}