package com.bnd.backend.domain.jwt.dto;

public record JWTResponseDTO(String accessToken, String refreshToken) {
}