package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.service.CryptoTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class CryptoTypeServiceImpl implements CryptoTypeService {
    private final CryptoTypeRepository cryptoTypeRepository;

    public CryptoTypeServiceImpl(CryptoTypeRepository cryptoTypeRepository) {
        this.cryptoTypeRepository = cryptoTypeRepository;
    }

    @Override
    public Map<String, String> getPairsPrices() {
        List<CryptoType> allPairs = cryptoTypeRepository.findAll();
        Map<String, String> pairsPrices = new TreeMap<>();
        for (CryptoType crypto : allPairs){
            pairsPrices.put(crypto.getSymbol(), crypto.getPrice().toString());
        }

        return pairsPrices;
    }

    @Override
    public CryptoType findBySymbol(String pair) {
        Optional<CryptoType> cryptoType = cryptoTypeRepository.findBySymbol(pair.replace("-","/"));
        if(cryptoType.isPresent()){
            return cryptoType.get();
        }
        return null;
    }
}
