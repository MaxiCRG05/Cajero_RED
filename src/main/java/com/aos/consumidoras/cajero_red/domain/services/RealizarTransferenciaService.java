package com.aos.consumidoras.cajero_red.domain.services;

import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransferenciaResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarTransferenciaUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RealizarTransferenciaService implements RealizarTransferenciaUseCase
{
    private final ESBPort esbPort;

    @Override
    public TransferenciaResponse transferir(Long cuentaOrigenId, String clabeDestino, Monto monto, String concepto, String token)
    {
        return esbPort.transferir(cuentaOrigenId, clabeDestino, monto, concepto, token);
    }
}