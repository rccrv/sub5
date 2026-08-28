package br.nom.rccrv.code.arch.usecase.veiculo;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;
import br.nom.rccrv.code.arch.port.repository.VeiculoRepositoryPort;
import java.util.Optional;
import java.util.UUID;

public final class ComprarVeiculoInteractorImpl implements ComprarVeiculoInteractor {

  private final VeiculoRepositoryPort repository;

  private ComprarVeiculoInteractorImpl(VeiculoRepositoryPort repository) {
    this.repository = repository;
  }

  public static ComprarVeiculoInteractor factory(VeiculoRepositoryPort repository) {
    return new ComprarVeiculoInteractorImpl(repository);
  }

  @Override
  public Optional<VeiculoEntity> comprar(String placa, String cpf) {
    return comprar(placa, cpf, null);
  }

  @Override
  public Optional<VeiculoEntity> comprar(String placa, String cpf, UUID pagamentoId) {
    if (!repository.sellIfAvailable(placa, cpf, pagamentoId)) {
      return Optional.empty();
    }

    return repository.findByPlaca(placa);
  }

  @Override
  public boolean rollback(String placa, UUID pagamentoId) {
    return repository.rollbackSale(placa, pagamentoId);
  }
}
