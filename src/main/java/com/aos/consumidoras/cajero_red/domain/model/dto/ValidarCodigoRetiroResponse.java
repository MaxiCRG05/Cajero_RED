package com.aos.consumidoras.cajero_red.domain.model.dto;

import java.math.BigDecimal;

public class ValidarCodigoRetiroResponse
{
    private Integer solicitudId;
    private Integer usuarioId;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer cuentaId;
    private BigDecimal monto;
    private String moneda;

    public ValidarCodigoRetiroResponse() {}

    public Integer getSolicitudId() { return solicitudId; }
    public void setSolicitudId(Integer solicitudId) { this.solicitudId = solicitudId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public Integer getCuentaId() { return cuentaId; }
    public void setCuentaId(Integer cuentaId) { this.cuentaId = cuentaId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}