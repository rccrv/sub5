package br.nom.rccrv.code.domain.dto;

import java.math.BigDecimal;

public record VeiculoRespDto(
    long id,
    String marca,
    String modelo,
    int ano,
    String placa,
    String cor,
    BigDecimal valor,
    String compradorCpf,
    boolean vendido
) {
}
