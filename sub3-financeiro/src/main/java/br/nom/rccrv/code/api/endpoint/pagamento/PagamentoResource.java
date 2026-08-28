package br.nom.rccrv.code.api.endpoint.pagamento;

import br.nom.rccrv.code.api.kafka.OrquestradorEmitter;
import br.nom.rccrv.code.arch.controller.PagamentoController;
import br.nom.rccrv.code.domain.dto.ack.SagaAck;
import br.nom.rccrv.code.domain.dto.pagamento.PagamentoRespDto;
import br.nom.rccrv.code.domain.dto.pagamento.PagarPagamentoReqDto;
import br.nom.rccrv.code.domain.dto.pagamento.ReservaPagamentoReqDto;
import br.nom.rccrv.code.domain.mapper.PagamentoInputMapper;
import br.nom.rccrv.code.domain.mapper.PagamentoOutputMapper;
import br.nom.rccrv.code.infrastructure.persistence.adapter.PagamentoRepositoryAdapter;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/** Internal API. The BFF supplies the authenticated buyer CPF. */
@Path("/internal/pagamentos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PagamentoResource {

  private final PagamentoController controller;
  private final OrquestradorEmitter orquestradorEmitter;
  private final Clock clock;

  @Inject
  public PagamentoResource(
      PagamentoRepositoryAdapter repository,
      OrquestradorEmitter orquestradorEmitter,
      Clock clock,
      @ConfigProperty(name = "financeiro.payment.timeout") Duration timeout) {
    controller = new PagamentoController(repository, timeout);
    this.orquestradorEmitter = orquestradorEmitter;
    this.clock = clock;
  }

  @POST
  @Path("/reservas")
  @Transactional
  public Response reserva(@Valid ReservaPagamentoReqDto request) {
    var pagamento = controller.reservar(PagamentoInputMapper.deReqDto(request), now());

    return Response.status(Response.Status.CREATED)
        .entity(PagamentoOutputMapper.paraRespDto(pagamento))
        .build();
  }

  @POST
  @Path("/{id}/pagar")
  @Transactional
  public Response pagar(
      @PathParam("id") UUID id,
      @HeaderParam("X-CPF") String cpf,
      @Valid PagarPagamentoReqDto request
  ) {
    var pagamento = controller.pagar(id, cpf, request.pixCode(), now());

    pagamento.ifPresentOrElse(
        value -> orquestradorEmitter.send(
            new SagaAck(id, SagaAck.SagaStatus.SUCCESS, value.getCpf(), value.getPlaca())),
        () -> orquestradorEmitter.send(new SagaAck(id, SagaAck.SagaStatus.FAILED, null, null)));

    return pagamento
        .map(PagamentoOutputMapper::paraRespDto)
        .map(value -> Response.accepted(value).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @GET
  @Path("/{id}")
  public PagamentoRespDto get(@PathParam("id") UUID id, @HeaderParam("X-CPF") String cpf) {
    return controller
        .consultar(id, cpf)
        .map(PagamentoOutputMapper::paraRespDto)
        .orElseThrow(NotFoundException::new);
  }

  private LocalDateTime now() {
    return LocalDateTime.now(clock);
  }
}
