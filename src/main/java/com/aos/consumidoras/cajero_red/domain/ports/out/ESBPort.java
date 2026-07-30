package com.aos.consumidoras.cajero_red.domain.ports.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.*;

import java.util.List;

public interface ESBPort
{
    UsuarioDTO obtenerUsuario(Integer usuarioId, String token);
    SaldoResponse consultarSaldo(Long cuentaId, String token);
    List<MovimientoDTO> consultarMovimientos(Long cuentaId, String token);
    TransaccionResponse depositar(String clabeDestino, Monto monto, String referencia, String concepto, String token);
    TransaccionResponse retirar(Long cuentaId, Monto monto, String tipoRetiro, String concepto, String token);
    TransferenciaResponse transferir(Long cuentaOrigenId, String clabeDestino, Monto monto, String concepto, String token);
}