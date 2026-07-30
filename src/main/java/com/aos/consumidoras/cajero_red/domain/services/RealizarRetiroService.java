package com.aos.consumidoras.cajero_red.domain.services;

import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.RealizarRetiroUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RealizarRetiroService implements RealizarRetiroUseCase
{
    private final ESBPort esbPort;

    @Override
    public TransaccionResponse retirar(Long cuentaId, Monto monto, String tipoRetiro, String concepto, String token)
    {
        return esbPort.retirar(cuentaId, monto, tipoRetiro, concepto, token);
    }
}