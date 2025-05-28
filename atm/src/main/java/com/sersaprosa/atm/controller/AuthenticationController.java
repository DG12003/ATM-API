package com.sersaprosa.atm.controller;

import com.sersaprosa.atm.dto.AuthenticatedUserDto;
import com.sersaprosa.atm.dto.UserDto;
import com.sersaprosa.atm.model.BankUser;
import com.sersaprosa.atm.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final ModelMapper modelMapper;
    private final UserService userService;

    public AuthenticationController(ModelMapper modelMapper, UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }
    @PostMapping("/v1/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody AuthenticatedUserDto bankUser) {
        BankUser registeredBankUser = userService.registerUser(bankUser);

        UserDto userDto = modelMapper.map(registeredBankUser, UserDto.class);

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/v1/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody AuthenticatedUserDto bankUser) {
        BankUser loggedBankUser = userService.loginUser(bankUser);

        UserDto userDto = modelMapper.map(loggedBankUser, UserDto.class);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

}
