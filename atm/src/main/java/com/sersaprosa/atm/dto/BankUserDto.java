package com.sersaprosa.atm.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class BankUserDto {
    private UUID id;
    private String firstName;
    private String secondName;
    private String email;
    private String pinNumber;
}
