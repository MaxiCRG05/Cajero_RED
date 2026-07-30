package com.aos.consumidoras.cajero_red.infrastructure.adapter.out;

import com.aos.consumidoras.cajero_red.domain.model.dto.*;
import com.aos.consumidoras.cajero_red.domain.ports.out.ESBPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ESBAdapter implements ESBPort
{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String esbUrl;

    public ESBAdapter(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${cajero.esb.url}") String esbUrl)
    {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.esbUrl = esbUrl;
    }

    @Override
    public UsuarioDTO obtenerUsuario(Integer usuarioId, String token)
    {
        ESBRequest request = buildRequest("CONSULTA_USUARIO", token, null);
        ESBResponse response = execute(request, token);
        if (!response.getBody().isExito())
            throw new RuntimeException(response.getBody().getMensaje());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(response.getBody().getDatos(), UsuarioDTO.class);
    }

    @Override
    public SaldoResponse consultarSaldo(Long cuentaId, String token)
    {
        ESBRequest request = buildRequest("CONSULTA", token,
                Map.of("tipoConsulta", "SALDO_CUENTA", "parametros", Map.of("cuentaId", cuentaId)));
        ESBResponse response = execute(request, token);
        if (!response.getBody().isExito())
            throw new RuntimeException(response.getBody().getMensaje());
        Map<String, Object> datos = (Map<String, Object>) response.getBody().getDatos();
        return new SaldoResponse(
                ((Number) datos.get("CuentaId")).longValue(),
                new java.math.BigDecimal(datos.get("Saldo").toString()),
                datos.get("Moneda").toString()
        );
    }

    @Override
    public List<MovimientoDTO> consultarMovimientos(Long cuentaId, String token)
    {
        ESBRequest request = buildRequest("CONSULTA", token,
                Map.of("tipoConsulta", "MOVIMIENTOS", "parametros", Map.of("cuentaId", cuentaId)));
        ESBResponse response = execute(request, token);
        if (!response.getBody().isExito())
            throw new RuntimeException(response.getBody().getMensaje());

        List<MovimientoDTO> movimientos = new ArrayList<>();
        Map<String, Object> datos = (Map<String, Object>) response.getBody().getDatos();
        if (datos.containsKey("movimientos")) {
            List<Map<String, Object>> lista = (List<Map<String, Object>>) datos.get("movimientos");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            for (Map<String, Object> item : lista) {
                MovimientoDTO dto = new MovimientoDTO();
                dto.setMovimientoId(((Number) item.get("movimientoId")).longValue());
                dto.setCuentaId(((Number) item.get("cuentaId")).longValue());
                dto.setDescripcion((String) item.get("descripcion"));
                dto.setMonto(new BigDecimal(item.get("monto").toString()));
                dto.setMoneda((String) item.get("moneda"));
                dto.setTipoMovimiento((String) item.get("tipoMovimiento"));

                String fechaStr = (String) item.get("fecha");
                LocalDateTime fecha = LocalDateTime.parse(fechaStr, formatter);
                dto.setFecha(fecha);

                if (item.containsKey("saldoPosterior") && item.get("saldoPosterior") != null) {
                    dto.setSaldoPosterior(new BigDecimal(item.get("saldoPosterior").toString()));
                }
                movimientos.add(dto);
            }
        }
        return movimientos;
    }

    @Override
    public TransaccionResponse depositar(String clabeDestino, Monto monto, String referencia, String concepto, String token)
    {
        Map<String, Object> body = Map.of(
                "metodo", "EFECTIVO",
                "monto", Map.of("cantidad", monto.getCantidad(), "moneda", monto.getMoneda()),
                "clabeDestino", clabeDestino,
                "referencia", referencia,
                "concepto", concepto
        );
        ESBRequest request = buildRequest("DEPOSITO", token, body);
        ESBResponse response = execute(request, token);
        if (!response.getBody().isExito())
            throw new RuntimeException(response.getBody().getMensaje());
        Map<String, Object> datos = (Map<String, Object>) response.getBody().getDatos();
        TransaccionResponse trans = new TransaccionResponse();
        trans.setTransaccionId(((Number) datos.get("transaccionId")).longValue());
        trans.setCuentaId(((Number) datos.get("cuentaId")).longValue());
        trans.setMontoCantidad(new java.math.BigDecimal(datos.get("montoCantidad").toString()));
        trans.setMontoMoneda(datos.get("montoMoneda").toString());
        String fechaStr = (String) datos.get("fecha");
        trans.setFecha(LocalDateTime.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        trans.setMetodo((String) datos.get("metodo"));
        trans.setReferencia((String) datos.get("referencia"));
        return trans;
    }

    @Override
    public TransaccionResponse retirar(Long cuentaId, Monto monto, String tipoRetiro, String concepto, String token)
    {
        Map<String, Object> body = Map.of(
                "tipoRetiro", tipoRetiro,
                "monto", Map.of("cantidad", monto.getCantidad(), "moneda", monto.getMoneda()),
                "cuentaId", cuentaId,
                "concepto", concepto
        );
        ESBRequest request = buildRequest("RETIRO", token, body);
        ESBResponse response = execute(request, token);
        if (!response.getBody().isExito())
            throw new RuntimeException(response.getBody().getMensaje());
        Map<String, Object> datos = (Map<String, Object>) response.getBody().getDatos();
        TransaccionResponse trans = new TransaccionResponse();
        trans.setTransaccionId(((Number) datos.get("transaccionId")).longValue());
        trans.setCuentaId(((Number) datos.get("cuentaId")).longValue());
        trans.setMontoCantidad(new java.math.BigDecimal(datos.get("montoCantidad").toString()));
        trans.setMontoMoneda(datos.get("montoMoneda").toString());
        String fechaStr = (String) datos.get("fecha");
        trans.setFecha(LocalDateTime.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        trans.setTipoRetiro((String) datos.get("tipoRetiro"));
        return trans;
    }

    @Override
    public TransferenciaResponse transferir(Long cuentaOrigenId, String clabeDestino, Monto monto, String concepto, String token)
    {
        Map<String, Object> body = Map.of(
                "monto", Map.of("cantidad", monto.getCantidad(), "moneda", monto.getMoneda()),
                "clabeDestino", clabeDestino,
                "concepto", concepto,
                "cuentaOrigenId", cuentaOrigenId
        );
        ESBRequest request = buildRequest("TRANSFERENCIA", token, body);
        ESBResponse response = execute(request, token);
        if (!response.getBody().isExito())
            throw new RuntimeException(response.getBody().getMensaje());
        Map<String, Object> datos = (Map<String, Object>) response.getBody().getDatos();
        TransferenciaResponse transfer = new TransferenciaResponse();
        transfer.setTransferenciaId(((Number) datos.get("transferenciaId")).longValue());
        transfer.setCuentaOrigenId(((Number) datos.get("cuentaOrigenId")).longValue());
        transfer.setCuentaDestinoId(((Number) datos.get("cuentaDestinoId")).longValue());
        Map<String, Object> montoData = (Map<String, Object>) datos.get("monto");
        transfer.setMonto(new Monto(
                new java.math.BigDecimal(montoData.get("cantidad").toString()),
                (String) montoData.get("moneda")
        ));
        transfer.setConcepto((String) datos.get("concepto"));
        String fechaStr = (String) datos.get("fecha");
        transfer.setFecha(OffsetDateTime.parse(fechaStr));
        Map<String, Object> saldoOrigenNuevo = (Map<String, Object>) datos.get("saldoOrigenNuevo");
        if (saldoOrigenNuevo != null)
            transfer.setSaldoOrigenNuevo(new Monto(
                    new java.math.BigDecimal(saldoOrigenNuevo.get("cantidad").toString()),
                    (String) saldoOrigenNuevo.get("moneda")
            ));
        Map<String, Object> saldoDestinoNuevo = (Map<String, Object>) datos.get("saldoDestinoNuevo");
        if (saldoDestinoNuevo != null)
            transfer.setSaldoDestinoNuevo(new Monto(
                    new java.math.BigDecimal(saldoDestinoNuevo.get("cantidad").toString()),
                    (String) saldoDestinoNuevo.get("moneda")
            ));
        return transfer;
    }

    private ESBRequest buildRequest(String tipoOperacion, String token, Object body)
    {
        Header header = new Header();
        header.setIdCorrelacion(UUID.randomUUID().toString());
        header.setAplicacionOrigen("cajero-red");
        header.setTimestamp(Instant.now().toString());
        header.setToken(token);
        header.setTipoOperacion(tipoOperacion);
        header.setVersion("1.0");
        ESBRequest request = new ESBRequest();
        request.setHeader(header);
        request.setBody(body);
        return request;
    }

    private ESBResponse execute(ESBRequest request, String token)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<ESBRequest> entity = new HttpEntity<>(request, headers);
        String url = esbUrl + "/api/v1/esb/dispatch";
        ResponseEntity<ESBResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, ESBResponse.class
        );
        return response.getBody();
    }
}