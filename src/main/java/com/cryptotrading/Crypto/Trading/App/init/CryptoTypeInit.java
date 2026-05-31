package com.cryptotrading.Crypto.Trading.App.init;

import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.service.RestAPIService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CryptoTypeInit implements CommandLineRunner {
    private final CryptoTypeRepository cryptoTypeRepository;
    private final RestAPIService restAPIService;

    public CryptoTypeInit(CryptoTypeRepository cryptoTypeRepository, RestAPIService restAPIService) {
        this.cryptoTypeRepository = cryptoTypeRepository;
        this.restAPIService = restAPIService;
    }

    @Override
    public void run(String... args) throws Exception {
        long countCrypto = cryptoTypeRepository.count();
        Map<String, String> cryptoPrices = restAPIService.getTop20CryptoPrices();

        if(countCrypto == 0){
            for (String pair : cryptoPrices.keySet()){
                double price = Double.parseDouble(cryptoPrices.get(pair));
                CryptoType cryptoType = new CryptoType();
                cryptoType.setSymbol(pair);
                cryptoType.setPrice(new BigDecimal(price));
                cryptoType.setName(getPairName(pair));
                cryptoTypeRepository.save(cryptoType);
            }
        }

    }
    private String getPairName(String pair){
        String name = "";
        switch (pair){
            case "BTC/USD":
                name = "Bitcoin";
                break;
            case "ETH/USD":
                name = "Ethereum";
                break;
            case "USDT/USD":
                name = "Tether";
                break;
            case "XRP/USD":
                name = "XRP";
                break;
            case "SOL/USD":
                name = "Solana";
                break;
            case "USDC/USD":
                name = "USDC";
                break;
            case "DOGE/USD":
                name = "Dogecoin";
                break;
            case "ADA/USD":
                name = "Cardano";
                break;
            case "TRX/USD":
                name = "TRON";
                break;
            case "TON/USD":
                name = "Toncoin";
                break;
            case "LINK/USD":
                name = "Chainlink";
                break;
            case "XLM/USD":
                name = "Stellar";
                break;
            case "AVAX/USD":
                name = "Avalanche";
                break;
            case "SHIB/USD":
                name = "Shiba Inu";
                break;
            case "SUI/USD":
                name = "Sui";
                break;
            case "LTC/USD":
                name = "Litecoin";
                break;
            case "DOT/USD":
                name = "Polkadot";
                break;
            case "OM/USD":
                name = "Mantra";
                break;
            case "BCH/USD":
                name = "Bitcoin Cash";
                break;
            case "DAI/USD":
                name = "Dai";
                break;
        }

        return name;
    }
}
