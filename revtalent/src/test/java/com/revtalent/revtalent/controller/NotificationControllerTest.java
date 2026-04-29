package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.exception.GlobalExceptionHandler;
import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(GlobalExceptionHandler.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private EmployeeRepository employeeRepository;

    // 1
    @Test
    @WithMockUser
    void getAll_success_returns200() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        n.setMessage("Leave approved");
        n.setType(Notification.Type.LEAVE);
        n.setRead(false);

        when(notificationService.getNotifications(1L)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/notifications/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("Leave approved"));
    }

    // 2
    @Test
    @WithMockUser
    void getAll_whenEmployeeNotFound_returns404() throws Exception {
        when(notificationService.getNotifications(99L))
                .thenThrow(new RuntimeException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/notifications/employee/99"))
                .andExpect(status().isNotFound());
    }

    // 3
    @Test
    @WithMockUser
    void getAll_returnsEmptyList_returns200() throws Exception {
        when(notificationService.getNotifications(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // 4
    @Test
    @WithMockUser
    void getUnread_success_returns200() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        n.setRead(false);

        when(notificationService.getUnreadNotifications(1L)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/notifications/employee/1/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(false));
    }

    // 5
    @Test
    @WithMockUser
    void getUnread_whenEmployeeNotFound_returns404() throws Exception {
        when(notificationService.getUnreadNotifications(99L))
                .thenThrow(new RuntimeException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/notifications/employee/99/unread"))
                .andExpect(status().isNotFound());
    }

    // 6
    @Test
    @WithMockUser
    void getUnreadCount_success_returns200() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(5);

        mockMvc.perform(get("/api/notifications/employee/1/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    // 7
    @Test
    @WithMockUser
    void getUnreadCount_returnsZero_returns200() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(0);

        mockMvc.perform(get("/api/notifications/employee/1/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    // 8
    @Test
    @WithMockUser
    void markRead_success_returns200() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        n.setRead(true);

        when(notificationService.markAsRead(1L)).thenReturn(n);

        mockMvc.perform(put("/api/notifications/1/read")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    // 9
    @Test
    @WithMockUser
    void markRead_whenNotFound_returns404() throws Exception {
        when(notificationService.markAsRead(99L))
                .thenThrow(new RuntimeException("Notification not found with id: 99"));

        mockMvc.perform(put("/api/notifications/99/read")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // 10
    @Test
    @WithMockUser
    void markAllRead_success_returns200() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/api/notifications/employee/1/read-all")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    // 11
    @Test
    @WithMockUser
    void getAll_returnsCorrectType() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        n.setType(Notification.Type.PAYROLL);

        when(notificationService.getNotifications(1L)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/notifications/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("PAYROLL"));
    }

    // 12
    @Test
    @WithMockUser
    void markAllRead_whenEmployeeNotFound_returns404() throws Exception {
        doThrow(new RuntimeException("Employee not found with id: 99"))
                .when(notificationService).markAllAsRead(99L);

        mockMvc.perform(put("/api/notifications/employee/99/read-all")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}