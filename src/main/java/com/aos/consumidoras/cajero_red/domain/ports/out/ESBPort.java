package com.aos.consumidoras.cajero_red.domain.ports.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.Monto;
import com.aos.consumidoras.cajero_red.domain.model.dto.SaldoResponse;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransaccionResponse;
import com.aos.consumidoras.cajero_red.domain.model.dto.TransferenciaResponse;

public interface ESBPort
{
    SaldoResponse consultarSaldo(Long cuentaId, String token);
    TransaccionResponse depositar(String clabeDestino, Monto monto, String referencia, String concepto, String token);
    TransaccionResponse retirar(Long cuentaId, Monto monto, String tipoRetiro, String concepto, String token);
    TransferenciaResponse transferir(Long cuentaOrigenId, String clabeDestino, Monto monto, String concepto, String token);
}