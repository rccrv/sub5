package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;

public sealed interface CadastrarCompradorInteractor permits CadastrarCompradorInteractorImpl {

    CompradorEntity cadastrar(CompradorEntity compradorEntity);
    void rollbackCadastrar(CompradorEntity compradorEntity);
}
