package com.apiscog.prices.infrastructure.adapter.in.web;

import com.apiscog.prices.application.port.in.FindApplicablePriceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.cache.type=none")
@AutoConfigureMockMvc
class PriceControllerPersistenceErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindApplicablePriceUseCase findApplicablePriceUseCase;

    @Test
    void returnsSafeInternalServerErrorWhenPersistenceFails() throws Exception {
        given(findApplicablePriceUseCase.findApplicablePrice(any()))
                .willThrow(new DataAccessResourceFailureException("sensitive database detail"));

        mockMvc.perform(get("/api/v1/prices/current")
                        .param("applicationDate", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.instance").value("/api/v1/prices/current"));
    }
}
