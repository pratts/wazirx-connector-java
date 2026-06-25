package wazirx.connector.java;

import org.junit.Assume;
import org.junit.Test;

import wazirx.connector.java.exception.WazirxClientException;

import static org.junit.Assert.*;

public class ClientTest {

    private static final String API_KEY    = System.getenv("WAZIRX_API_KEY");
    private static final String API_SECRET = System.getenv("WAZIRX_API_SECRET");

    // -------------------------------------------------------------------------
    // Public API tests — no credentials required
    // -------------------------------------------------------------------------

    @Test
    public void testPing() {
        Client client = new Client("", "");
        String response = client.ping();
        assertNotNull(response);
        assertEquals("{}", response);
    }

    @Test
    public void testTime() {
        Client client = new Client("", "");
        String response = client.time();
        assertNotNull(response);
        assertTrue(response.contains("serverTime"));
    }

    @Test
    public void testSystemStatus() {
        Client client = new Client("", "");
        String response = client.systemStatus();
        assertNotNull(response);
        assertTrue(response.contains("status"));
    }

    @Test
    public void testExchangeInfo() {
        Client client = new Client("", "");
        String response = client.exchangeInfo();
        assertNotNull(response);
        assertTrue(response.contains("symbols"));
    }

    @Test
    public void testTickers() {
        Client client = new Client("", "");
        String response = client.tickers();
        assertNotNull(response);
        assertTrue(response.startsWith("["));
    }

    @Test
    public void testTicker() {
        Client client = new Client("", "");
        String response = client.ticker("btcinr");
        assertNotNull(response);
        assertTrue(response.contains("symbol"));
    }

    @Test
    public void testDepth() {
        Client client = new Client("", "");
        String response = client.depth("btcinr", 10);
        assertNotNull(response);
        assertTrue(response.contains("asks") || response.contains("bids"));
    }

    @Test
    public void testTrades() {
        Client client = new Client("", "");
        String response = client.trades("btcinr", 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testKlines() {
        Client client = new Client("", "");
        String response = client.klines("btcinr", "1m", 5, null, null);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Parameter validation tests — no network call needed
    // -------------------------------------------------------------------------

    @Test(expected = WazirxClientException.class)
    public void testCreateOrderInvalidSide() {
        new Client("key", "secret").createOrder("btcinr", "up", "limit", 1.0, 100.0, null);
    }

    @Test(expected = WazirxClientException.class)
    public void testCreateOrderNullSide() {
        new Client("key", "secret").createOrder("btcinr", null, "limit", 1.0, 100.0, null);
    }

    @Test(expected = WazirxClientException.class)
    public void testCreateOrderInvalidType() {
        new Client("key", "secret").createOrder("btcinr", "buy", "market", 1.0, 100.0, null);
    }

    @Test(expected = WazirxClientException.class)
    public void testCreateOrderLimitMissingPrice() {
        new Client("key", "secret").createOrder("btcinr", "buy", "limit", 1.0, null, null);
    }

    @Test(expected = WazirxClientException.class)
    public void testCreateOrderLimitMissingQuantity() {
        new Client("key", "secret").createOrder("btcinr", "buy", "limit", null, 100.0, null);
    }

    @Test(expected = WazirxClientException.class)
    public void testCreateOrderStopLimitMissingStopPrice() {
        new Client("key", "secret").createOrder("btcinr", "buy", "stop_limit", 1.0, 100.0, null);
    }

    // -------------------------------------------------------------------------
    // Signed API tests — skipped unless WAZIRX_API_KEY / WAZIRX_API_SECRET
    // environment variables are set
    // -------------------------------------------------------------------------

    @Test
    public void testHistoricalTrades() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).historicalTrades("btcinr", 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testAccountInfo() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).accountInfo();
        assertNotNull(response);
        assertTrue(response.contains("balances") || response.contains("assets"));
    }

    @Test
    public void testFundsInfo() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).fundsInfo();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testCoinInfo() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).coinInfo();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testOpenOrders() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).openOrders(null, null);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testAllOrders() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).allOrders("btcinr", null, null, null, 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testMyTrades() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).myTrades("btcinr", null, null, null, null, 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testSubAccountDetails() {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        String response = new Client(API_KEY, API_SECRET).subAccountDetails();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
