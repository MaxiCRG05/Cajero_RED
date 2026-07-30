package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.Data;

@Data
public class TokenResponse
{
    private String token;
    private String refreshToken;
    private int expiraEn;
    private int usuarioId;
    private String clabe;
}