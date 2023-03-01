package com.example.ludo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private String loggedInUserId;
    private UserRole userRole;
}
