package br.nom.rccrv.code.arch.controller;

import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoInputAdapter;
import br.nom.rccrv.code.arch.adapter.veiculo.VeiculoOutputAdapter;
import br.nom.rccrv.code.arch.usecase.veiculo.*;
import br.nom.rccrv.code.domain.dto.VeiculoReqDto;
import br.nom.rccrv.code.domain.dto.VeiculoRespDto;
import br.nom.rccrv.code.infrastructure.persistence.repository.VeiculoRepository;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

public class VeiculoController {

    VeiculoRepository veiculoRepository;

    public VeiculoController(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public VeiculoRespDto cadastrar(VeiculoReqDto req) {
        var interactor = CadastrarVeiculoInteractorImpl.factory(veiculoRepository);
        var veiculoEntity = VeiculoInputAdapter.deReqDto(req);

        return VeiculoOutputAdapter.paraRespDto(interactor.cadastrar(veiculoEntity));
    }

    public VeiculoRespDto atualizar(
            String placa,
            VeiculoReqDto req
    ) {
        var interactor = EditarVeiculoInteractorImpl.factory(veiculoRepository);

        return VeiculoOutputAdapter.paraRespDto(
            interactor.editar(placa, VeiculoInputAdapter.deReqDto(req))
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado"))
        );
    }

    public List<VeiculoRespDto> listarVeiculosAVenda() {
        var interactor = ListarVeiculosAVendaInteractorImpl.factory(veiculoRepository);

        return interactor.listar().stream().map(VeiculoOutputAdapter::paraRespDto).toList();
    }

    public List<VeiculoRespDto> listarVeiculosVendidos() {
        var interactor = ListarVeiculosVendidosInteractorImpl.factory(veiculoRepository);

        return interactor.listar().stream().map(VeiculoOutputAdapter::paraRespDto).toList();
    }

    public VeiculoRespDto comprar(String placa, String cpf) {
        var interactor = ComprarVeiculoInteractorImpl.factory(veiculoRepository);

        return VeiculoOutputAdapter.paraRespDto(
            interactor.comprar(placa, cpf).orElseThrow(() -> new NotFoundException("Veículo não encontrado"))
        );
    }
}
