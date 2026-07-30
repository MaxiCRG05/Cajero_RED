package com.aos.consumidoras.cajero_red.domain.ports.in.usecases;

import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransferenciaResponse;

public interface RealizarTransferenciaUseCase
{
    TransferenciaResponse transferir(Long cuentaOrigenId, String clabeDestino, Monto monto, String concepto, String token);
}