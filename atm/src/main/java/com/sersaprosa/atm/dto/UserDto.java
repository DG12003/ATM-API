package com.sersaprosa.atm.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Setter
@Getter
public class UserDto {
    private UUID id;
    private String firstName;
    private String secondName;
    private String email;
    private String pinNumber;
}
