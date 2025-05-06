package com.markendation.server.services;

import org.springframework.stereotype.Service;

import com.markendation.server.auth.entities.User;
import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.classes.Location;
import com.markendation.server.dto.UserDto;
import com.markendation.server.exceptions.UserNotFoundException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        UserDto response = new UserDto();
        response.update(user);

        return response;
    }

    public UserDto updateLocation(String email, Location location) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        user.setLocation(location); User savedUser = userRepository.save(user);

        UserDto response = new UserDto();
        response.update(savedUser);

        return response;
    }
}
