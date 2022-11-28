package com.example.rewardcalculator.controller;

import com.example.rewardcalculator.service.RewardCalculationService;
import com.example.rewardcalculator.validator.ValidationContext;
import com.example.rewardcalculator.validator.exception.EmptyTransactionListException;
import com.example.rewardcalculator.validator.exception.InvalidTransactionsOnListException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RewardWebServiceTest {
    private static final String TEST_MESSAGE = "TEST";
    private static final String TEST_FIELD = "FIELD";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardCalculationService service;

    @Test
    public void shouldGetStatus200() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/calculate-reward")
                        .content(String.valueOf((Lists.newArrayList())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    public void shouldFailOnValidationContextAndGetErrorMessage() throws Exception {
        final ValidationContext validationContext = new ValidationContext();
        validationContext.addValidationError(new ValidationContext.ValidationError(TEST_MESSAGE, TEST_FIELD));
        final InvalidTransactionsOnListException ex = new InvalidTransactionsOnListException(validationContext);
        when(service.processRewardCalculation(Lists.newArrayList())).thenThrow(ex);
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/calculate-reward")
                        .content(String.valueOf((Lists.newArrayList())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("[" + TEST_MESSAGE + "]"));
    }

    @Test
    public void shouldReturnStatus400OnEmptyList() throws Exception {
        when(service.processRewardCalculation(Lists.newArrayList())).thenThrow(EmptyTransactionListException.class);
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/calculate-reward")
                        .content(String.valueOf((Lists.newArrayList())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400 BAD_REQUEST"));
    }
}