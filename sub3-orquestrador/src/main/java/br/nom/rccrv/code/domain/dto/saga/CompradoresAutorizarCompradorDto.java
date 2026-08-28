package br.nom.rccrv.code.domain.dto.saga;

import br.nom.rccrv.code.domain.enums.StepType;

import java.util.UUID;

public record CompradoresAutorizarCompradorDto(
    String cpf,
    UUID transactionId,
    StepType stepType,
    String type
) implements SagaDtoInterface {

    public CompradoresAutorizarCompradorDto(String cpf, UUID transactionId, StepType stepType) {
        this(cpf, transactionId, stepType, "CompradoresAutorizarCompradorDto");
    }

    public CompradoresAutorizarCompradorDto withRollBack() {
        return new CompradoresAutorizarCompradorDto(
            this.cpf,
            this.transactionId,
            StepType.ROLLBACK,
            "CompradoresAutorizarCompradorDto"
        );
    }
}
