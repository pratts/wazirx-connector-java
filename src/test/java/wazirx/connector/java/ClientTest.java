package wazirx.connector.java;

import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTest {

    private static final String API_KEY = System.getenv("WAZIRX_API_KEY");
    private static final String API_SECRET = System.getenv("WAZIRX_API_SECRET");

    // -------------------------------------------------------------------------
    // Public API tests — no credentials required
    // -------------------------------------------------------------------------

    @Test
    public void testPing() throws Exception {
        Client client = new Client("", "");
        String response = client.ping();
        assertNotNull(response);
        assertEquals("{}", response);
    }

    @Test
    public void testTime() throws Exception {
        Client client = new Client("", "");
        String response = client.time();
        assertNotNull(response);
        assertTrue(response.contains("serverTime"));
    }

    @Test
    public void testSystemStatus() throws Exception {
        Client client = new Client("", "");
        String response = client.systemStatus();
        assertNotNull(response);
        assertTrue(response.contains("status"));
    }

    @Test
    public void testExchangeInfo() throws Exception {
        Client client = new Client("", "");
        String response = client.exchangeInfo();
        assertNotNull(response);
        assertTrue(response.contains("symbols"));
    }

    @Test
    public void testTickers() throws Exception {
        Client client = new Client("", "");
        String response = client.tickers();
        assertNotNull(response);
        assertTrue(response.startsWith("["));
    }

    @Test
    public void testTicker() throws Exception {
        Client client = new Client("", "");
        String response = client.ticker("btcinr");
        assertNotNull(response);
        assertTrue(response.contains("symbol"));
    }

    @Test
    public void testDepth() throws Exception {
        Client client = new Client("", "");
        String response = client.depth("btcinr", 10);
        assertNotNull(response);
        assertTrue(response.contains("asks") || response.contains("bids"));
    }

    @Test
    public void testTrades() throws Exception {
        Client client = new Client("", "");
        String response = client.trades("btcinr", 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testKlines() throws Exception {
        Client client = new Client("", "");
        String response = client.klines("btcinr", "1m", 5, null, null);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Parameter validation tests — no network call needed
    // -------------------------------------------------------------------------

    @Test(expected = Exception.class)
    public void testCreateOrderInvalidType() throws Exception {
        Client client = new Client("key", "secret");
        client.createOrder("btcinr", "buy", "market", 1.0, 100.0, null);
    }

    @Test(expected = Exception.class)
    public void testCreateOrderLimitMissingPrice() throws Exception {
        Client client = new Client("key", "secret");
        client.createOrder("btcinr", "buy", "limit", 1.0, null, null);
    }

    @Test(expected = Exception.class)
    public void testCreateOrderLimitMissingQuantity() throws Exception {
        Client client = new Client("key", "secret");
        client.createOrder("btcinr", "buy", "limit", null, 100.0, null);
    }

    @Test(expected = Exception.class)
    public void testCreateOrderStopLimitMissingStopPrice() throws Exception {
        Client client = new Client("key", "secret");
        client.createOrder("btcinr", "buy", "stop_limit", 1.0, 100.0, null);
    }

    // -------------------------------------------------------------------------
    // Signed API tests — skipped unless WAZIRX_API_KEY / WAZIRX_API_SECRET
    // environment variables are set
    // -------------------------------------------------------------------------

    @Test
    public void testHistoricalTrades() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.historicalTrades("btcinr", 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testAccountInfo() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.accountInfo();
        assertNotNull(response);
        assertTrue(response.contains("balances") || response.contains("assets"));
    }

    @Test
    public void testFundsInfo() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.fundsInfo();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testCoinInfo() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.coinInfo();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testOpenOrders() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.openOrders(null, null);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testAllOrders() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.allOrders("btcinr", null, null, null, 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testMyTrades() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.myTrades("btcinr", null, null, null, null, 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testSubAccountDetails() throws Exception {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        Client client = new Client(API_KEY, API_SECRET);
        String response = client.subAccountDetails();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}
