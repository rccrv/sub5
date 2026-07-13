package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

import java.util.List;

public sealed interface ListarVeiculosAVendaInteractor permits ListarVeiculosAVendaInteractorImpl {

    List<VeiculoEntity> listar();
}
