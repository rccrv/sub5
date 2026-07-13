package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

import java.util.Optional;

public sealed interface ComprarVeiculoInteractor permits ComprarVeiculoInteractorImpl {

    Optional<VeiculoEntity> comprar(String placa, String cpf);
}
