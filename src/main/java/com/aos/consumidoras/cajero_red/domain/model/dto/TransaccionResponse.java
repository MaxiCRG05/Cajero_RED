package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransaccionResponse
{
    private Long transaccionId;
    private Long cuentaId;
    private BigDecimal montoCantidad;
    private String montoMoneda;
    private LocalDateTime fecha;
    private String metodo;
    private String referencia;
    private String tipoRetiro;
}