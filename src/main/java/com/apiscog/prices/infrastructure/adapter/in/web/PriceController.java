package com.apiscog.prices.infrastructure.adapter.in.web;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.in.FindApplicablePriceUseCase;
import com.apiscog.prices.infrastructure.adapter.in.web.error.ApiProblemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "/api/v1/prices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Prices", description = "Price lookup operations")
public final class PriceController {

    private static final String LOCAL_DATE_TIME_REGEX =
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";

    private final FindApplicablePriceUseCase findApplicablePriceUseCase;
    private final PriceWebMapper mapper;

    public PriceController(FindApplicablePriceUseCase findApplicablePriceUseCase, PriceWebMapper mapper) {
        this.findApplicablePriceUseCase = findApplicablePriceUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/current")
    @Operation(summary = "Find the applicable price")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Applicable price found",
                    content = @Content(schema = @Schema(implementation = PriceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "No applicable price found",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class)))
    })
    public PriceResponse findCurrentPrice(
            @Parameter(description = "Application date in yyyy-MM-dd'T'HH:mm:ss format, without offset",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            pattern = LOCAL_DATE_TIME_REGEX,
                            example = "2020-06-14T16:00:00"
                    ))
            @RequestParam(name = "applicationDate")
            @Pattern(regexp = LOCAL_DATE_TIME_REGEX)
            String applicationDate,
            @Parameter(description = "Product identifier", example = "35455", required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @RequestParam(name = "productId") @Positive long productId,
            @Parameter(description = "Brand identifier", example = "1", required = true,
                    schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @RequestParam(name = "brandId") @Positive long brandId
    ) {
        LocalDateTime parsedApplicationDate =
                LocalDateTime.parse(applicationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        FindApplicablePriceQuery query =
                new FindApplicablePriceQuery(parsedApplicationDate, productId, brandId);
        return mapper.toResponse(findApplicablePriceUseCase.findApplicablePrice(query));
    }
}
