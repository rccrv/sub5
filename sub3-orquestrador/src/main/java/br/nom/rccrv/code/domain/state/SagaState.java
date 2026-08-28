package br.nom.rccrv.code.domain.state;

import br.nom.rccrv.code.domain.dto.saga.SagaDtoInterface;
import br.nom.rccrv.code.domain.enums.StepStatus;

public record SagaState(
    StepStatus compradorStatus,
    SagaDtoInterface compradorPayload,
    StepStatus financeiroStatus,
    SagaDtoInterface financeiroPayload,
    StepStatus principalStatus,
    SagaDtoInterface principalPayload
) {

    public boolean completou() {
        boolean compradorCompleted = compradorStatus == null || compradorStatus != StepStatus.PENDING;
        boolean financeiroCompleted = financeiroStatus == null || financeiroStatus != StepStatus.PENDING;
        boolean principalCompleted = principalStatus == null || principalStatus != StepStatus.PENDING;

        return compradorCompleted && financeiroCompleted && principalCompleted;
    }

    public boolean falhou() {
        return (
            (compradorStatus != null && compradorStatus == StepStatus.FAILED)
            ||
            (financeiroStatus != null && financeiroStatus == StepStatus.FAILED)
            ||
            (principalStatus != null && principalStatus == StepStatus.FAILED)
        );
    }
}
