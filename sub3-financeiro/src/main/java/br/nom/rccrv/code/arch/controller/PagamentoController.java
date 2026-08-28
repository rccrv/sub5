package br.nom.rccrv.code.arch.controller;

import br.nom.rccrv.code.arch.entity.PagamentoEntity;
import br.nom.rccrv.code.arch.port.repository.PagamentoRepositoryPort;
import br.nom.rccrv.code.arch.usecase.pagamento.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class PagamentoController {
    private final PagamentoRepositoryPort repository;
    private final Duration timeout;

    public PagamentoController(PagamentoRepositoryPort repository, Duration timeout) {
        this.repository = repository;
        this.timeout = timeout;
    }

    public PagamentoEntity reservar(PagamentoEntity pagamento, LocalDateTime now) {
        var interactor = ReservarPagamentoInteractorImpl.factory(repository, timeout);

        return interactor.reservar(
            pagamento.getCpf(),
            pagamento.getPlaca(),
            pagamento.getEndereco(),
            pagamento.getCep(),
            pagamento.getQuotedAmount(),
            now
        );
    }

    public Optional<PagamentoEntity> pagar(UUID id, String cpf, String pix, LocalDateTime now) {
        var interactor = ProcessarPagamentoInteractorImpl.factory(repository);

        return interactor.processar(id, cpf, pix, now);
    }

    public Optional<PagamentoEntity> consultar(UUID id, String cpf) {
        var interactor = ConsultarPagamentoInteractorImpl.factory(repository);

        return interactor.consultar(id, cpf);
    }

    public boolean settle(UUID id, BigDecimal amount, LocalDateTime now) {
        var interactor = LiquidarPagamentoInteractorImpl.factory(repository);

        return interactor.liquidar(id, amount, now);
    }

    public boolean cancel(UUID id, LocalDateTime now) {
        var interactor = CancelarPagamentoInteractorImpl.factory(repository);

        return interactor.cancelar(id, now);
    }
}
