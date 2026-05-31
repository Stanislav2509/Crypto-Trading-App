package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.CryptoTypeService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import com.cryptotrading.Crypto.Trading.App.service.WebSocketService;
import com.cryptotrading.Crypto.Trading.App.service.impl.CryptoTypeServiceImpl;
import com.cryptotrading.Crypto.Trading.App.service.impl.WebSocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketRestAPIController {

    private final WebSocketService webSocketService;
    private final CryptoTypeService cryptoTypeService;
    private final UserService userService;

    @Autowired
    public WebSocketRestAPIController(WebSocketServiceImpl webSocketService, CryptoTypeServiceImpl cryptoTypeService, UserService userService) {
        this.webSocketService = webSocketService;
        this.cryptoTypeService = cryptoTypeService;
        this.userService = userService;
    }

    @GetMapping("/real-time-prices")
    public String getPricesTable(Model model, Principal principal) {
        Map<String, String> prices = cryptoTypeService.getPairsPrices();
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("prices", prices);
        model.addAttribute("user", user);

        webSocketService.viewRealTimePrices(principal.getName());

        return "real-time-prices";
    }
}