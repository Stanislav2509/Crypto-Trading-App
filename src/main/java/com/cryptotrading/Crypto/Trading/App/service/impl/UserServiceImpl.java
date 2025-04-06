package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.repo.AssetRepository;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.repo.TransactionRepository;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    private final CryptoTypeRepository cryptoTypeRepository;
    private final AssetRepository assetRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TransactionRepository transactionRepository, CryptoTypeRepository cryptoTypeRepository, AssetRepository assetRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository = transactionRepository;
        this.cryptoTypeRepository = cryptoTypeRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public boolean register(UserRegisterBindingModel userRegisterBindingModel) {
        if(!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())){
            return false;
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(userRegisterBindingModel.getPhoneNumber());

        if(userOptional.isPresent()){
            return false;
        }

        User user = new User();
        user.setFirstName(userRegisterBindingModel.getFirstName());
        user.setLastName(userRegisterBindingModel.getLastName());
        user.setEmail(userRegisterBindingModel.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterBindingModel.getPassword()));
        user.setPhoneNumber(userRegisterBindingModel.getPhoneNumber());
        user.setBalance(10000.00);
        userRepository.save(user);

        return true;
    }
    @Override
    public Optional<Transaction> buyCrypto(String email, String pair, double spend, double quantity) {
        User user = findByEmail(email);

        if(user == null){
           return Optional.empty();
        }

        double currentUserBalance = user.getBalance();

        if ( currentUserBalance < spend) return Optional.empty();

        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType;
        if(cryptoOpt.isEmpty()){
            return Optional.empty();
        }
        cryptoType = cryptoOpt.get();

        user.setBalance(currentUserBalance - spend);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType("Buy");
        transaction.setSpendMoney(spend);
        transaction.setReceiveCrypto(quantity);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setCryptoType(cryptoType);
        transaction.setCurrCryptoPrice(cryptoType.getPrice());

        transactionRepository.save(transaction);
        userRepository.save(user);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType,user);
        Asset asset = assetOpt.orElseGet(Asset::new);
        asset.setTotal_quantity(asset.getTotal_quantity()+quantity);
        asset.setUser(user);
        asset.setCryptoType(cryptoType);
        assetRepository.save(asset);

        return Optional.of(transaction);
    }

    @Override
    public double getBalance(String email) {
        User user = findByEmail(email);
        return  user.getBalance();
    }

    @Override
    public double getQuantityFromPair(String email, String pair) {
        double quantity = 0;
        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType = new CryptoType();
        if(cryptoOpt.isPresent()){
            cryptoType = cryptoOpt.get();
        }

        User user = findByEmail(email);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType, user);
        if(assetOpt.isPresent()){
            quantity = assetOpt.get().getTotal_quantity();
        }
        return quantity;
    }

    @Override
    public Optional<Transaction> sellCrypto(String email, String pair, double spend, double receiveMoney) {
       User user = findByEmail(email);

        if(user == null){
            return Optional.empty();
        }

        double currentUserBalance = user.getBalance();
        double currentQuantityFromPair = getQuantityFromPair(email, pair.replace("-","/"));

        if ( currentQuantityFromPair < spend) return Optional.empty();

        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType;
        if(cryptoOpt.isEmpty()){
            return Optional.empty();
        }
        cryptoType = cryptoOpt.get();

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType("Sell");
        transaction.setDateTime(LocalDateTime.now());
        transaction.setCryptoType(cryptoType);
        transaction.setSpendCrypto(spend);
        transaction.setReceiveMoney(receiveMoney);
        transaction.setCurrCryptoPrice(cryptoType.getPrice());

        transactionRepository.save(transaction);
        user.setBalance(currentUserBalance+receiveMoney);
        userRepository.save(user);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType,user);
        Asset asset = assetOpt.orElseGet(Asset::new);
        double remainingQuantity = asset.getTotal_quantity()- spend;
        if(remainingQuantity==0){
            assetRepository.delete(asset);
        }else {
            asset.setTotal_quantity(remainingQuantity);
            asset.setUser(user);
            asset.setCryptoType(cryptoType);
            assetRepository.save(asset);
        }


        return Optional.of(transaction);
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = new User();
        if(userOpt.isPresent()){
            user= userOpt.get();
        }
        return user;
    }

    @Override
    public void resetProfile(String email) {
        User user = findByEmail(email);
        List<Asset> assets= assetRepository.findAllByUser(user);
        assetRepository.deleteAll(assets);
        List<Transaction> transactions = transactionRepository.findAllByUser(user);
        transactionRepository.deleteAll(transactions);
        user.setBalance(10000);
        userRepository.save(user);
    }
}
