package com.aos.consumidoras.cajero_red.domain.services;

import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarDepositoUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RealizarDepositoService implements RealizarDepositoUseCase
{
    private final ESBPort esbPort;

    @Override
    public TransaccionResponse depositar(String clabeDestino, Monto monto, String referencia, String concepto, String token)
    {
        return esbPort.depositar(clabeDestino, monto, referencia, concepto, token);
    }
}