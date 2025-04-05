package com.cryptotrading.Crypto.Trading.App.client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketClient implements WebSocket.Listener {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, String> currentPrices = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        String message = data.toString();
        if (message.contains("\"event\"")) return WebSocket.Listener.super.onText(webSocket, data, false);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            if (jsonNode.isArray() && jsonNode.size() > 2 && "trade".equals(jsonNode.get(2).asText())) {
                JsonNode tradesArray = jsonNode.get(1);
                String pair = jsonNode.get(3).asText();
                if ("XBT/USD".equals(pair)) {
                    pair = "BTC/USD";
                }
                String price = tradesArray.get(0).get(0).asText();

                currentPrices.put(pair, price);

                messagingTemplate.convertAndSend("/topic/prices", currentPrices);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return WebSocket.Listener.super.onText(webSocket, data, false);
    }

}