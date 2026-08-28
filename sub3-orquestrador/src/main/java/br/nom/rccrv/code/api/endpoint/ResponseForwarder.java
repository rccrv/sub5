package br.nom.rccrv.code.api.endpoint;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public final class ResponseForwarder {

    private ResponseForwarder() {
    }

    public static Response forward(Response upstreamResponse) {
        try (upstreamResponse) {
            var contentType = upstreamResponse.getMediaType();
            var body = upstreamResponse.hasEntity() ? upstreamResponse.readEntity(String.class) : null;
            var response = Response.status(upstreamResponse.getStatus());

            if (contentType != null) {
                response.type(contentType);
            } else if (body != null) {
                response.type(MediaType.APPLICATION_JSON_TYPE);
            }

            if (body != null) {
                response.entity(body);
            }

            return response.build();
        }
    }
}
