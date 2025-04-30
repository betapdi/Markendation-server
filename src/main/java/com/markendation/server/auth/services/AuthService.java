package com.markendation.server.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.markendation.server.auth.entities.User;
import com.markendation.server.auth.entities.UserRole;
import com.markendation.server.auth.repositories.UserRepository;

import com.markendation.server.auth.utils.AuthResponse;
import com.markendation.server.auth.utils.LoginRequest;
import com.markendation.server.auth.utils.RegisterRequest;
import com.markendation.server.models.Basket;
import com.markendation.server.repositories.primary.BasketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final BasketRepository basketRepository;

    public AuthResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullname(registerRequest.getFullname())
                .role(UserRole.USER)
                .build();
        
        Basket basket = new Basket(); Basket savedBasket = basketRepository.save(basket);
        System.out.println(savedBasket);
        user.setBasket(savedBasket); User savedUser = userRepository.save(user);
        System.out.println(savedUser);
        savedBasket.setUserId(savedUser.getId()); basketRepository.save(savedBasket);
        System.out.println(savedBasket); System.out.println(savedUser);

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            )
        );

        var user = userRepository.findByEmail(loginRequest.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
