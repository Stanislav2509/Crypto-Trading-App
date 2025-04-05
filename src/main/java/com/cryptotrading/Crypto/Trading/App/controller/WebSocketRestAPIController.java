package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.service.impl.RestAPIServiceImpl;
import com.cryptotrading.Crypto.Trading.App.service.impl.WebSocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class WebSocketRestAPIController {

    private final RestAPIServiceImpl restAPIService;
    private final WebSocketServiceImpl webSocketService;

    @Autowired
    public WebSocketRestAPIController(RestAPIServiceImpl restAPIService, WebSocketServiceImpl webSocketService) {
        this.restAPIService = restAPIService;
        this.webSocketService = webSocketService;
    }

    @GetMapping("/real-time-prices")
    public String getPricesTable(Model model) {
        Map<String, String> prices = restAPIService.getTop20CryptoPrices();
        model.addAttribute("prices", prices);

        webSocketService.viewRealTimePrices();

        return "real-time-prices";
    }
}