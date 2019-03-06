package com.fuelrewards.userservice.controller;

import com.fuelrewards.userservice.message.request.LoginForm;
import com.fuelrewards.userservice.message.request.SignupForm;
import com.fuelrewards.userservice.message.response.JwtResponse;
import com.fuelrewards.userservice.model.User;
import com.fuelrewards.userservice.repository.UserRepository;
import com.fuelrewards.userservice.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    private JwtResponse getJwt(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new JwtResponse(jwtProvider.generateJwtToken(authentication));
    }

    @CrossOrigin
    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        JwtResponse jwtResponse = getJwt(loginRequest.getEmail(), loginRequest.getPassword());

        User user = userRepository.findByEmail(loginRequest.getEmail()).get();

        String response = "";
        response += "{";
        response += "\"accessToken\":\""+jwtResponse.getAccessToken()+"\",";
        response += "\"firstName\":\""+user.getFirstName()+"\",";
        response += "\"lastName\":\""+user.getLastName()+"\",";
        response += "\"email\":\""+user.getEmail()+"\",";
        response += "\"homeOffice\":\""+user.getCardNumber()+"\"";
        response += "}";

        return ResponseEntity.ok(response);
    }

    @CrossOrigin
    @PostMapping("/api/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupForm signUpRequest) {


        String lowerCasedEmail = signUpRequest.getEmail().toLowerCase(); // always lower case email


        if(userRepository.existsByEmail(lowerCasedEmail)) {
            return new ResponseEntity<>("{\"message\": \"That email already exists\"}", HttpStatus.BAD_REQUEST);
        }

        User user = new User(lowerCasedEmail, encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(), signUpRequest.getLastName());
        userRepository.save(user);

        return ResponseEntity.ok(getJwt(signUpRequest.getEmail(), signUpRequest.getPassword()));
    }
}