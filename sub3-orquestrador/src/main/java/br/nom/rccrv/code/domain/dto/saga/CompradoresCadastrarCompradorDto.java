package br.nom.rccrv.code.domain.dto.saga;

import br.nom.rccrv.code.domain.dto.rest.CompradorReqDto;
import br.nom.rccrv.code.domain.enums.StepType;

import java.util.UUID;

public record CompradoresCadastrarCompradorDto(
    CompradorReqDto comprador,
    UUID transactionId,
    StepType stepType,
    String type
) implements SagaDtoInterface {

    public CompradoresCadastrarCompradorDto(CompradorReqDto comprador, UUID transactionId, StepType stepType) {
        this(comprador, transactionId, stepType, "CompradoresCadastrarCompradorDto");
    }

    public CompradoresCadastrarCompradorDto withRollBack() {
        return new CompradoresCadastrarCompradorDto(
            this.comprador,
            this.transactionId,
            StepType.ROLLBACK,
            "CompradoresCadastrarCompradorDto"
        );
    }
}
