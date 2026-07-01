package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned by {@code /auth/register} and {@code /auth/login}.
 * The Angular app stores {@code accessToken} in localStorage and
 * attaches it via the JWT interceptor on subsequent requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;   // set explicitly to "Bearer" in AuthService.buildAuthResponse()
    private Long   userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
