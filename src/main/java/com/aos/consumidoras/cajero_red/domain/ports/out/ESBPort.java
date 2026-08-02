package com.aos.consumidoras.cajero_red.domain.ports.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ESBPort {
    UsuarioDTO obtenerUsuario(Integer usuarioId, String token);
    UsuarioDTO obtenerUsuarioPorTelefono(String telefono);
    SaldoResponse consultarSaldo(Long cuentaId, String token);
    List<MovimientoDTO> consultarMovimientos(Long cuentaId, String token);
    TransaccionResponse depositar(String clabeDestino, Monto monto, String referencia, String concepto, String token);
    TransaccionResponse retirar(Long cuentaId, Monto monto, String tipoRetiro, String concepto, String token);
    TransferenciaResponse transferir(Long cuentaOrigenId, String clabeDestino, Monto monto, String concepto, String token);
    GenerarCodigoRetiroResponse generarCodigoRetiro(BigDecimal monto, String moneda, Integer cuentaId, String token);
    ValidarCodigoRetiroResponse validarCodigoRetiro(String codigo, String token);
    void ejecutarRetiroSinTarjeta(Integer solicitudId, String token);
}