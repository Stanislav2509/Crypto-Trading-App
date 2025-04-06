package com.cryptotrading.Crypto.Trading.App.init;

import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.service.RestAPIService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
                cryptoType.setPrice(price);
                cryptoTypeRepository.save(cryptoType);
            }
        }
    }
}
