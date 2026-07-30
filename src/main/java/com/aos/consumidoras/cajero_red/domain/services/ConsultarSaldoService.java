package com.aos.consumidoras.cajero_red.domain.services;

import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarSaldoUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultarSaldoService implements ConsultarSaldoUseCase
{
    private final ESBPort esbPort;

    @Override
    public SaldoResponse consultarSaldo(Long cuentaId, String token)
    {
        return esbPort.consultarSaldo(cuentaId, token);
    }
}