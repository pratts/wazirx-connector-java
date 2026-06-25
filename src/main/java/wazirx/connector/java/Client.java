package wazirx.connector.java;

import java.util.HashMap;
import java.util.Map;

import wazirx.connector.java.exception.WazirxClientException;

public class Client extends BaseClient {

    public Client(final String apiKey, final String secretKey) {
        super(apiKey, secretKey);
    }

    public String ping() {
        return call("ping", null);
    }

    public String time() {
        return call("time", null);
    }

    public String systemStatus() {
        return call("system_status", null);
    }

    public String exchangeInfo() {
        return call("exchange_info", null);
    }

    public String tickers() {
        return call("tickers", null);
    }

    public String ticker(String symbol) {
        return call("ticker", Map.of("symbol", symbol));
    }

    public String depth(String symbol, int limit) {
        return call("depth", Map.of("symbol", symbol, "limit", limit));
    }

    public String trades(String symbol, int limit) {
        return call("trades", Map.of("symbol", symbol, "limit", limit));
    }

    public String klines(String symbol, String interval, Integer limit, Long startTime, Long endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("interval", interval);
        if (limit != null)     params.put("limit", limit);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null)   params.put("endTime", endTime);
        return call("klines", params);
    }

    public String historicalTrades(String symbol, int limit) {
        return call("historical_trades", Map.of(
                "symbol", symbol,
                "limit", limit,
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }

    public String createOrder(String symbol, String side, String type, Double quantity, Double price, Double stopPrice) {
        return call("create_order", buildOrderParams(symbol, side, type, quantity, price, stopPrice));
    }

    public String createTestOrder(String symbol, String side, String type, Double quantity, Double price, Double stopPrice) {
        return call("create_test_order", buildOrderParams(symbol, side, type, quantity, price, stopPrice));
    }

    private Map<String, Object> buildOrderParams(String symbol, String side, String type,
            Double quantity, Double price, Double stopPrice) {
        if (side == null || !(side.equalsIgnoreCase("buy") || side.equalsIgnoreCase("sell"))) {
            throw new WazirxClientException("side must be 'buy' or 'sell', got: " + side);
        }
        if (type == null || !(type.equalsIgnoreCase("limit") || type.equalsIgnoreCase("stop_limit"))) {
            throw new WazirxClientException("type must be 'limit' or 'stop_limit', got: " + type);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("side", side);
        params.put("type", type);
        params.put("recvWindow", RECV_WINDOW_DEFAULT);
        params.put("timestamp", System.currentTimeMillis());
        if (type.equalsIgnoreCase("limit")) {
            if (quantity == null || price == null) {
                throw new WazirxClientException("quantity and price are required for limit orders");
            }
            params.put("quantity", quantity);
            params.put("price", price);
        } else {
            if (quantity == null || price == null || stopPrice == null) {
                throw new WazirxClientException("quantity, price and stopPrice are required for stop_limit orders");
            }
            params.put("quantity", quantity);
            params.put("price", price);
            params.put("stop_price", stopPrice);
        }
        return params;
    }

    public String queryOrder(long orderId) {
        return call("query_order", Map.of(
                "orderId", orderId,
                "recvWindow", RECV_WINDOW_TRADING,
                "timestamp", System.currentTimeMillis()));
    }

    public String openOrders(String symbol, Long orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("recvWindow", RECV_WINDOW_TRADING);
        params.put("timestamp", System.currentTimeMillis());
        if (symbol != null && !symbol.isBlank()) params.put("symbol", symbol);
        if (orderId != null)                     params.put("orderId", orderId);
        return call("open_orders", params);
    }

    public String allOrders(String symbol, Long orderId, Long startTime, Long endTime, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("recvWindow", RECV_WINDOW_TRADING);
        params.put("timestamp", System.currentTimeMillis());
        if (orderId != null)   params.put("orderId", orderId);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null)   params.put("endTime", endTime);
        if (limit != null)     params.put("limit", limit);
        return call("all_orders", params);
    }

    public String cancelOrder(String symbol, long orderId) {
        return call("cancel_order", Map.of(
                "symbol", symbol,
                "orderId", orderId,
                "recvWindow", RECV_WINDOW_TRADING,
                "timestamp", System.currentTimeMillis()));
    }

    public String cancelOpenOrders(String symbol) {
        return call("cancel_open_orders", Map.of(
                "symbol", symbol,
                "recvWindow", RECV_WINDOW_TRADING,
                "timestamp", System.currentTimeMillis()));
    }

    public String myTrades(String symbol, Long orderId, Long fromId, Long startTime, Long endTime, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("recvWindow", RECV_WINDOW_TRADING);
        params.put("timestamp", System.currentTimeMillis());
        if (symbol != null && !symbol.isBlank()) params.put("symbol", symbol);
        if (orderId != null)   params.put("orderId", orderId);
        if (fromId != null)    params.put("fromId", fromId);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null)   params.put("endTime", endTime);
        if (limit != null)     params.put("limit", limit);
        return call("my_trades", params);
    }

    public String accountInfo() {
        return call("account_info", Map.of(
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }

    public String fundsInfo() {
        return call("funds_info", Map.of(
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }

    public String coinInfo() {
        return call("coin_info", Map.of(
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }

    public String depositAddress(String coin, String network) {
        return call("deposit_address", Map.of(
                "coin", coin,
                "network", network,
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }

    public String withdrawHistory(String coin, Integer transferType, Integer status,
            Long startTime, Long endTime, Integer offset, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", System.currentTimeMillis());
        if (coin != null && !coin.isBlank())   params.put("coin", coin);
        if (transferType != null)              params.put("transferType", transferType);
        if (status != null)                    params.put("status", status);
        if (startTime != null)                 params.put("startTime", startTime);
        if (endTime != null)                   params.put("endTime", endTime);
        if (offset != null)                    params.put("offset", offset);
        if (limit != null)                     params.put("limit", limit);
        return call("withdraw_history", params);
    }

    public String withdraw(String coin, String address, String network, Double amount,
            String withdrawConsent, String withdrawOrderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("coin", coin);
        params.put("address", address);
        params.put("network", network);
        params.put("amount", amount);
        params.put("withdrawConsent", withdrawConsent);
        params.put("timestamp", System.currentTimeMillis());
        if (withdrawOrderId != null && !withdrawOrderId.isBlank()) {
            params.put("withdrawOrderId", withdrawOrderId);
        }
        return call("withdraw", params);
    }

    public String createAuthToken() {
        return call("create_auth_token", Map.of(
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }

    public String subAccountDetails() {
        return call("sub_account_details", Map.of(
                "timestamp", System.currentTimeMillis()));
    }

    public String subAccountTransferHistory(String fromEmail, String toEmail, String currency,
            Long fromTransactionId, Long startTime, Long endTime, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("recvWindow", RECV_WINDOW_DEFAULT);
        params.put("timestamp", System.currentTimeMillis());
        if (fromEmail != null && !fromEmail.isBlank())   params.put("fromEmail", fromEmail);
        if (toEmail != null && !toEmail.isBlank())       params.put("toEmail", toEmail);
        if (currency != null && !currency.isBlank())     params.put("currency", currency);
        if (fromTransactionId != null)                   params.put("fromTransactionId", fromTransactionId);
        if (startTime != null)                           params.put("startTime", startTime);
        if (endTime != null)                             params.put("endTime", endTime);
        if (limit != null)                               params.put("limit", limit);
        return call("sub_account_transfer_history", params);
    }

    public String subAccountFundTransfer(String fromEmail, String toEmail, String currency, Double amount) {
        return call("sub_account_fund_transfer", Map.of(
                "fromEmail", fromEmail,
                "toEmail", toEmail,
                "currency", currency,
                "amount", amount,
                "recvWindow", RECV_WINDOW_DEFAULT,
                "timestamp", System.currentTimeMillis()));
    }
}
