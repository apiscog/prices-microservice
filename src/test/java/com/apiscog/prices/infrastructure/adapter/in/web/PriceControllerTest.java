package com.apiscog.prices.infrastructure.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("applicablePriceScenarios")
    void returnsTheApplicablePrice(
            String applicationDate,
            long expectedPriceList,
            double expectedPrice
    ) throws Exception {
        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", applicationDate)
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void returnsAllResponseFields() throws Exception {
        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void documentsTheEndpointInOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters.length()").value(3))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[0].name")
                        .value("applicationDate"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[0].required").value(true))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[0].schema.type")
                        .value("string"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[0].schema.format")
                        .doesNotExist())
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[0].schema.pattern")
                        .value("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[1].name")
                        .value("productId"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[1].required").value(true))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[1].schema.type")
                        .value("integer"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[1].schema.format")
                        .value("int64"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[1].schema.minimum")
                        .value(1))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[2].name")
                        .value("brandId"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[2].required").value(true))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[2].schema.type")
                        .value("integer"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[2].schema.format")
                        .value("int64"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.parameters[2].schema.minimum")
                        .value(1))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['500']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['200']"
                                + ".content['application/json'].schema['$ref']")
                        .value("#/components/schemas/PriceResponse"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['400']"
                                + ".content['application/problem+json'].schema['$ref']")
                        .value("#/components/schemas/ApiProblemResponse"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['404']"
                                + ".content['application/problem+json'].schema['$ref']")
                        .value("#/components/schemas/ApiProblemResponse"))
                .andExpect(jsonPath("$.paths['/api/v1/prices/current'].get.responses['500']"
                                + ".content['application/problem+json'].schema['$ref']")
                        .value("#/components/schemas/ApiProblemResponse"))
                .andExpect(jsonPath("$.components.schemas.ApiProblemResponse.properties.code").exists())
                .andExpect(jsonPath("$.components.schemas.ApiProblemResponse.properties.code.type")
                        .value("string"))
                .andExpect(jsonPath("$.components.schemas.ApiProblemResponse.properties.code.enum",
                        containsInAnyOrder("PRICE_NOT_FOUND", "INVALID_REQUEST", "INTERNAL_ERROR")))
                .andExpect(jsonPath("$.components.schemas.ApiProblemResponse.required",
                        containsInAnyOrder("title", "status", "detail", "instance", "code")))
                .andExpect(jsonPath("$.components.schemas.PriceResponse.required",
                        containsInAnyOrder(
                                "productId",
                                "brandId",
                                "priceList",
                                "startDate",
                                "endDate",
                                "price",
                                "currency"
                        )));
    }

    @Test
    void returnsNotFoundWhenNoPriceApplies() throws Exception {
        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", "2021-01-01T00:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Price not found"))
                .andExpect(jsonPath("$.code").value("PRICE_NOT_FOUND"))
                .andExpect(jsonPath("$.instance").value("/api/v1/prices/current"));
    }

    @Test
    void returnsBadRequestForMalformedDate() throws Exception {
        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", "not-a-date")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @ParameterizedTest
    @MethodSource("unsupportedDateFormats")
    void rejectsDatesThatAreNotLocalDateTimesWithSecondPrecision(String applicationDate) throws Exception {
        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", applicationDate)
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @ParameterizedTest
    @MethodSource("missingParameterScenarios")
    void returnsBadRequestForMissingParameter(String omittedParameter) throws Exception {
        var request = get("/api/v1/prices/current");
        if (!"applicationDate".equals(omittedParameter)) {
            request.param("applicationDate", "2020-06-14T16:00:00");
        }
        if (!"productId".equals(omittedParameter)) {
            request.param("productId", "35455");
        }
        if (!"brandId".equals(omittedParameter)) {
            request.param("brandId", "1");
        }

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @ParameterizedTest
    @MethodSource("nonPositiveIdentifierScenarios")
    void returnsBadRequestForNonPositiveIdentifier(String parameter, String value) throws Exception {
        String productId = "productId".equals(parameter) ? value : "35455";
        String brandId = "brandId".equals(parameter) ? value : "1";

        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", productId)
                        .param("brandId", brandId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    private static Stream<Arguments> applicablePriceScenarios() {
        return Stream.of(
                Arguments.of("2020-06-14T10:00:00", 1, 35.50),
                Arguments.of("2020-06-14T16:00:00", 2, 25.45),
                Arguments.of("2020-06-14T21:00:00", 1, 35.50),
                Arguments.of("2020-06-15T10:00:00", 3, 30.50),
                Arguments.of("2020-06-16T21:00:00", 4, 38.95)
        );
    }

    private static Stream<String> missingParameterScenarios() {
        return Stream.of("applicationDate", "productId", "brandId");
    }

    private static Stream<Arguments> nonPositiveIdentifierScenarios() {
        return Stream.of(
                Arguments.of("productId", "0"),
                Arguments.of("productId", "-1"),
                Arguments.of("brandId", "0"),
                Arguments.of("brandId", "-1")
        );
    }

    private static Stream<String> unsupportedDateFormats() {
        return Stream.of(
                "2020-06-14T16:00",
                "2020-06-14T16:00:00.000",
                "2020-06-14T16:00:00Z",
                "2020-06-14T16:00:00+02:00",
                "2020-06-14 16:00:00",
                "2020-02-30T16:00:00"
        );
    }
}
