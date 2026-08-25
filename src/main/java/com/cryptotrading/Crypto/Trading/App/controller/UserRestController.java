package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.ConvertBalanceRequest;
import com.cryptotrading.Crypto.Trading.App.model.dto.UserProfileDTO;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserProfileDTO getCurrentUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return new UserProfileDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBalance(),
                user.getBalanceCurrency(),
                user.getEmail()
        );
    }

    @DeleteMapping("/reset-profile")
    public UserProfileDTO resetProfile(Principal principal) {
        userService.resetProfile(principal.getName());
        User user = userService.findByEmail(principal.getName());
        return new UserProfileDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBalance(),
                user.getBalanceCurrency(),
                user.getEmail()
        );
    }

    @PostMapping("/convert-balance")
    public ResponseEntity<?> convertBalance(@RequestBody ConvertBalanceRequest request, Principal principal) {
        try {
            User user = userService.convertBalance(principal.getName(), request.targetCurrency());
            return ResponseEntity.ok(new UserProfileDTO(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getBalance(),
                    user.getBalanceCurrency(),
                    user.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
