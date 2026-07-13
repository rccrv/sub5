package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

import java.util.List;

public sealed interface ListarVeiculosVendidosInteractor permits ListarVeiculosVendidosInteractorImpl {

    List<VeiculoEntity> listar();
}
