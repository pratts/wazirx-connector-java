# Wazirx unofficial Java client library
This is an unofficial Java wrapper for the Wazirx exchange REST API + Websocket collection.

Wazirx Java connector is a set of helper methods to connecto with Wazirx.com platform via APIs and Websocket. 

## Usage:
Generate API KEY from Wazirx website using the https://wazirx.com/settings/keys

[Download the Wazirx Connector jar file](https://github.com/pratts/wazirx-connector-java/releases/tag/v1.0.0) and include it in your project.

### API usage (All methods return JSON string as return type)
```
// Importing the rest client class
import wazirx.connector.java.Client

// Initialize the client object
Client client = new Client(apiKey, apiSecret);

// Test connectivity by sending ping
client.ping();

// Get system status
client.systemStatus();

// Get server time
client.time();

// Get exchange info
client.exchangeInfo();

// 24hr tickers price change statistics
client.tickers();

// 24hr ticker price change statistics for a symbol : here symbol name(example "btcinr" or one of the symbols from exchange info method)
client.ticket(symbolName);

// Order book : limit value Valid limits:[1, 5, 10, 20, 50, 100, 500, 1000]
client.depth(symbolName, limit)

// Recent trades list : limit value Default 500; max 1000.
client.trades(symbolName, limit)

// Old trade lookup (Market Data)
client.historicalTrades(symbolName, limit)

// Account Information
client.accountInfo()

// Get funds info
client.fundsInfo()

// Create authentication token for websocket connection
client.createAuthToken()

```

Note : Currently working on trading APIs
