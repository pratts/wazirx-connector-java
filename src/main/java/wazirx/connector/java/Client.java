/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package wazirx.connector.java;

import java.util.HashMap;
import java.util.Map;

public class Client extends BaseClient {
	public Client(final String apiKey, final String secretKey) {
		super(apiKey, secretKey);
	}

	//	ping
	public String ping() throws Exception {
		return this.call("ping", null);
	}

	//	time
	public String time() throws Exception {
		return this.call("time", null);
	}

	//	system_status
	public String systemStatus() throws Exception {
		return this.call("system_status", null);
	}

	//	exchange_info
	public String exchangeInfo() throws Exception {
		return this.call("exchange_info", null);
	}

	//	tickers
	// handle case for array response
	public String tickers() throws Exception {
		return this.call("tickers", null);
	}

	//	ticker
	public String ticker(String symbol) throws Exception {
		Map<String, Object> params = Map.of("symbol", symbol);
		return this.call("ticker", params);
	}

	//	depth
	public String depth(String symbol, int limit) throws Exception {
		Map<String, Object> params = Map.of("symbol", symbol, "limit", limit);
		return this.call("depth", params);
	}

	//	trades
	public String trades(String symbol, int limit) throws Exception {
		Map<String, Object> params = Map.of("symbol", symbol, "limit", limit);
		return this.call("trades", params);
	}

	//	historical_trades
	public String historicalTrades(String symbol, int limit) throws Exception {
		Map<String, Object> params = Map.of(
				"symbol", symbol,
				"limit", limit,
				"recvWindow", 10000,
				"timestamp", System.currentTimeMillis());
		return this.call("historical_trades", params);
	}

	private Map<String, Object> getCreateOrderData(String symbolName, String type, Double quantity, Double price, Double stopPrice) throws Exception {
		if(type == null || !(type.equalsIgnoreCase("limit") || type.equalsIgnoreCase("stop_limit"))) {
			throw new Exception("Invalid type provided");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbol", symbolName);
		params.put("side", "buy");
		params.put("type", type);
		params.put("recvWindow", 10000);
		params.put("timestamp", System.currentTimeMillis());
		if(type.equalsIgnoreCase("limit")) {
			if(quantity == null || price == null) {
				throw new Exception("Both quantity and price should be non null");
			}
			params.put("quantity", quantity);
			params.put("price", price);
		} else if(type.equalsIgnoreCase("stop_limit")) {
			if(quantity == null || price == null || stopPrice == null) {
				throw new Exception("Quantity, price and stopPrice should be non null");
			}
			params.put("quantity", quantity);
			params.put("price", price);
			params.put("stopPrice", stopPrice);
		}
		return params;
	}

	//	create_order
	public String createOrder(String symbolName, String type, Double quantity, Double price, Double stopPrice) throws Exception {
		return this.call("create_order", this.getCreateOrderData(symbolName, type, quantity, price, stopPrice));
	}

	//	create_test_order
	public String createTestOrder(String symbolName, String type, Double quantity, Double price, Double stopPrice) throws Exception {
		return this.call("create_test_order", this.getCreateOrderData(symbolName, type, quantity, price, stopPrice));
	}

	//	query_order
	public String queryOrder(long orderId) throws Exception {
		Map<String, Object> params = Map.of(
				"orderId", orderId,
				"recvWindow", 60000,
				"timestamp", System.currentTimeMillis());
		return this.call("query_order", params);
	}

	//	open_orders
	public String openOrders(String symbolName, Long orderId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recvWindow", 60000);
		params.put("timestamp", System.currentTimeMillis());
		if(symbolName != null && symbolName.length() > 0) {
			params.put("symbol", symbolName);
		}
		if(orderId != null) {
			params.put("orderId", orderId);
		}
		return this.call("open_orders", params);
	}

	//	all_orders
	public String allOrders(String symbolName, Long orderId, Long startTime, Long endTime, Integer limit) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbol", symbolName);
		params.put("recvWindow", 60000);
		params.put("timestamp", System.currentTimeMillis());
		if(orderId != null) {
			params.put("orderId", orderId);
		}
		if(startTime != null) {
			params.put("startTime", startTime);
		}
		if(endTime != null) {
			params.put("endTime", endTime);
		}
		if(limit != null) {
			params.put("limit", limit);
		}
		return this.call("all_orders", params);
	}

	//	cancel_order
	public String cancelOrder(String symbolName, long orderId) throws Exception {
		Map<String, Object> params = Map.of(
				"symbol", symbolName,
				"orderId", orderId,
				"recvWindow", 60000,
				"timestamp", System.currentTimeMillis());
		return this.call("cancel_order", params);
	}

	//	cancel_open_orders
	public String cancelOpenOrders(String symbolName) throws Exception {
		Map<String, Object> params = Map.of(
				"symbol", symbolName,
				"recvWindow", 60000,
				"timestamp", System.currentTimeMillis());
		return this.call("cancel_open_orders", params);
	}

	//	account_info
	public String accountInfo() throws Exception {
		Map<String, Object> params = Map.of(
				"recvWindow", 20000,
				"timestamp", System.currentTimeMillis());
		return this.call("account_info", params);
	}

	//	funds_info
	public String fundsInfo() throws Exception {
		Map<String, Object> params = Map.of(
				"recvWindow", 20000,
				"timestamp", System.currentTimeMillis());
		return this.call("funds_info", params);
	}

	//	create_auth_token
	public String createAuthToken() throws Exception {
		Map<String, Object> params = Map.of(
				"recvWindow", 20000,
				"timestamp", System.currentTimeMillis());
		return this.call("create_auth_token", params);
	}
}
