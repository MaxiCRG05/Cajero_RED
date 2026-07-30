package com.aos.consumidoras.cajero_red.domain.ports.in.usecases;

import com.aos.consumidoras.cajero_red.domain.model.dto.MovimientoDTO;
import java.util.List;

public interface ConsultarMovimientosUseCase
{
    List<MovimientoDTO> consultarMovimientos(Long cuentaId, String token);
}