package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TransferenciaResponse
{
    private Long transferenciaId;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private Monto monto;
    private String concepto;
    private OffsetDateTime fecha;
    private Monto saldoOrigenNuevo;
    private Monto saldoDestinoNuevo;
}