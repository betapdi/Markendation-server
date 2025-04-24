package com.markendation.server.auth.utils;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    
    private String refreshToken;

}
