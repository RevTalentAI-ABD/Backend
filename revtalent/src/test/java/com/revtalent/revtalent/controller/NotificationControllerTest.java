package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.notification.NotificationResponse;
import com.revtalent.revtalent.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("NotificationController – MockMvc Tests")
class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private NotificationService notificationService;

    private NotificationResponse makeResponse(Long id, String message, String type, boolean unread) {
        return NotificationResponse.builder()
                .id(id)
                .message(message)
                .type(type)
                .unread(unread)
                .createdAt(LocalDateTime.of(2025, 5, 1, 10, 0))
                .build();
    }

    @Nested
    @DisplayName("GET /api/manager/notifications")
    class GetNotifications {

        @Test
        @DisplayName("returns 200 with all notifications list")
        void returns200WithList() throws Exception {
            when(notificationService.getNotifications()).thenReturn(List.of(
                    makeResponse(1L, "Alice applied for leave", "LEAVE", true),
                    makeResponse(2L, "May payroll processed", "PAYROLL", false)
            ));

            mockMvc.perform(get("/api/manager/notifications").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].message").value("Alice applied for leave"))
                    .andExpect(jsonPath("$[0].type").value("LEAVE"))
                    .andExpect(jsonPath("$[0].unread").value(true))
                    .andExpect(jsonPath("$[1].unread").value(false));
        }

        @Test
        @DisplayName("returns 200 with empty list when no notifications")
        void returns200EmptyList() throws Exception {
            when(notificationService.getNotifications()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/notifications").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("delegates to notificationService.getNotifications() once")
        void delegatesToService() throws Exception {
            when(notificationService.getNotifications()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/notifications"));

            verify(notificationService, times(1)).getNotifications();
        }
    }

    @Nested
    @DisplayName("GET /api/manager/notifications/unread")
    class GetUnread {

        @Test
        @DisplayName("returns 200 with only unread notifications")
        void returns200WithUnread() throws Exception {
            when(notificationService.getUnreadNotifications()).thenReturn(List.of(
                    makeResponse(1L, "New leave request", "LEAVE", true)
            ));

            mockMvc.perform(get("/api/manager/notifications/unread").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].unread").value(true))
                    .andExpect(jsonPath("$[0].type").value("LEAVE"));
        }

        @Test
        @DisplayName("returns 200 with empty list when all notifications are read")
        void returns200EmptyWhenAllRead() throws Exception {
            when(notificationService.getUnreadNotifications()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/notifications/unread").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("delegates to getUnreadNotifications(), not getNotifications()")
        void delegatesToCorrectServiceMethod() throws Exception {
            when(notificationService.getUnreadNotifications()).thenReturn(List.of());

            mockMvc.perform(get("/api/manager/notifications/unread"));

            verify(notificationService).getUnreadNotifications();
            verify(notificationService, never()).getNotifications();
        }
    }

    @Nested
    @DisplayName("PUT /api/manager/notifications/{id}/read")
    class MarkRead {

        @Test
        @DisplayName("returns 200 'Marked as read' on success")
        void returns200OnSuccess() throws Exception {
            doNothing().when(notificationService).markAsRead(1L);

            mockMvc.perform(put("/api/manager/notifications/1/read"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Marked as read"));
        }

        @Test
        @DisplayName("calls markAsRead() with the correct path variable id")
        void passesCorrectId() throws Exception {
            doNothing().when(notificationService).markAsRead(42L);

            mockMvc.perform(put("/api/manager/notifications/42/read"));

            verify(notificationService).markAsRead(42L);
        }

        @Test
        @DisplayName("returns 404 when notification not found")
        void returns404WhenNotFound() throws Exception {
            doThrow(new RuntimeException("Notification not found with id: 99"))
                    .when(notificationService).markAsRead(99L);

            mockMvc.perform(put("/api/manager/notifications/99/read"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/manager/notifications/read-all")
    class MarkAllRead {

        @Test
        @DisplayName("returns 200 'All marked as read' on success")
        void returns200OnSuccess() throws Exception {
            doNothing().when(notificationService).markAllAsRead();

            mockMvc.perform(put("/api/manager/notifications/read-all"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("All marked as read"));
        }

        @Test
        @DisplayName("delegates to notificationService.markAllAsRead() once")
        void delegatesToService() throws Exception {
            doNothing().when(notificationService).markAllAsRead();

            mockMvc.perform(put("/api/manager/notifications/read-all"));

            verify(notificationService, times(1)).markAllAsRead();
        }
    }
}