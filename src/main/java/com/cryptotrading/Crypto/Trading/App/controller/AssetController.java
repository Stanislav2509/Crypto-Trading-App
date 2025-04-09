package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.TradeBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.AssetService;
import com.cryptotrading.Crypto.Trading.App.service.TransactionService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class AssetController {
    private final AssetService assetService;
    private final UserService userService;
    private final TransactionService transactionService;

    public AssetController(AssetService assetService, UserService userService, TransactionService transactionService) {
        this.assetService = assetService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @PostMapping("/buy")
    public String buyCrypto(@ModelAttribute("tradeBindingModel")@Valid TradeBindingModel tradeBindingModel,
                            BindingResult bindingResult,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "buy";
        }

        String email = principal.getName();
        String pair = tradeBindingModel.getPair();
        BigDecimal spend = tradeBindingModel.getSpend();
        BigDecimal quantity = tradeBindingModel.getReceive();

        Optional<Transaction> transaction = userService.buyCrypto(email, pair, spend, quantity);
        long transactionID = 0;
        if (transaction.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Покупката е успешна!");
            transactionID = transaction.get().getId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Проблем с покупката");
        }

        return "redirect:/buy-details/" + transactionID;
    }
    @PostMapping("/sell")
    public String sellCrypto(@ModelAttribute("tradeBindingModel")@Valid TradeBindingModel tradeBindingModel,
                             BindingResult bindingResult,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "sell";
        }

        String email = principal.getName();
        String pair = tradeBindingModel.getPair();
        BigDecimal spend = tradeBindingModel.getSpend();
        BigDecimal receiveMoney = tradeBindingModel.getReceive();

        Optional<Transaction> transaction = userService.sellCrypto(email, pair, spend, receiveMoney);

        long transactionID = 0;
        if (transaction.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Продажбата е успешна!");
            transactionID = transaction.get().getId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Проблем с продажбата");
        }

        return "redirect:/sell-details/" + transactionID;
    }

    @GetMapping("/buy-details/{id}")
    public String buyDetails(@PathVariable Long id, Model model, Principal principal){
        Transaction transaction = transactionService.findById(id);
        String pair = transaction.getCryptoType().getSymbol();
        List<Asset>  assets = assetService.findAllByUserEmail(principal.getName());
        User user = userService.findByEmail(principal.getName());

        model.addAttribute("transaction", transaction);
        model.addAttribute("pair", pair);
        model.addAttribute("user", user);
        model.addAttribute("assets", assets);

        return "buy-details";
    }

    @GetMapping("/sell-details/{id}")
    public String sellDetails(@PathVariable Long id, Model model, Principal principal){
        Transaction transaction = transactionService.findById(id);
        String pair = transaction.getCryptoType().getSymbol();
        User user = userService.findByEmail(principal.getName());
        List<Asset>  assets = assetService.findAllByUserEmail(principal.getName());

        model.addAttribute("transaction", transaction);
        model.addAttribute("pair", pair);
        model.addAttribute("user", user);
        model.addAttribute("assets", assets);

        return "sell-details";
    }

    @GetMapping("/wallet")
    public String getWallet(Principal principal, Model model){
        String email = principal.getName();
        List<Asset> assets = assetService.findAllByUserEmail(email);
        User user = userService.findByEmail(principal.getName());

        model.addAttribute("assets", assets);
        model.addAttribute("user", user);
        return "wallet";
    }
}
