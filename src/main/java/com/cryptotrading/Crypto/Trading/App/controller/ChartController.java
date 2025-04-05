package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.entity.CandleData;
import com.cryptotrading.Crypto.Trading.App.service.impl.CandleDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChartController {

    @Autowired
    private CandleDataServiceImpl candleDataService;

    @GetMapping("/chart/{pair}")
    public String getChart(@PathVariable String pair,
                           @RequestParam(defaultValue = "1") int interval,
                           Model model) {

        List<CandleData> candles = candleDataService.fetchCandles(pair, interval);

        model.addAttribute("pair", pair.replace("-", "/"));
        model.addAttribute("interval", interval);
        model.addAttribute("candles", candles);
        return "chart";
    }
}
