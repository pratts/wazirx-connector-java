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
client.ticker(symbolName);

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

// Create order
/*
*	side : buy/sell
*	type : limit/stop_limit
*	In case of limit : quantity and price are mandatory
*	In case of stop_limit : quantity, price and stopPrice are mandatory
*/
client.createOrder(symbolName, side, type, quantity, price, stopPrice);

// Create test order - Same as client.createOrder but only validating the order
client.createOrder(symbolName, type, quantity, price, stopPrice);

// Query an order - Using orderId received from client.createOrder method
client.queryOrder(orderId);

// Open orders search
/*
*	symbolName : Optional. Can be null if search doesn't involve any symbol
*	orderId : Optional. Can be null if search doesn't involve any orderId
*/
client.openOrders(symbolName, orderId)

// All orders search
/*
*	symbolName : Mandatory.
*	orderId : Optional. Can be null if search doesn't involve any orderId
*	startTime : Optional. Can be null if search doesn't involve any startTime
*	endTime : Optional. Can be null if search doesn't involve any endTime
*	limit : Optional. Can be null if search doesn't involve any limit
*/
client.allOrders(symbolName, orderId, startTime, endTime, limit)

// Cancel an order
client.cancelOrder(symbolName, orderId)

// Cancel All Open Orders on a Symbol
client.cancelOpenOrders(symbolName)

```
