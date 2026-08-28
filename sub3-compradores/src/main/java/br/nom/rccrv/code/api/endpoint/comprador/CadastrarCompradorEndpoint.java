package br.nom.rccrv.code.api.endpoint.comprador;

import br.nom.rccrv.code.arch.controller.CompradorController;
import br.nom.rccrv.code.domain.dto.CompradorReqDto;
import br.nom.rccrv.code.domain.dto.CompradorRespDto;
import br.nom.rccrv.code.domain.mapper.CompradorInputMapper;
import br.nom.rccrv.code.domain.mapper.CompradorOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.adapter.CompradorRepositoryAdapter;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;

@RequestScoped
@Path("/cadastrar")
public class CadastrarCompradorEndpoint {

    CompradorRepositoryAdapter compradorRepository;
    CompradorController controller;

    @Inject
    public CadastrarCompradorEndpoint(CompradorRepositoryAdapter compradorRepository) {
        this.compradorRepository = compradorRepository;
        this.controller = new CompradorController(compradorRepository);
    }

    @POST
    @Transactional
    @RunOnVirtualThread
    public RestResponse<CompradorRespDto> cadastrar(@Valid CompradorReqDto req) {
        var resp = CompradorOutputMapper.paraRespDto(
            controller.cadastrar(CompradorInputMapper.deReqDto(req))
        );

        return RestResponse.status(Response.Status.CREATED, resp);
    }
}
