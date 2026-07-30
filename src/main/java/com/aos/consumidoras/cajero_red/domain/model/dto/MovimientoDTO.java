package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoDTO
{
    private Long movimientoId;
    private Long cuentaId;
    private String descripcion;
    private BigDecimal monto;
    private String moneda;
    private String tipoMovimiento;
    private LocalDateTime fecha;
    private BigDecimal saldoPosterior;
}