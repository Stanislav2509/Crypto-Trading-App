package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.service.CryptoTypeService;
import com.cryptotrading.Crypto.Trading.App.service.WebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PriceController {

    private final CryptoTypeService cryptoTypeService;
    private final WebSocketService webSocketService;

    public PriceController(CryptoTypeService cryptoTypeService, WebSocketService webSocketService) {
        this.cryptoTypeService = cryptoTypeService;
        this.webSocketService = webSocketService;
    }

    @GetMapping("/prices")
    public Map<String, String> getPrices(Principal principal) {
        webSocketService.viewRealTimePrices(principal.getName());
        return cryptoTypeService.getPairsPrices();
    }
}
