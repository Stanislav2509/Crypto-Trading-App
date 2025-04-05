package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.service.RestAPIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RestAPIServiceImpl implements RestAPIService {

    private static final String REST_KRAKEN_API_URL = "https://api.kraken.com/0/public/Ticker?pair=";

    private final RestTemplate restTemplate ;

    @Autowired
    public RestAPIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String[] pairs = {
                "BTC/USD", "ETH/USD", "USDT/USD", "XRP/USD","SOL/USD",
                "USDC/USD", "DOGE/USD", "ADA/USD", "TRX/USD","TON/USD",
                "LINK/USD", "XLM/USD", "AVAX/USD","SHIB/USD","SUI/USD",
                 "LTC/USD", "DOT/USD", "OM/USD", "BCH/USD" , "DAI/USD"
    };

    @Override
    public Map<String, String> getTop20CryptoPrices() {
        Map<String, String> prices = new HashMap<>();
        try {
            String joinedPairs = String.join(",", pairs);
            String url = REST_KRAKEN_API_URL + joinedPairs;

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode result = root.path("result");

            for (String pair : pairs) {
                JsonNode pairNode = result.path(pair);
                if (!pairNode.isMissingNode()) {
                    String price = pairNode.path("c").get(0).asText();
                    prices.put(pair, price);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return prices;
    }

}
