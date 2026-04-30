package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.recruitment.JobPostingResponse;
import com.revtalent.revtalent.dto.recruitment.JobRequest;
import com.revtalent.revtalent.model.*;
import com.revtalent.revtalent.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {

    @Mock private JobPostingRepository repo;

    @InjectMocks private RecruitmentService recruitmentService;

    private JobPosting jobPosting;

    @BeforeEach
    void setUp() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        jobPosting = JobPosting.builder()
                .id(1L)
                .title("Backend Developer")
                .description("Java Spring Boot")
                .requirements("3+ years")
                .vacancies(2)
                .status(JobPosting.Status.OPEN)
                .department(department)
                .postedOn(LocalDate.now())
                .build();
    }

    @Test
    void getAllJobs_returnsResponseList() {
        when(repo.findAll()).thenReturn(List.of(jobPosting));

        List<JobPostingResponse> result = recruitmentService.getAllJobs();

        assertEquals(1, result.size());
        assertEquals("Backend Developer", result.get(0).getTitle());
        assertEquals("Engineering", result.get(0).getDepartmentName());
    }

    @Test
    void getAllJobs_emptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<JobPostingResponse> result = recruitmentService.getAllJobs();

        assertTrue(result.isEmpty());
    }

    @Test
    void createJob_success() {
        JobRequest req = new JobRequest();
        req.setTitle("Backend Developer");
        req.setDescription("Java Spring Boot");
        req.setRequirements("3+ years");
        req.setVacancies(2);
        req.setStatus("OPEN");

        when(repo.save(any())).thenReturn(jobPosting);

        JobPostingResponse result = recruitmentService.createJob(req);

        assertNotNull(result);
        assertEquals("Backend Developer", result.getTitle());
        assertEquals(JobPosting.Status.OPEN, result.getStatus());
        verify(repo).save(any());
    }

    @Test
    void createJob_invalidStatus_throwsException() {
        JobRequest req = new JobRequest();
        req.setTitle("Backend Developer");
        req.setStatus("INVALID_STATUS");

        assertThrows(IllegalArgumentException.class, () -> recruitmentService.createJob(req));
    }
}