package com.fuelrewards.userservice.controller;

import com.fuelrewards.userservice.message.request.UpdateProfileForm;
import com.fuelrewards.userservice.model.User;
import com.fuelrewards.userservice.repository.UserRepository;
import com.fuelrewards.userservice.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ProfileController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    private Optional<User> getUserFromAuthHeader(String authHeader) {
        String authToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authToken = authHeader.replace("Bearer ","");
        }

        Optional<User> user = Optional.empty();

        if (authToken!= null) {
            // username is equivalent to email
            String username = jwtProvider.getUserNameFromJwtToken(authToken);
            user = userRepository.findByEmail(username);
        }

        return user;
    }

    @CrossOrigin
    @GetMapping("/api/profile")
    public ResponseEntity<?> fetchProfile(@RequestHeader("Authorization") String authHeader) {

        Optional<User> user = getUserFromAuthHeader(authHeader);

        if (user.isPresent()) {
            System.out.println(user.get());

            return ResponseEntity.ok(user.get());
        } else {
            return new ResponseEntity<>("{\"message\": \"Could not find user\"}", HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/api/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                           @Valid @RequestBody UpdateProfileForm updateProfileForm) {

        Optional<User> user = getUserFromAuthHeader(authHeader);

        if (user.isPresent()) {
            userRepository.save(user.get()); // update user
            return ResponseEntity.ok(user.get());
        } else {
            return new ResponseEntity<>("{\"message\": \"Could not find user\"}", HttpStatus.BAD_REQUEST);
        }
    }
}