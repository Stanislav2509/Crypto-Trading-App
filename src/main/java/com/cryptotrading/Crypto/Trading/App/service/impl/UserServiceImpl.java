package com.cryptotrading.Crypto.Trading.App.service.impl;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.Asset;
import com.cryptotrading.Crypto.Trading.App.model.entity.CryptoType;
import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.model.enums.MoneyCurrency;
import com.cryptotrading.Crypto.Trading.App.model.enums.TransactionType;
import com.cryptotrading.Crypto.Trading.App.repo.AssetRepository;
import com.cryptotrading.Crypto.Trading.App.repo.CryptoTypeRepository;
import com.cryptotrading.Crypto.Trading.App.repo.TransactionRepository;
import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import com.cryptotrading.Crypto.Trading.App.service.CurrencyConversionService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
 public class UserServiceImpl implements UserService {
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    private final CryptoTypeRepository cryptoTypeRepository;
    private final AssetRepository assetRepository;
    private final EmailServiceImpl emailService;
    private final CurrencyConversionService currencyConversionService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TransactionRepository transactionRepository, CryptoTypeRepository cryptoTypeRepository, AssetRepository assetRepository, EmailServiceImpl emailService, CurrencyConversionService currencyConversionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository = transactionRepository;
        this.cryptoTypeRepository = cryptoTypeRepository;
        this.assetRepository = assetRepository;

        this.emailService = emailService;
        this.currencyConversionService = currencyConversionService;
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
        user.setBalance(INITIAL_BALANCE);
        user.setBalanceCurrency(MoneyCurrency.USD);

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        user.setEnabled(false);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(1));

        userRepository.save(user);

        emailService.sendVerificationCode(user.getEmail(), code);

        userRepository.save(user);

        return true;
    }
    @Override
    public Optional<Transaction> buyCrypto(String email, String pair, BigDecimal spend) {
        if (spend == null || spend.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        User user = findByEmail(email);
        if (user == null) {
            return Optional.empty();
        }

        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType = checkCryptoTypeAvailable(cryptoOpt);
        if (cryptoType == null) {
            return Optional.empty();
        }

        BigDecimal price = cryptoType.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        MoneyCurrency accountCurrency = user.getBalanceCurrency();
        if (accountCurrency == null) {
            accountCurrency = MoneyCurrency.USD;
        }

        BigDecimal currentUserBalance = user.getBalance();
        if (currentUserBalance.compareTo(spend) < 0) {
            return Optional.empty();
        }

        BigDecimal usdSpend = accountCurrency == MoneyCurrency.EUR
                ? currencyConversionService.convert(spend, MoneyCurrency.EUR, MoneyCurrency.USD)
                : spend;

        BigDecimal cryptoQuantity = usdSpend.divide(price, 8, RoundingMode.DOWN);
        if (cryptoQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        user.setBalance(currentUserBalance.subtract(spend));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.BUY.getDisplayValue());
        transaction.setSpendMoney(spend);
        transaction.setReceiveCrypto(cryptoQuantity);
        transaction.setCurrency(accountCurrency);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setCryptoType(cryptoType);
        transaction.setCurrCryptoPrice(price);

        transactionRepository.save(transaction);
        userRepository.save(user);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType,user);
        Asset asset = assetOpt.orElseGet(Asset::new);
        if (asset.getTotalQuantity() == null) {
            asset.setTotalQuantity(BigDecimal.ZERO.add(cryptoQuantity));
        } else {
            asset.setTotalQuantity(asset.getTotalQuantity().add(cryptoQuantity));
        }
        asset.setUser(user);
        asset.setCryptoType(cryptoType);
        if (asset.getMoneyCurrency() == null) {
            asset.setMoneyCurrency(BigDecimal.ZERO.add(usdSpend));
        } else {
            asset.setMoneyCurrency(asset.getMoneyCurrency().add(usdSpend));
        }
        asset.setPriceDuringPurchase(price);
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
        CryptoType cryptoType = checkCryptoTypeAvailable(cryptoOpt);

        User user = findByEmail(email);

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType, user);
        if(assetOpt.isPresent()){
            quantity = assetOpt.get().getTotalQuantity();
        }
        return quantity;
    }

    @Override
    public Optional<Transaction> sellCrypto(String email, String pair, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        User user = findByEmail(email);
        if (user == null) {
            return Optional.empty();
        }

        Optional<CryptoType> cryptoOpt = cryptoTypeRepository.findBySymbol(pair.replace("-", "/"));
        CryptoType cryptoType = checkCryptoTypeAvailable(cryptoOpt);
        if (cryptoType == null) {
            return Optional.empty();
        }

        BigDecimal price = cryptoType.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        Optional<Asset> assetOpt = assetRepository.findByCryptoTypeAndUser(cryptoType, user);
        if (assetOpt.isEmpty() || assetOpt.get().getTotalQuantity() == null
                || assetOpt.get().getTotalQuantity().compareTo(quantity) < 0) {
            return Optional.empty();
        }
        Asset asset = assetOpt.get();

        MoneyCurrency accountCurrency = user.getBalanceCurrency();
        if (accountCurrency == null) {
            accountCurrency = MoneyCurrency.USD;
        }

        BigDecimal cashValueUsd = quantity.multiply(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal creditAmount = accountCurrency == MoneyCurrency.EUR
                ? currencyConversionService.convert(cashValueUsd, MoneyCurrency.USD, MoneyCurrency.EUR)
                : cashValueUsd;

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.SELL.getDisplayValue());
        transaction.setDateTime(LocalDateTime.now());
        transaction.setCryptoType(cryptoType);
        transaction.setSpendCrypto(quantity);
        transaction.setReceiveMoney(creditAmount);
        transaction.setCurrency(accountCurrency);
        transaction.setCurrCryptoPrice(price);
        transaction.setProfitLoss(asset.getProfitLoss());

        transactionRepository.save(transaction);
        user.setBalance(user.getBalance().add(creditAmount));
        userRepository.save(user);

        BigDecimal remainingQuantity = asset.getTotalQuantity().subtract(quantity);
        if(remainingQuantity.compareTo(new BigDecimal("0")) == 0){
            assetRepository.delete(asset);
        }else {
            asset.setTotalQuantity(remainingQuantity);
            asset.setUser(user);
            asset.setCryptoType(cryptoType);
            asset.setMoneyCurrency(asset.getMoneyCurrency().subtract(price.multiply(quantity)));
            assetRepository.save(asset);
        }


        return Optional.of(transaction);
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return checkUserAvailable(userOpt);
    }

    @Override
    public void resetProfile(String email) {
        User user = findByEmail(email);
        List<Asset> assets= assetRepository.findAllByUser(user);
        assetRepository.deleteAll(assets);
        List<Transaction> transactions = transactionRepository.findAllByUser(user);
        transactionRepository.deleteAll(transactions);
        user.setBalance(INITIAL_BALANCE);
        user.setBalanceCurrency(MoneyCurrency.USD);
        userRepository.save(user);
    }

    @Override
    public boolean verifyEmail(String email, String code) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (user.isEnabled()) {
            return true;
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!user.getVerificationCode().equals(code)) {
            return false;
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);

        userRepository.save(user);

        return true;
    }

    @Override
    public boolean resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return false;
        }
        User user = optionalUser.get();

        if(user.isEnabled()){
            return false;
        }
        String newCode = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(1));
        userRepository.save(user);
        emailService.sendVerificationCode(email, newCode);
        return false;
    }

    @Override
    @Transactional
    public void creditBalance(String email, BigDecimal amount) {
        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalStateException("User not found: " + email);
        }
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User convertBalance(String email, MoneyCurrency targetCurrency) {
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency is required");
        }
        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + email);
        }
        MoneyCurrency currentCurrency = user.getBalanceCurrency();
        if (currentCurrency == null) {
            currentCurrency = MoneyCurrency.USD;
        }
        if (targetCurrency == currentCurrency) {
            throw new IllegalArgumentException("Balance is already in " + targetCurrency);
        }

        BigDecimal convertedBalance = currencyConversionService.convert(user.getBalance(), currentCurrency, targetCurrency);
        user.setBalance(convertedBalance);
        user.setBalanceCurrency(targetCurrency);
        userRepository.save(user);

        return user;
    }

    private User checkUserAvailable(Optional<User> user){
        return user.orElse(null);
    }
    private CryptoType checkCryptoTypeAvailable(Optional<CryptoType> cryptoType){
        return cryptoType.orElse(null);
    }
}
