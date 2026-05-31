package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.entity.CandleData;
import com.cryptotrading.Crypto.Trading.App.service.CandleDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
public class CandleDataServiceImpl implements CandleDataService {

    @Override
    public List<CandleData> fetchCandles(String pair, int interval) {
        List<CandleData> result = new ArrayList<>();
        String newPair = pair.replace("-", "/");
        String url = String.format("https://api.kraken.com/0/public/OHLC?pair=%s&interval=%d",
               newPair , interval);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode resultNode = root.get("result");


            String key = resultNode.fieldNames().next();
            JsonNode data = resultNode.get(key);

            for (JsonNode node : data) {
                CandleData candle = new CandleData();
                candle.setTime(BigDecimal.valueOf(node.get(0).asDouble()));
                candle.setOpen(BigDecimal.valueOf(node.get(1).asDouble()));
                candle.setHigh(BigDecimal.valueOf(node.get(2).asDouble()));
                candle.setLow(BigDecimal.valueOf(node.get(3).asDouble()));
                candle.setClose(BigDecimal.valueOf(node.get(4).asDouble()));
                result.add(candle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
