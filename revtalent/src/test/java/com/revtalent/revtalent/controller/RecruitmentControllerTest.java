package com.revtalent.revtalent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revtalent.revtalent.dto.recruitment.JobPostingResponse;
import com.revtalent.revtalent.dto.recruitment.JobRequest;
import com.revtalent.revtalent.model.JobPosting;
import com.revtalent.revtalent.service.RecruitmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecruitmentController.class)
class RecruitmentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private RecruitmentService service;

    private JobPostingResponse jobPostingResponse;

    @BeforeEach
    void setUp() {
        jobPostingResponse = new JobPostingResponse();
        jobPostingResponse.setId(1L);
        jobPostingResponse.setTitle("Backend Developer");
        jobPostingResponse.setDescription("Java Spring Boot");
        jobPostingResponse.setRequirements("3+ years");
        jobPostingResponse.setVacancies(2);
        jobPostingResponse.setStatus(JobPosting.Status.OPEN);
        jobPostingResponse.setDepartmentName("Engineering");
        jobPostingResponse.setPostedOn(LocalDate.now());
    }

    // ================= GET =================

    @Test
    @WithMockUser
    void getAllJobs_returns200WithList() throws Exception {

        when(service.getAllJobs()).thenReturn(List.of(jobPostingResponse));

        mockMvc.perform(get("/api/recruitment/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Backend Developer"));
    }

    @Test
    @WithMockUser
    void getAllJobs_emptyList_returns200() throws Exception {

        when(service.getAllJobs()).thenReturn(List.of());

        mockMvc.perform(get("/api/recruitment/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllJobs_noAuth_returns401() throws Exception {

        mockMvc.perform(get("/api/recruitment/jobs"))
                .andExpect(status().isUnauthorized());
    }

    // ================= CREATE =================

    @Test
    @WithMockUser(roles = "HR_ADMIN")
    void createJob_returns200WithResponse() throws Exception {

        JobRequest req = new JobRequest();
        req.setTitle("Backend Developer");
        req.setDescription("Java Spring Boot");
        req.setRequirements("3+ years");
        req.setVacancies(2);
        req.setStatus("OPEN");

        when(service.createJob(any())).thenReturn(jobPostingResponse);

        mockMvc.perform(post("/api/recruitment/jobs")
                        .with(csrf()) // ✅ IMPORTANT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Developer"));
    }


    @Test
    @WithMockUser(roles = "HR_ADMIN")
    void createJob_invalidStatus_returns400() throws Exception {

        JobRequest req = new JobRequest();
        req.setTitle("Backend Developer");
        req.setStatus("INVALID_STATUS");

        when(service.createJob(any()))
                .thenThrow(new IllegalArgumentException("Invalid status"));

        mockMvc.perform(post("/api/recruitment/jobs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest()); // ✅ FIXED
    }
}