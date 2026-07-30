package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Monto
{
    private BigDecimal cantidad;
    private String moneda;
}