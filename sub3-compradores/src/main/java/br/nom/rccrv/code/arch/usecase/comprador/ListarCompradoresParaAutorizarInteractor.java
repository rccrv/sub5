package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;

import java.util.List;

public sealed interface ListarCompradoresParaAutorizarInteractor permits ListarCompradoresParaAutorizarInteractorImpl {

    List<CompradorEntity> listar();
}
