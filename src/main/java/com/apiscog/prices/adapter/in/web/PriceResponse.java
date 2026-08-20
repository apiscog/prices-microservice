package com.apiscog.prices.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Applicable price for a product and brand at the requested date")
public record PriceResponse(
        @Schema(example = "35455", requiredMode = Schema.RequiredMode.REQUIRED) long productId,
        @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED) long brandId,
        @Schema(example = "2", requiredMode = Schema.RequiredMode.REQUIRED) long priceList,
        @Schema(example = "2020-06-14T15:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startDate,
        @Schema(example = "2020-06-14T18:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endDate,
        @Schema(example = "25.45", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal price,
        @Schema(example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED) String currency
) {
}
