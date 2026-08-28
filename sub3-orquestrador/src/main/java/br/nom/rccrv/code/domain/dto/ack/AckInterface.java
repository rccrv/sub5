package br.nom.rccrv.code.domain.dto.ack;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SagaAck.class, name = "SagaAck")
})
public sealed interface AckInterface permits SagaAck {

    String type();
}
