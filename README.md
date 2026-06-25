# WazirX Unofficial Java Connector

An unofficial Java wrapper for the [WazirX](https://wazirx.com) exchange REST API and WebSocket streams.

> **Disclaimer:** This is an unofficial library and is not affiliated with, endorsed by, or supported by WazirX. Use at your own risk.

---

## Features

- Full coverage of WazirX REST API (public + signed endpoints)
- WebSocket support for real-time market data and account updates
- HMAC-SHA256 request signing handled automatically
- All methods return raw JSON strings for flexibility

---

## Requirements

- Java 11+
- Gradle 7+ (or use the included wrapper)

---

## Installation

[Download the latest release jar](https://github.com/pratts/wazirx-connector-java/releases) and add it to your project classpath, or build from source:

```bash
git clone https://github.com/pratts/wazirx-connector-java.git
cd wazirx-connector-java
gradle build
```

---

## Setup

Generate your API key and secret from the [WazirX API settings page](https://wazirx.com/settings/keys).

`Client` implements `Closeable` and holds an HTTP connection pool. Use try-with-resources to ensure it is shut down cleanly:

```java
import wazirx.connector.java.Client;

try (Client client = new Client("your_api_key", "your_api_secret")) {
    String response = client.ping();
}
```

For public endpoints (no auth required), empty strings are fine:

```java
try (Client client = new Client("", "")) {
    String tickers = client.tickers();
}
```

---

## REST API

All methods return a JSON string. Signed endpoints automatically include the `timestamp` and `signature` — you do not need to add them manually.

### General

```java
// Test connectivity
client.ping();
// → {}

// Server time
client.time();
// → {"serverTime": 1632375600000}

// System status
client.systemStatus();
// → {"status": "normal", "message": "System is running"}

// Exchange info (all trading pairs and their rules)
client.exchangeInfo();
```

### Market Data (public)

```java
// 24hr price change statistics for all symbols
client.tickers();

// 24hr price change statistics for a single symbol
client.ticker("btcinr");

// Order book depth
// limit: valid values are 1, 5, 10, 20, 50, 100, 500, 1000 (default 20)
client.depth("btcinr", 20);

// Recent trades
// limit: default 500, max 1000
client.trades("btcinr", 100);

// Kline / candlestick data
// interval: 1m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 12h, 1d, 1w
// startTime / endTime: Unix timestamp in seconds (optional, pass null to omit)
client.klines("btcinr", "1h", 100, null, null);
client.klines("btcinr", "1d", 30, 1647822960L, 1647823020L);
```

### Market Data (signed)

```java
// Historical trades (requires API key)
// limit: default 500, max 1000
client.historicalTrades("btcinr", 100);
```

### Orders

```java
// Create a limit order
// side: "buy" or "sell"
// type: "limit" or "stop_limit"
// quantity and price are required for limit orders
client.createOrder("btcinr", "buy", "limit", 0.001, 2500000.0, null);

// Create a stop-limit order
// quantity, price, and stopPrice are all required
client.createOrder("btcinr", "sell", "stop_limit", 0.001, 2400000.0, 2450000.0);

// Test order — validates the request without placing it
client.createTestOrder("btcinr", "buy", "limit", 0.001, 2500000.0, null);

// Query a specific order
client.queryOrder(orderId);

// Open orders (both parameters are optional, pass null to omit)
client.openOrders("btcinr", null);
client.openOrders(null, null);

// All orders for a symbol
// orderId, startTime, endTime, limit are optional — pass null to omit
// limit: range 1–1000, default 500
client.allOrders("btcinr", null, 1590148051000L, null, 100);

// Cancel an order
client.cancelOrder("btcinr", orderId);

// Cancel all open orders on a symbol
client.cancelOpenOrders("btcinr");

// My trades
// All parameters are optional — pass null to omit
// symbol, orderId, fromId, startTime, endTime, limit
client.myTrades("btcinr", null, null, null, null, 50);
client.myTrades(null, orderId, null, null, null, null);
```

### Account

```java
// Account information
client.accountInfo();

// Fund balances
client.fundsInfo();
```

### Crypto (Deposits & Withdrawals)

```java
// Coin information
client.coinInfo();

// Deposit address for a coin and network
client.depositAddress("btc", "btc");
client.depositAddress("eth", "eth");

// Withdraw history
// All parameters are optional — pass null to omit
// coin, transferType, status, startTime, endTime, offset, limit
client.withdrawHistory("btc", null, null, null, null, null, 10);

// Submit a withdrawal
// withdrawOrderId is optional — pass null to omit
client.withdraw("eth", "0xYourAddress", "eth", 0.02,
    "I hereby confirm that I am withdrawing these crypto assets.", null);
```

### Sub-Accounts

```java
// List sub-accounts
client.subAccountDetails();

// Sub-account transfer history
// All parameters are optional — pass null to omit
// fromEmail, toEmail, currency, fromTransactionId, startTime, endTime, limit
client.subAccountTransferHistory(null, null, null, null, null, null, 20);

// Initiate a fund transfer between sub-accounts
client.subAccountFundTransfer("from@example.com", "to@example.com", "btc", 0.01);
```

### WebSocket Auth Token

```java
// Generate an auth token (used internally by SocketClient for private streams)
client.createAuthToken();
```

---

## WebSocket

The `SocketClient` establishes a persistent WebSocket connection to `wss://stream.wazirx.com/stream` and keeps it alive with automatic pings every 5 minutes.

```java
import wazirx.connector.java.SocketClient;
import wazirx.connector.java.handlers.IMessageHandler;

IMessageHandler handler = message -> System.out.println("Received: " + message);

SocketClient socket = new SocketClient("your_api_key", "your_api_secret", handler);
socket.connect();
```

### Public streams

```java
// Live trades for a symbol
socket.subToSymbolTrade("btcinr");

// Full market ticker feed (all symbols)
socket.subToMarket();

// Order book depth updates for a symbol
socket.subToMarketDepth("btcinr");
```

### Private streams (require valid API credentials)

```java
// Account balance updates
socket.subToAccountUpdate();

// Order status updates
socket.subToOrderUpdate();

// Your own trade events
socket.subToOwnTrade();
```

### Unsubscribe

```java
socket.unsubscribe(new String[]{"btcinr@trades"}, false);
```

---

## Error Handling

All methods throw unchecked exceptions — no forced `try/catch` at every call site.

| Exception | When thrown |
|---|---|
| `WazirxClientException` | Invalid parameters (bad `side`, missing required field, unknown endpoint) |
| `WazirxApiException` | Network failure or a non-2xx HTTP response from the API |

`WazirxClientException` extends `WazirxApiException`, so you can catch either depending on how granular you need to be:

```java
import wazirx.connector.java.exception.WazirxApiException;
import wazirx.connector.java.exception.WazirxClientException;

try (Client client = new Client(apiKey, apiSecret)) {
    String result = client.createOrder("btcinr", "buy", "limit", 0.001, 2500000.0, null);
} catch (WazirxClientException e) {
    // bad parameters — fix the call
    System.err.println("Invalid request: " + e.getMessage());
} catch (WazirxApiException e) {
    // network error or API rejected the request (response body included in message)
    System.err.println("API error: " + e.getMessage());
}
```

---

## Logging

The library uses [SLF4J](https://www.slf4j.org/) for logging and ships with no binding. Add a binding to your project to see log output, or add `slf4j-nop` to silence the startup warning:

```groovy
// Logback (recommended)
implementation 'ch.qos.logback:logback-classic:1.5.18'

// Or silence all logging
implementation 'org.slf4j:slf4j-nop:2.0.16'
```

---

## Running Tests

Public API tests run without credentials. Signed API tests are automatically skipped unless credentials are provided via environment variables:

```bash
# Run all tests (public only)
gradle test

# Run all tests including signed endpoint tests
WAZIRX_API_KEY=your_key WAZIRX_API_SECRET=your_secret gradle test
```

---

## License

This project is licensed under the MIT License.
