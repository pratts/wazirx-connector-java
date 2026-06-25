package wazirx.connector.java;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import wazirx.connector.java.exception.WazirxClientException;

import static org.junit.Assert.*;

public class ClientTest {

    private static final String API_KEY    = System.getenv("WAZIRX_API_KEY");
    private static final String API_SECRET = System.getenv("WAZIRX_API_SECRET");

    // Shared across all public tests — one connection pool for the suite
    private static Client publicClient;

    @BeforeClass
    public static void setUpClass() {
        publicClient = new Client("", "");
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        if (publicClient != null) publicClient.close();
    }

    // -------------------------------------------------------------------------
    // Public API tests — no credentials required
    // -------------------------------------------------------------------------

    @Test
    public void testPing() {
        String response = publicClient.ping();
        assertNotNull(response);
        assertEquals("{}", response);
    }

    @Test
    public void testTime() {
        String response = publicClient.time();
        assertNotNull(response);
        assertTrue(response.contains("serverTime"));
    }

    @Test
    public void testSystemStatus() {
        String response = publicClient.systemStatus();
        assertNotNull(response);
        assertTrue(response.contains("status"));
    }

    @Test
    public void testExchangeInfo() {
        String response = publicClient.exchangeInfo();
        assertNotNull(response);
        assertTrue(response.contains("symbols"));
    }

    @Test
    public void testTickers() {
        String response = publicClient.tickers();
        assertNotNull(response);
        assertTrue(response.startsWith("["));
    }

    @Test
    public void testTicker() {
        String response = publicClient.ticker("btcinr");
        assertNotNull(response);
        assertTrue(response.contains("symbol"));
    }

    @Test
    public void testDepth() {
        String response = publicClient.depth("btcinr", 10);
        assertNotNull(response);
        assertTrue(response.contains("asks") || response.contains("bids"));
    }

    @Test
    public void testTrades() {
        String response = publicClient.trades("btcinr", 10);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testKlines() {
        String response = publicClient.klines("btcinr", "1m", 5, null, null);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Parameter validation tests — exception thrown before any network call,
    // so no connection pool is used and close() would be a no-op
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
    // environment variables are set. Each test owns its Client lifecycle.
    // -------------------------------------------------------------------------

    @Test
    public void testHistoricalTrades() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.historicalTrades("btcinr", 10);
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testAccountInfo() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.accountInfo();
            assertNotNull(response);
            assertTrue(response.contains("balances") || response.contains("assets"));
        }
    }

    @Test
    public void testFundsInfo() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.fundsInfo();
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testCoinInfo() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.coinInfo();
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testOpenOrders() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.openOrders(null, null);
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testAllOrders() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.allOrders("btcinr", null, null, null, 10);
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testMyTrades() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.myTrades("btcinr", null, null, null, null, 10);
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }

    @Test
    public void testSubAccountDetails() throws IOException {
        Assume.assumeNotNull(API_KEY, API_SECRET);
        try (Client client = new Client(API_KEY, API_SECRET)) {
            String response = client.subAccountDetails();
            assertNotNull(response);
            assertFalse(response.isEmpty());
        }
    }
}
