package com.cryptotrading.Crypto.Trading.App.client;

import com.cryptotrading.Crypto.Trading.App.model.dto.AssetDTO;
import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.repo.AssetRepository;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.WebSocket;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketClient implements WebSocket.Listener {
    private final SimpMessagingTemplate messagingTemplate;
    private final CryptoTypeRepository cryptoTypeRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final Map<String, String> currentPrices = new ConcurrentHashMap<>();
    private final Map<String, AssetDTO> assetPrice = new ConcurrentHashMap<>();
    private Long currentUserId;

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
    }
    public WebSocketClient(SimpMessagingTemplate messagingTemplate, CryptoTypeRepository cryptoTypeRepository, AssetRepository assetRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.cryptoTypeRepository = cryptoTypeRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
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

                Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair);
                CryptoType cryptoType = cryptoOpt.orElseGet(CryptoType::new);
                cryptoType.setSymbol(pair);
                cryptoType.setPrice(Double.parseDouble(price));
                cryptoTypeRepository.save(cryptoType);

                List<Asset> assets = assetRepository.findAllByCryptoType(cryptoType);
                for(Asset asset : assets){
                    double x = asset.getTotalQuantity() * Double.parseDouble(price);

                    asset.setMoneyCurrency(x);
                    assetRepository.save(asset);
                }
                Optional<User> byId = userRepository.findById(currentUserId);
                User user = new User();
                if(byId.isPresent()){
                    user= byId.get();
                }

                Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType, user);
                if(assetOpt.isPresent()){
                    Asset asset  = assetOpt.get();

                    double profitLoss = (cryptoType.getPrice() - asset.getPriceDuringPurchase()) * asset.getTotalQuantity();
                    asset.setProfitLoss(profitLoss);
                    asset.setPriceNow(cryptoType.getPrice());
                    assetRepository.save(asset);
                    String key = asset.getCryptoType().getSymbol();
                    AssetDTO assetDTO = new AssetDTO();
                    assetDTO.setMoneyCurrency(asset.getMoneyCurrency());
                    assetDTO.setProfitLoss(profitLoss);
                    assetPrice.put(key, assetDTO);
                }

                messagingTemplate.convertAndSend("/topic/prices", currentPrices);
                messagingTemplate.convertAndSend("/topic/asset", assetPrice);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return WebSocket.Listener.super.onText(webSocket, data, false);
    }

}