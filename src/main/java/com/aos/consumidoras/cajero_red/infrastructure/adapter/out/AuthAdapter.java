package com.aos.consumidoras.cajero_red.infrastructure.adapter.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;
import com.aos.consumidoras.cajero_red.domain.ports.out.AuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuthAdapter implements AuthPort
{
    private final RestTemplate restTemplate;
    private final String authUrl;
    private final String internalApiKey;

    public AuthAdapter(
            RestTemplate restTemplate,
            @Value("${cajero.auth.url}") String authUrl,
            @Value("${internal.api.key}") String internalApiKey)
    {
        this.restTemplate = restTemplate;
        this.authUrl = authUrl;
        this.internalApiKey = internalApiKey;
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
        Map<String, String> body = Map.of("numeroTarjeta", numeroTarjeta, "nip", nip);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, entity, TokenResponse.class);
    }

    @Override
    public TokenResponse obtenerTokenPorUsuarioId(Integer usuarioId)
    {
        String url = authUrl + "/api/Autenticacion/token/" + usuarioId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Key", internalApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                TokenResponse.class
        );
        return response.getBody();
    }
}