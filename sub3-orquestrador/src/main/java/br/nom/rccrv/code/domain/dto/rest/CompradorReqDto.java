package br.nom.rccrv.code.domain.dto.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CompradorReqDto(
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$")
    String cpf,
    @NotNull
    @NotBlank
    String primeiroNome,
    @NotNull
    @NotBlank
    String ultimoNome,
    @NotNull
    @NotBlank
    String email,
    @NotNull
    @NotBlank
    String telefone
) {
    // NOTE: Valida CPF e identifica usuário apenas com digitos do CPF.
    public CompradorReqDto {
        cpf = normalizarCpf(cpf);
        validarCpf(cpf);
    }

    private static String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }

        return cpf.replaceAll("\\D", "");
    }

    private static void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return;
        }

        var cpfValido = cpfValido(cpf.chars().mapToObj(c -> c - '0').toList());

        if (!cpfValido) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    public static boolean cpfValido(List<Integer> digitos) {
        if (digitos.size() != 11) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += digitos.get(i) * (10 - i);
        }

        int resto = soma % 11;
        int digito1 = resto < 2 ? 0 : 11 - resto;
        if (digito1 != digitos.get(9)) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += digitos.get(i) * (11 - i);
        }
        resto = soma % 11;
        int digito2 = resto < 2 ? 0 : 11 - resto;
        return digito2 == digitos.get(10);
    }
}
