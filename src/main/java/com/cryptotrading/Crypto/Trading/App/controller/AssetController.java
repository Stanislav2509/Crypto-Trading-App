package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.service.AssetService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class AssetController {
    private final AssetService assetService;
    private final UserService userService;

    public AssetController(AssetService assetService, UserService userService) {
        this.assetService = assetService;
        this.userService = userService;
    }

    @GetMapping("/wallet")
    public String getWallet(Principal principal, Model model){
        String email = principal.getName();
        double balanceUSD = userService.getBalance(email);
        List<Asset> assets = assetService.findAllByUserEmail(email);
        model.addAttribute("balanceUSD", balanceUSD);
        model.addAttribute("assets", assets);
        return "wallet";
    }
}
