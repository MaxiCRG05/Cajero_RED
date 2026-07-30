package com.aos.consumidoras.cajero_red.domain.ports.in.usecases;

import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;

public interface ConsultarSaldoUseCase
{
    SaldoResponse consultarSaldo(Long cuentaId, String token);
}