package com.aos.consumidoras.cajero_red.domain.ports.in.usecases;

import com.aos.consumidoras.cajero_red.domain.model.dto.*;

public interface RealizarRetiroUseCase
{
    TransaccionResponse retirar(Long cuentaId, Monto monto, String tipoRetiro, String concepto, String token);
}