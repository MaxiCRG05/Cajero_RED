package com.aos.consumidoras.cajero_red.domain.services;

import com.aos.consumidoras.cajero_red.domain.model.dto.MovimientoDTO;
import com.aos.consumidoras.cajero_red.domain.ports.in.usecases.ConsultarMovimientosUseCase;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarMovimientosService implements ConsultarMovimientosUseCase
{
    private final ESBPort esbPort;

    @Override
    public List<MovimientoDTO> consultarMovimientos(Long cuentaId, String token)
    {
        return esbPort.consultarMovimientos(cuentaId, token);
    }
}