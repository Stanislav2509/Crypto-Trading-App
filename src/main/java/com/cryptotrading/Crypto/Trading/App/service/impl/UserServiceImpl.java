package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.model.enums.TransactionType;
import com.cryptotrading.Crypto.Trading.App.repo.AssetRepository;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.repo.TransactionRepository;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        user.setBalance(new BigDecimal( "10000.00"));
        userRepository.save(user);

        return true;
    }
    @Override
    public Optional<Transaction> buyCrypto(String email, String pair, BigDecimal spend, BigDecimal quantity) {
        User user = findByEmail(email);

        if(user == null){
           return Optional.empty();
        }

        BigDecimal currentUserBalance = user.getBalance();

        if ( currentUserBalance.compareTo(spend) < 0) return Optional.empty();

        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType;
        if(cryptoOpt.isEmpty()){
            return Optional.empty();
        }
        cryptoType = cryptoOpt.get();

        user.setBalance(currentUserBalance.subtract(spend));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.BUY.getDisplayValue());
        transaction.setSpendMoney(spend);
        transaction.setReceiveCrypto(quantity);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setCryptoType(cryptoType);
        transaction.setCurrCryptoPrice(cryptoType.getPrice());

        transactionRepository.save(transaction);
        userRepository.save(user);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType,user);
        Asset asset = assetOpt.orElseGet(Asset::new);
        if (asset.getTotalQuantity() == null) {
            asset.setTotalQuantity(BigDecimal.ZERO.add(quantity));
        } else {
            asset.setTotalQuantity(asset.getTotalQuantity().add(quantity));
        }
        asset.setUser(user);
        asset.setCryptoType(cryptoType);
        if (asset.getMoneyCurrency() == null) {
            asset.setMoneyCurrency(BigDecimal.ZERO.add(spend));
        } else {
            asset.setMoneyCurrency(asset.getMoneyCurrency().add(spend));
        }
        asset.setPriceDuringPurchase(cryptoType.getPrice());
        assetRepository.save(asset);

        return Optional.of(transaction);
    }

    @Override
    public BigDecimal getBalance(String email) {
        User user = findByEmail(email);
        return  user.getBalance();
    }

    @Override
    public BigDecimal getQuantityFromPair(String email, String pair) {
        BigDecimal quantity = new BigDecimal("0");
        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType = new CryptoType();
        if(cryptoOpt.isPresent()){
            cryptoType = cryptoOpt.get();
        }

        User user = findByEmail(email);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType, user);
        if(assetOpt.isPresent()){
            quantity = assetOpt.get().getTotalQuantity();
        }
        return quantity;
    }

    @Override
    public Optional<Transaction> sellCrypto(String email, String pair, BigDecimal spend, BigDecimal receiveMoney) {
       User user = findByEmail(email);

        if(user == null){
            return Optional.empty();
        }

        BigDecimal currentUserBalance = user.getBalance();
        BigDecimal currentQuantityFromPair = getQuantityFromPair(email, pair.replace("-","/"));

        if ( currentQuantityFromPair.compareTo(spend) < 0) return Optional.empty();

        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType;
        if(cryptoOpt.isEmpty()){
            return Optional.empty();
        }
        cryptoType = cryptoOpt.get();

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType,user);
        Asset asset = assetOpt.orElseGet(Asset::new);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.SELL.getDisplayValue());
        transaction.setDateTime(LocalDateTime.now());
        transaction.setCryptoType(cryptoType);
        transaction.setSpendCrypto(spend);
        transaction.setReceiveMoney(receiveMoney);
        transaction.setCurrCryptoPrice(cryptoType.getPrice());
        transaction.setProfitLoss(asset.getProfitLoss());

        transactionRepository.save(transaction);
        user.setBalance(currentUserBalance.add(receiveMoney));
        userRepository.save(user);


        BigDecimal remainingQuantity = asset.getTotalQuantity().subtract(spend);
        if(remainingQuantity.compareTo(new BigDecimal("0")) == 0){
            assetRepository.delete(asset);
        }else {
            asset.setTotalQuantity(remainingQuantity);
            asset.setUser(user);
            asset.setCryptoType(cryptoType);
            asset.setMoneyCurrency(asset.getMoneyCurrency().subtract(cryptoType.getPrice().multiply(spend)));
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
        user.setBalance(new BigDecimal( "10000"));
        userRepository.save(user);
    }
}
