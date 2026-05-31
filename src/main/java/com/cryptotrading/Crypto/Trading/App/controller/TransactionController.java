package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.entity.Transaction;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.TransactionService;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/transaction-history")
    public String transactionHistory(Principal principal, Model model){
        List<Transaction> transactions = transactionService.getHistoryByEmail(principal.getName());
        User user = userService.findByEmail(principal.getName());

        model.addAttribute("transactions",transactions);
        model.addAttribute("user", user);
        return "transaction-history";
    }
}
