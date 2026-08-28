package br.nom.rccrv.code.arch.port.repository;

import br.nom.rccrv.code.arch.entity.VeiculoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepositoryPort {

    VeiculoEntity save(VeiculoEntity veiculo);
    Optional<VeiculoEntity> findByPlaca(String placa);
    List<VeiculoEntity> findAvailable();
    List<VeiculoEntity> findSold();
    boolean sellIfAvailable(String placa, String cpf, UUID pagamentoId);
    boolean rollbackSale(String placa, UUID pagamentoId);
}
