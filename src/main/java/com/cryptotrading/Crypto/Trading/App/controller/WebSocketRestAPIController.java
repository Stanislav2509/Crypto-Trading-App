package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.service.CryptoTypeService;
import com.cryptotrading.Crypto.Trading.App.service.WebSocketService;
import com.cryptotrading.Crypto.Trading.App.service.impl.CryptoTypeServiceImpl;
import com.cryptotrading.Crypto.Trading.App.service.impl.WebSocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class WebSocketRestAPIController {

    private final WebSocketService webSocketService;
    private final CryptoTypeService cryptoTypeService;

    @Autowired
    public WebSocketRestAPIController( WebSocketServiceImpl webSocketService, CryptoTypeServiceImpl cryptoTypeService) {
        this.webSocketService = webSocketService;
        this.cryptoTypeService = cryptoTypeService;
    }

    @GetMapping("/real-time-prices")
    public String getPricesTable(Model model) {
        Map<String, String> prices = cryptoTypeService.getPairsPrices();
        model.addAttribute("prices", prices);

        webSocketService.viewRealTimePrices();

        return "real-time-prices";
    }
}