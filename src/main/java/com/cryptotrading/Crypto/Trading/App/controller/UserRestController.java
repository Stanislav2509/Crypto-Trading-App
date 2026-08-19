package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserProfileDTO;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
                user.getEmail()
        );
    }
}
