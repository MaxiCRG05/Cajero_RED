package com.aos.consumidoras.cajero_red.application;

import com.aos.consumidoras.cajero_red.domain.model.dto.TokenResponse;

public class SessionManager
{
    private static final SessionManager instance = new SessionManager();
    private String token;
    private Integer usuarioId;
    private String usuarioNombre;
    private String clabe;

    private SessionManager() { }

    public static SessionManager getInstance()
    {
        return instance;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public Integer getUsuarioId()
    {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId)
    {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre()
    {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre)
    {
        this.usuarioNombre = usuarioNombre;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }

    public void setTokenResponse(TokenResponse response)
    {
        this.token = response.getToken();
        this.usuarioId = response.getUsuarioId();
        this.clabe = response.getClabe();
    }
}