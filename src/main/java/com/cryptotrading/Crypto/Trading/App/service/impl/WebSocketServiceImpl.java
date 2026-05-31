package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.client.WebSocketClient;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import com.cryptotrading.Crypto.Trading.App.service.WebSocketService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;


@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final WebSocketClient webSocketClient;
    private final UserService userService;

    public WebSocketServiceImpl(WebSocketClient webSocketClient, UserService userService) {
        this.webSocketClient = webSocketClient;
        this.userService = userService;
    }

    @Override
    public void viewRealTimePrices(String email) {
        User user = userService.findByEmail(email);
        webSocketClient.setCurrentUserId(user.getId());

        String[] pairs = {
                "XBT/USD", "ETH/USD", "USDT/USD", "XRP/USD","SOL/USD",
                "USDC/USD", "DOGE/USD", "ADA/USD", "TRX/USD","TON/USD",
                "LINK/USD", "XLM/USD", "AVAX/USD","SHIB/USD","SUI/USD",
                "LTC/USD", "DOT/USD", "OM/USD", "BCH/USD" , "DAI/USD"
        };

        StringBuilder subscriptionMsg = new StringBuilder("{ \"event\":\"subscribe\", \"subscription\":{\"name\":\"trade\"},\"pair\":[");
        for (int i = 0; i < pairs.length; i++) {
            subscriptionMsg.append("\"").append(pairs[i]).append("\"");
            if (i < pairs.length - 1) subscriptionMsg.append(",");
        }
        subscriptionMsg.append("]}");

        WebSocket webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("wss://ws.kraken.com"), webSocketClient)
                .join();

        webSocket.sendText(subscriptionMsg.toString(), true);
    }
}
