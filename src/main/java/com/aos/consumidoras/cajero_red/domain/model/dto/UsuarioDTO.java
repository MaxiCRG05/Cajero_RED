package com.aos.consumidoras.cajero_red.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsuarioDTO
{
    @JsonProperty("Id")
    private Integer id;

    @JsonProperty("Nombres")
    private String nombres;

    @JsonProperty("ApellidoPaterno")
    private String apellidoPaterno;

    @JsonProperty("ApellidoMaterno")
    private String apellidoMaterno;

    @JsonProperty("Correo")
    private String correo;

    @JsonProperty("Telefono")
    private String telefono;

    @JsonProperty("Activo")
    private Boolean activo;
}