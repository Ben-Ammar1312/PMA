package com.example.backend.controller;


import com.example.backend.service.UserAuthService;
import com.example.backend.service.UserRegistrationService;
import com.example.backend.service.FertilityRecordService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean private UserRegistrationService registrationService;
    @MockitoBean private FertilityRecordService fertilityRecordService;
    @MockitoBean private UserAuthService authService;



    @Test
    @Disabled()
    void login_success_returnsToken() throws Exception {
        AccessTokenResponse token = new AccessTokenResponse();
        token.setToken("tkn");
        token.setExpiresIn(60);
        token.setRefreshToken("rtk");
        token.setRefreshExpiresIn(120);
        token.setTokenType("Bearer");

        given(authService.login("alice", "secret")).willReturn(token);

        String json = "{\"username\":\"alice\",\"password\":\"secret\"}";

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("tkn"))
                .andExpect(jsonPath("$.refreshToken").value("rtk"));
    }

    @Test
    @Disabled()
    void login_invalidCredentials_returns401() throws Exception {
        given(authService.login("bob", "bad")).willThrow(new RuntimeException());

        String json = "{\"username\":\"bob\",\"password\":\"bad\"}";

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }
}
