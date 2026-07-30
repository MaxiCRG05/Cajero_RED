package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.Data;

@Data
public class ESBResponse
{
    private Header header;
    private Body body;

    @lombok.Data
    public static class Body
    {
        private String codigoEstatus;
        private boolean exito;
        private String mensaje;
        private Object datos;
    }
}