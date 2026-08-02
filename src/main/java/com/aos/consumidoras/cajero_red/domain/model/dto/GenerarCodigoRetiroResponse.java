package com.aos.consumidoras.cajero_red.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerarCodigoRetiroResponse
{
    @JsonProperty("exito")
    private Boolean exito;
    @JsonProperty("codigo")
    private String codigo;
    @JsonProperty("solicitudId")
    private Integer solicitudId;
    @JsonProperty("mensaje")
    private String mensaje;

    public GenerarCodigoRetiroResponse() {}

    public Boolean getExito() { return exito; }
    public void setExito(Boolean exito) { this.exito = exito; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Integer getSolicitudId() { return solicitudId; }
    public void setSolicitudId(Integer solicitudId) { this.solicitudId = solicitudId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}