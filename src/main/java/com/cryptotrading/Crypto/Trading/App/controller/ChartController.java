package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.TradeBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.CandleData;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.service.CandleDataService;
import com.cryptotrading.Crypto.Trading.App.service.CryptoTypeService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class ChartController {
    private final UserService userService;
    private final CryptoTypeService cryptoTypeService;
    private final CandleDataService candleDataService;

    public ChartController(UserService userService, CryptoTypeService cryptoTypeService, CandleDataService candleDataService) {
        this.userService = userService;
        this.cryptoTypeService = cryptoTypeService;
        this.candleDataService = candleDataService;
    }

    @GetMapping("/chart/{pair}")
    public String showCryptoPage(@PathVariable String pair,
                                 @RequestParam(defaultValue = "1") int interval,
                                 Model model, Principal principal,
                                 @ModelAttribute("tradeBindingModel") TradeBindingModel tradeBindingModel) {

        List<CandleData> candles = candleDataService.fetchCandles(pair, interval);
        model.addAttribute("pair", pair.replace("-", "/"));
        model.addAttribute("interval", interval);
        model.addAttribute("candles", candles);

        double userBalance = userService.getBalance(principal.getName());
        CryptoType cryptoType = cryptoTypeService.findBySymbol(pair);
        double lastPrice = cryptoType.getPrice();
        model.addAttribute("pair", pair);
        model.addAttribute("currentPrice", lastPrice);
        model.addAttribute("userBalance", userBalance);

        double quantityFromPair = userService.getQuantityFromPair(principal.getName(), pair);
        model.addAttribute("quantityCrypto", quantityFromPair);

        return "chart";
    }

}
