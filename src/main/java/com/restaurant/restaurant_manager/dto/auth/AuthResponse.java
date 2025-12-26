package com.restaurant.restaurant_manager.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restaurant.restaurant_manager.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String email;
    private String fullName;
    private UserRole role;
    private String avatar;

    private UUID customerId;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
}