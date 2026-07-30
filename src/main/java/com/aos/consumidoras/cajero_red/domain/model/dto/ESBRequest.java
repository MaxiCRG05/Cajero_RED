package com.aos.consumidoras.cajero_red.domain.model.dto;

import lombok.Data;

@Data
public class ESBRequest
{
    private Header header;
    private Object body;
}