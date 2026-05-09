package com.example.demo.controllers;

import com.example.demo.dtos.*;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.AuthService;
import exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository){
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto request){
        SignUpResponseDto response = new SignUpResponseDto();
        System.out.println(request.getEmail() + " " + request.getPassword());
        try {
            if(authService.signUp(request.getEmail(), request.getPassword())){
                response.setRequestStatus(RequestStatus.SUCCESS);
            }else{
                response.setRequestStatus(RequestStatus.FAILURE);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setRequestStatus(RequestStatus.FAILURE);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto responseDto = new LoginResponseDto();
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            System.out.println("Token" + token);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("AUTH_TOKEN", token);
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
            return response;
        }catch (Exception e){
            responseDto.setRequestStatus(RequestStatus.FAILURE);
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(responseDto, null, HttpStatus.BAD_REQUEST);
            return response;
        }
    }

    @GetMapping("/validate")
    public boolean validate(@RequestParam("token") String token) {
        return authService.validate(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDto> logout(@RequestBody LoginRequestDto request) {
        LoginResponseDto responseDto = new LoginResponseDto();
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            System.out.println("Token" + token);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("AUTH_TOKEN", token);
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
            return response;
        }catch (Exception e){
            responseDto.setRequestStatus(RequestStatus.FAILURE);
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(responseDto, null, HttpStatus.BAD_REQUEST);
            return response;
        }
    }

}
