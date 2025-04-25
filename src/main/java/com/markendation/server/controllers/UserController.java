package com.markendation.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.markendation.server.dto.IngredientDto;
import com.markendation.server.dto.UserDto;
import com.markendation.server.services.IngredientService;
import com.markendation.server.services.UserService;
import com.markendation.server.utils.Location;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<UserDto> getUserInformation(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        UserDto response = userService.getInfo(userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/location")
    public ResponseEntity<UserDto> updateLocation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Location location) throws IOException {
        UserDto response = userService.updateLocation(userDetails.getUsername(), location);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
