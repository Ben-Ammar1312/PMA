package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.model.FertilityRecordDetails;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.UserAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean
    private FertilityRecordService fertilityRecordService;
    @MockitoBean private UserAuthService userAuthService;


    @Test
    void getPatients_returns200AndDelegatesToService() throws Exception {
        given(fertilityRecordService.getAllRecords()).willReturn(List.of(new FertilityRecord()));

        mockMvc.perform(get("/doctor/patients")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Doctor"))))
                .andExpect(status().isOk());

        verify(fertilityRecordService).getAllRecords();
    }

    @Test
    void getPatientRecord_returns200AndDelegatesToService() throws Exception {
        // Arrange: mock the actual method used in controller
        given(fertilityRecordService.getFullFertilityRecord("42"))
                .willReturn(FertilityRecordDetails.builder().build());

        // Act & Assert: perform the request
        mockMvc.perform(get("/doctor/patient/{id}", "42")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Doctor"))))
                .andExpect(status().isOk());

        // Verify: correct method was called
        verify(fertilityRecordService).getFullFertilityRecord("42");
    }

}
