package com.aos.consumidoras.cajero_red.domain.ports.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;

public interface AuthPort
{
    TokenResponse login(String correo, String contrasena);
    TokenResponse loginTarjeta(String numeroTarjeta, String nip);
    TokenResponse obtenerTokenPorUsuarioId(Integer usuarioId);
}