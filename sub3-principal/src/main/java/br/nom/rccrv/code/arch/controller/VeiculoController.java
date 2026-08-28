package br.nom.rccrv.code.arch.controller;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;
import br.nom.rccrv.code.arch.usecase.veiculo.CadastrarVeiculoInteractorImpl;
import br.nom.rccrv.code.arch.usecase.veiculo.ComprarVeiculoInteractorImpl;
import br.nom.rccrv.code.arch.usecase.veiculo.EditarVeiculoInteractorImpl;
import br.nom.rccrv.code.arch.usecase.veiculo.ListarVeiculosAVendaInteractorImpl;
import br.nom.rccrv.code.arch.usecase.veiculo.ListarVeiculosVendidosInteractorImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VeiculoController {

    private final VeiculoRepositoryPort veiculoRepositoryPort;

    public VeiculoController(VeiculoRepositoryPort veiculoRepositoryPort) {
        this.veiculoRepositoryPort = veiculoRepositoryPort;
    }

    public VeiculoEntity cadastrar(VeiculoEntity veiculoEntity) {
        var interactor = CadastrarVeiculoInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.cadastrar(veiculoEntity);
    }

    public Optional<VeiculoEntity> atualizar(String placa, VeiculoEntity veiculoEntity) {
        var interactor = EditarVeiculoInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.editar(placa, veiculoEntity);
    }

    public List<VeiculoEntity> listarVeiculosAVenda() {
        var interactor = ListarVeiculosAVendaInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.listar();
    }

    public List<VeiculoEntity> listarVeiculosVendidos() {
        var interactor = ListarVeiculosVendidosInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.listar();
    }

    public Optional<VeiculoEntity> comprar(String placa, String cpf) {
        var interactor = ComprarVeiculoInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.comprar(placa, cpf);
    }

    public Optional<VeiculoEntity> comprar(String placa, String cpf, UUID pagamentoId) {
        var interactor = ComprarVeiculoInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.comprar(placa, cpf, pagamentoId);
    }

    public boolean rollbackCompra(String placa, UUID pagamentoId) {
        var interactor = ComprarVeiculoInteractorImpl.factory(veiculoRepositoryPort);

        return interactor.rollback(placa, pagamentoId);
    }
}
