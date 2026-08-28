package br.nom.rccrv.code.domain.dto.rest;

public record CompradorRespDto(
    long id,
    String cpf,
    String primeiroNome,
    String ultimoNome,
    String email,
    String telefone,
    boolean autorizado
) {
}
