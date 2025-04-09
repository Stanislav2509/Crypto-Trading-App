package com.cryptotrading.Crypto.Trading.App.controller;

import com.cryptotrading.Crypto.Trading.App.model.dto.UserRegisterBindingModel;
import com.cryptotrading.Crypto.Trading.App.model.entity.User;
import com.cryptotrading.Crypto.Trading.App.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(@ModelAttribute("userRegisterBindingModel")
                           UserRegisterBindingModel userRegisterBindingModel){

        return "register";
    }

    @PostMapping("/register")
    public ModelAndView register(@ModelAttribute("userRegisterBindingModel") @Valid
                                 UserRegisterBindingModel userRegisterBindingModel, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ModelAndView("register");
        }

        boolean hasSuccessfullyRegistration = userService.register(userRegisterBindingModel);

        if(!hasSuccessfullyRegistration){
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("hasRegistrationError", true);
            return modelAndView;
        }

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public String login(){

        return "login";
    }

    @PostMapping("/login-error")
    public ModelAndView failLogin(@ModelAttribute("email") String email){
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("email", email);
        modelAndView.addObject("bad_credentials", "true");

        return modelAndView;
    }

    @GetMapping("/warning-reset-profile")
    public String warningResetProfile(Model model ,Principal principal){
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "warning-reset-profile";
    }

    @DeleteMapping("/reset-profile")
    public String resetProfile(Principal principal){
        userService.resetProfile(principal.getName());
        return "redirect:/real-time-prices";
    }
}
