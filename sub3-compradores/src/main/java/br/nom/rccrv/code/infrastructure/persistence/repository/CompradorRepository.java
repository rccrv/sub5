package br.nom.rccrv.code.infrastructure.persistence.repository;

import br.nom.rccrv.code.infrastructure.persistence.entity.Comprador;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.List;

@Repository
public interface CompradorRepository extends CrudRepository<Comprador, Long> {

    @Query("select c from Comprador c where c.autorizado = false")
    List<Comprador> listarCompradoresParaAutorizar();

    @Query("select c from Comprador c where c.cpf = :cpf")
    Comprador findByCpf(@Param("cpf") String cpf);
}
