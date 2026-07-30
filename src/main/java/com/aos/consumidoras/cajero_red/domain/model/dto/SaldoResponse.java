package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoResponse
{
    private Long cuentaId;
    private BigDecimal saldo;
    private String moneda;
}