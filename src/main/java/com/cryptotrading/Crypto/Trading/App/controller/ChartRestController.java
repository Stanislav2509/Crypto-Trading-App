package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.ChartDataDTO;
import com.cryptotrading.Crypto.Trading.App.model.dto.TradeBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.CandleData;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.service.CandleDataService;
import com.cryptotrading.Crypto.Trading.App.service.CryptoTypeService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ChartRestController {

    private final CandleDataService candleDataService;
    private final CryptoTypeService cryptoTypeService;
    private final UserService userService;

    public ChartRestController(CandleDataService candleDataService, CryptoTypeService cryptoTypeService, UserService userService) {
        this.candleDataService = candleDataService;
        this.cryptoTypeService = cryptoTypeService;
        this.userService = userService;
    }

    @GetMapping("/chart/{pair}")
    public ChartDataDTO getChartData(@PathVariable String pair,
                                      @RequestParam(defaultValue = "1") int interval,
                                      Principal principal) {
        List<CandleData> candles = candleDataService.fetchCandles(pair, interval);
        BigDecimal userBalance = userService.getBalance(principal.getName());
        CryptoType cryptoType = cryptoTypeService.findBySymbol(pair);
        BigDecimal quantityCrypto = userService.getQuantityFromPair(principal.getName(), pair);

        return new ChartDataDTO(
                pair,
                cryptoType.getName(),
                candles,
                cryptoType.getPrice(),
                userBalance,
                quantityCrypto
        );
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyCrypto(@RequestBody TradeBindingModel tradeBindingModel, Principal principal) {
        Optional<Transaction> transaction = userService.buyCrypto(
                principal.getName(),
                tradeBindingModel.getPair(),
                tradeBindingModel.getSpend()
        );

        return transaction
                .<ResponseEntity<Map<String, Object>>>map(t -> ResponseEntity.ok(Map.of("transactionId", t.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "Trade could not be completed")));
    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String, Object>> sellCrypto(@RequestBody TradeBindingModel tradeBindingModel, Principal principal) {
        Optional<Transaction> transaction = userService.sellCrypto(
                principal.getName(),
                tradeBindingModel.getPair(),
                tradeBindingModel.getSpend()
        );

        return transaction
                .<ResponseEntity<Map<String, Object>>>map(t -> ResponseEntity.ok(Map.of("transactionId", t.getId())))
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "Trade could not be completed")));
    }
}
