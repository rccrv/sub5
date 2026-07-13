package br.nom.rccrv.code.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record VeiculoReqDto(
    @NotNull
    @NotBlank
    String marca,
    @NotNull
    @NotBlank
    String modelo,
    @NotNull
    @Positive
    Integer ano,
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$")
    String placa,
    @NotNull
    @NotBlank
    String cor,
    @NotNull
    @Positive
    BigDecimal valor
) {
}
