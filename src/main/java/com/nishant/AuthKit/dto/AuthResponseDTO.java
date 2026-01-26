package com.nishant.AuthKit.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private UserResponseDTO user;
}
