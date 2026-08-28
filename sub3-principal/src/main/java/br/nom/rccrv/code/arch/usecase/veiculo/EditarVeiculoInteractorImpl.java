package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;

import java.util.Optional;

final public class EditarVeiculoInteractorImpl implements EditarVeiculoInteractor {

    VeiculoRepositoryPort veiculoRepository;

    private EditarVeiculoInteractorImpl() {}

    public static EditarVeiculoInteractor factory(VeiculoRepositoryPort veiculoRepository) {
        var interactor = new EditarVeiculoInteractorImpl();

        interactor.veiculoRepository = veiculoRepository;

        return interactor;
    }

    public Optional<VeiculoEntity> editar(String placa, VeiculoEntity veiculoEntity) {
        var veiculoOpt = veiculoRepository.findByPlaca(placa);

        if (veiculoOpt.isEmpty()) {
            return Optional.empty();
        }

        var veiculo = veiculoOpt.get();

        veiculo.setMarca(veiculoEntity.getMarca());
        veiculo.setModelo(veiculoEntity.getModelo());
        veiculo.setAno(veiculoEntity.getAno());
        veiculo.setCor(veiculoEntity.getCor());
        veiculo.setValor(veiculoEntity.getValor());
        veiculo.setCompradorCpf(veiculoEntity.getCompradorCpf());
        veiculo.setVendido(veiculoEntity.getVendido());

        return Optional.of(veiculoRepository.save(veiculo));
    }
}
