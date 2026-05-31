package com.cryptotrading.Crypto.Trading.App.config;

import com.cryptotrading.Crypto.Trading.App.repo.UserRepository;
import com.cryptotrading.Crypto.Trading.App.security.JwtAuthenticationFilter;
import com.cryptotrading.Crypto.Trading.App.service.impl.CryptoTradingAppUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                authorizeRequest -> authorizeRequest
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/login", "/register", "/api/auth/login",
                                "/verify-email", "/resend-verification-code").permitAll()
                        .anyRequest().authenticated()

        ).formLogin(
                formLogin ->{
                    formLogin
                            .loginPage("/login")
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/real-time-prices",true)
                            .failureForwardUrl("/login-error");
                }
        ).logout(
                logout ->{
                    logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true);
                }
        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();

    }

//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository){
//        return new CryptoTradingAppUserDetailsService(userRepository);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
