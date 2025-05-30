package com.sersaprosa.atm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private Integer id;
    private String firstName;
    private String secondName;
    private String email;
    private String pinNumber;
}
