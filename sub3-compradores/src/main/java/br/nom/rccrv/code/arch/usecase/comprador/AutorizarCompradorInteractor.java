package br.nom.rccrv.code.arch.usecase.comprador;

import br.nom.rccrv.code.arch.entity.CompradorEntity;

import java.util.Optional;

public sealed interface AutorizarCompradorInteractor permits AutorizarCompradorInteractorImpl {

    Optional<CompradorEntity> autorizar(String cpf);
}
