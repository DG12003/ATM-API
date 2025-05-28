package com.sersaprosa.atm.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthenticatedUserDto {
    private String firstName;
    private String secondName;
    private String email;
    private String pinNumber;
}
