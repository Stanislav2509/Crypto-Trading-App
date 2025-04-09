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
import org.springframework.web.servlet.ModelAndView;
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
        long transactionID = transaction.isPresent() ? transaction.get().getId(): 0;

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

        long transactionID = transaction.isPresent() ? transaction.get().getId(): 0;

        return "redirect:/sell-details/" + transactionID;
    }

    @GetMapping("/buy-details/{id}")
    public ModelAndView buyDetails(@PathVariable Long id, Model model, Principal principal){
        String email = principal.getName();
        return getDealDetails(id, email , "buy-details");
    }

    @GetMapping("/sell-details/{id}")
    public ModelAndView sellDetails(@PathVariable Long id, Model model, Principal principal){
        String email = principal.getName();
        return getDealDetails(id, email , "sell-details");
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

    private ModelAndView getDealDetails(Long id, String email, String template){
        ModelAndView model = new ModelAndView(template);
        Transaction transaction = transactionService.findById(id);
        String pair = transaction.getCryptoType().getSymbol();
        List<Asset>  assets = assetService.findAllByUserEmail(email);
        User user = userService.findByEmail(email);

        model.addObject("transaction", transaction);
        model.addObject("pair", pair);
        model.addObject("user", user);
        model.addObject("assets", assets);

        return model;
    }
}
