package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

import java.util.Optional;
import java.util.UUID;

public sealed interface ComprarVeiculoInteractor permits ComprarVeiculoInteractorImpl {

    Optional<VeiculoEntity> comprar(String placa, String cpf);
    Optional<VeiculoEntity> comprar(String placa, String cpf, UUID pagamentoId);
    boolean rollback(String placa, UUID pagamentoId);
}
