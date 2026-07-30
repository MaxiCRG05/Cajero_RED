package com.aos.consumidoras.cajero_red.domain.ports.in.usecases;

import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;

public interface RealizarDepositoUseCase
{
    TransaccionResponse depositar(String clabeDestino, Monto monto, String referencia, String concepto, String token);
}