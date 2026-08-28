package br.nom.rccrv.code.domain.state;

import java.util.UUID;

public record PrincipalSaleContext(UUID pagamentoId, String cpf, String placa) {
}
