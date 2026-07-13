package br.nom.rccrv.code.domain.dto;

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
