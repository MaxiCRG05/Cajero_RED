package com.aos.consumidoras.cajero_red.domain.services;

import com.aos.consumidoras.cajero_red.domain.model.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RetiroSinTarjetaService
{
    private static final int CODIGO_EXPIRATION_SECONDS = 300;
    private final Map<String, CodigoInfo> codigosMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RetiroSinTarjetaService()
    {
        scheduler.scheduleAtFixedRate(this::limpiarCodigosExpirados, 30, 30, TimeUnit.SECONDS);
    }

    public boolean esTelefonoValido(String telefono)
    {
        return telefono != null && telefono.matches("\\d{10}");
    }

    public String generarCodigo(String telefono)
    {
        Random random = new Random();
        String codigo = String.format("%08d", random.nextInt(100_000_000));
        Instant expiracion = Instant.now().plusSeconds(CODIGO_EXPIRATION_SECONDS);
        CodigoInfo info = codigosMap.computeIfAbsent(telefono, k -> new CodigoInfo());
        info.codigo = codigo;
        info.expiracion = expiracion;
        info.telefono = telefono;
        return codigo;
    }

    public void guardarUsuario(String telefono, UsuarioDTO usuario)
    {
        CodigoInfo info = codigosMap.computeIfAbsent(telefono, k -> new CodigoInfo());
        info.usuario = usuario;
    }

    public UsuarioDTO obtenerUsuario(String telefono)
    {
        CodigoInfo info = codigosMap.get(telefono);
        return info != null ? info.usuario : null;
    }

    public UsuarioDTO validarYRecuperarUsuario(String telefono, String codigo)
    {
        CodigoInfo info = codigosMap.get(telefono);
        if (info == null) {
            return null;
        }
        if (Instant.now().isAfter(info.expiracion)) {
            codigosMap.remove(telefono);
            return null;
        }
        if (info.codigo.equals(codigo)) {
            return info.usuario;
        }
        return null;
    }

    public void limpiarCodigo(String telefono)
    {
        codigosMap.remove(telefono);
    }

    private void limpiarCodigosExpirados()
    {
        Instant now = Instant.now();
        codigosMap.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiracion));
    }

    private static class CodigoInfo
    {
        String codigo;
        Instant expiracion;
        String telefono;
        UsuarioDTO usuario;
    }
}