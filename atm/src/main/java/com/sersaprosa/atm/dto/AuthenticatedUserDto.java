package com.sersaprosa.atm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthenticatedUserDto {
    @NotNull(message = "firstName cannot be null")
    @NotEmpty(message = "firstname cannot be empty")
    private String firstName;
    @NotNull(message = "secondName cannot be null")
    @NotEmpty(message = "secondName cannot be empty")
    private String secondName;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "\\d{4}",
            message = "Pin Number must be four digits")
    private String pinNumber;
}
