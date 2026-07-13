package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

public sealed interface CadastrarVeiculoInteractor permits CadastrarVeiculoInteractorImpl {

    VeiculoEntity cadastrar(VeiculoEntity veiculoEntity);
}
