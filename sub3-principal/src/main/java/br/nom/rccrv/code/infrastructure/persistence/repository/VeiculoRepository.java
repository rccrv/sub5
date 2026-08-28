package br.nom.rccrv.code.infrastructure.persistence.repository;

import br.nom.rccrv.code.infrastructure.persistence.entity.Veiculo;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VeiculoRepository extends CrudRepository<Veiculo, Long> {

    @Query("select v from Veiculo v where v.placa = :placa")
    Veiculo findByPlaca(@Param("placa") String placa);

    @Query("select v from Veiculo v where v.vendido = false order by v.valor asc")
    List<Veiculo> listarVeiculosAVenda();

    @Query("select v from Veiculo v where v.vendido = true order by v.valor asc")
    List<Veiculo> listarVeiculosVendidos();

    @Query("update Veiculo v set v.vendido = true, v.compradorCpf = :cpf, v.pagamentoId = :pagamentoId where v.placa = :placa and v.vendido = false")
    long sellIfAvailable(
        @Param("placa") String placa,
        @Param("cpf") String cpf,
        @Param("pagamentoId") UUID pagamentoId
    );

    @Query("update Veiculo v set v.vendido = false, v.compradorCpf = null, v.pagamentoId = null where v.placa = :placa and v.pagamentoId = :pagamentoId")
    long rollbackSale(
        @Param("placa") String placa,
        @Param("pagamentoId") UUID pagamentoId
    );
}
