package com.aos.consumidoras.cajero_red.infrastructure.adapter.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class AuthAdapter implements AuthPort
{
    private final RestTemplate restTemplate;
    private final String authUrl;

    public AuthAdapter(RestTemplate restTemplate, @Value("${cajero.auth.url}") String authUrl)
    {
        this.restTemplate = restTemplate;
        this.authUrl = authUrl;
    }

    @Override
    public TokenResponse login(String correo, String contrasena)
    {
        String url = authUrl + "/api/Autenticacion/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("correo", correo, "contrasena", contrasena);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, entity, TokenResponse.class);
    }

    @Override
    public TokenResponse loginTarjeta(String numeroTarjeta, String nip)
    {
        String url = authUrl + "/api/Autenticacion/login-tarjeta";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of(
                "numeroTarjeta", numeroTarjeta,
                "nip", nip
        );
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, entity, TokenResponse.class);
    }
}