package org.example.authexample.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.authexample.dto.user.UserAuthDto;
import org.example.authexample.dto.user.UserDto;
import org.example.authexample.dto.user.UserRegistrationDto;
import org.example.authexample.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDto> createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        return new ResponseEntity<>(userService.addUser(userRegistrationDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserAuthDto userCredentialDto , HttpServletResponse response) {
        return new ResponseEntity<>(userService.login(userCredentialDto, response), HttpStatus.OK);
    }

    @GetMapping("/current-user")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        String number = user.getUsername();
        return userService.findByPhoneNumber(number);
    }
}
