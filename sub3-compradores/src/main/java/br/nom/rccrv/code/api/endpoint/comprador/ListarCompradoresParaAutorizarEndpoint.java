package br.nom.rccrv.code.api.endpoint.comprador;

import br.nom.rccrv.code.arch.controller.CompradorController;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.domain.mapper.CompradorOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.adapter.CompradorRepositoryAdapter;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@RequestScoped
@Path("/listar")
public class ListarCompradoresParaAutorizarEndpoint {

    CompradorRepositoryAdapter compradorRepository;
    CompradorController controller;

    @Inject
    public ListarCompradoresParaAutorizarEndpoint(CompradorRepositoryAdapter compradorRepository) {
        this.compradorRepository = compradorRepository;
        this.controller = new CompradorController(compradorRepository);
    }

    @GET
    @RunOnVirtualThread
    public RestResponse<List<CompradorRespDto>> listar() {
        var resp = controller.listar().stream()
            .map(CompradorOutputMapper::paraRespDto)
            .toList();

        return RestResponse.ok(resp);
    }
}
