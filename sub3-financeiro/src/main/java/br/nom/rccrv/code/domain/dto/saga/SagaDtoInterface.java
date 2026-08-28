package br.nom.rccrv.code.domain.dto.saga;

import br.nom.rccrv.code.domain.enums.StepType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CancelaPagamentoDto.class, name = "CancelaPagamentoDto")
})
public sealed interface SagaDtoInterface permits CancelaPagamentoDto {

  SagaDtoInterface withRollBack();

  StepType stepType();

  String type();
}
