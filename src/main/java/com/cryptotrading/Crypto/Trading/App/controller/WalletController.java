package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.AssetSummaryDTO;
import com.cryptotrading.Crypto.Trading.App.service.AssetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WalletController {

    private final AssetService assetService;

    public WalletController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/wallet")
    public List<AssetSummaryDTO> getWalletAssets(Principal principal) {
        return assetService.findAllByUserEmail(principal.getName())
                .stream()
                .map(asset -> new AssetSummaryDTO(
                        asset.getCryptoType().getSymbol(),
                        asset.getTotalQuantity(),
                        asset.getMoneyCurrency(),
                        asset.getProfitLoss()
                ))
                .toList();
    }
}
