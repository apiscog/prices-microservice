package com.apiscog.prices.adapter.in.web.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;

@Schema(description = "RFC 9457 problem detail with an application error code")
public record ApiProblemResponse(
        @Schema(format = "uri", example = "about:blank") URI type,
        @Schema(example = "Invalid request", requiredMode = Schema.RequiredMode.REQUIRED) String title,
        @Schema(example = "400", requiredMode = Schema.RequiredMode.REQUIRED) int status,
        @Schema(example = "One or more request parameters are invalid",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String detail,
        @Schema(format = "uri", example = "/api/v1/prices/current",
                requiredMode = Schema.RequiredMode.REQUIRED)
        URI instance,
        @Schema(example = "INVALID_REQUEST", requiredMode = Schema.RequiredMode.REQUIRED)
        ApiErrorCode code
) {
}
