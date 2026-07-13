package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

import java.util.Optional;

public sealed interface EditarVeiculoInteractor permits EditarVeiculoInteractorImpl {

    Optional<VeiculoEntity> editar(String placa, VeiculoEntity veiculoEntity);
}
