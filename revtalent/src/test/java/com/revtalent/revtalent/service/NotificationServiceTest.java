package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.notification.NotificationResponse;
import com.revtalent.revtalent.model.Notification;
import com.revtalent.revtalent.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;
    private Notification unreadLeaveNotif;
    private Notification readPayrollNotif;

    @BeforeEach
    void setUp() {
        unreadLeaveNotif = Notification.builder()
                .id(1L)
                .message("Alice applied for sick leave")
                .type(Notification.Type.LEAVE)
                .read(false)
                .createdAt(LocalDateTime.of(2025, 5, 1, 10, 0))
                .build();

        readPayrollNotif = Notification.builder()
                .id(2L)
                .message("May payroll processed")
                .type(Notification.Type.PAYROLL)
                .read(true)
                .createdAt(LocalDateTime.of(2025, 5, 2, 9, 0))
                .build();
    }

    @Nested
    @DisplayName("getNotifications()")
    class GetNotifications {

        @Test
        @DisplayName("returns all notifications mapped to NotificationResponse")
        void returnsAllNotifications() {
            when(notificationRepository.findAll())
                    .thenReturn(List.of(unreadLeaveNotif, readPayrollNotif));

            List<NotificationResponse> result = notificationService.getNotifications();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("maps all fields correctly — id, message, type, unread, createdAt")
        void mapsAllFieldsCorrectly() {
            when(notificationRepository.findAll()).thenReturn(List.of(unreadLeaveNotif));

            NotificationResponse r = notificationService.getNotifications().get(0);

            assertThat(r.getId()).isEqualTo(1L);
            assertThat(r.getMessage()).isEqualTo("Alice applied for sick leave");
            assertThat(r.getType()).isEqualTo("LEAVE");
            assertThat(r.isUnread()).isTrue();                          // read=false → unread=true
            assertThat(r.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 5, 1, 10, 0));
        }

        @Test
        @DisplayName("unread is false when notification has been read")
        void unreadFalseWhenRead() {
            when(notificationRepository.findAll()).thenReturn(List.of(readPayrollNotif));

            NotificationResponse r = notificationService.getNotifications().get(0);

            assertThat(r.isUnread()).isFalse();
        }

        @Test
        @DisplayName("maps null type as null string (not throws)")
        void mapsNullTypeAsNull() {
            Notification noType = Notification.builder()
                    .id(3L).message("System alert").type(null).read(false)
                    .createdAt(LocalDateTime.now()).build();
            when(notificationRepository.findAll()).thenReturn(List.of(noType));

            NotificationResponse r = notificationService.getNotifications().get(0);

            assertThat(r.getType()).isNull();
        }

        @Test
        @DisplayName("returns empty list when no notifications exist")
        void returnsEmptyList() {
            when(notificationRepository.findAll()).thenReturn(List.of());

            assertThat(notificationService.getNotifications()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUnreadNotifications()")
    class GetUnreadNotifications {

        @Test
        @DisplayName("calls findByReadFalse() on repository")
        void callsFindByReadFalse() {
            when(notificationRepository.findByReadFalse()).thenReturn(List.of(unreadLeaveNotif));

            notificationService.getUnreadNotifications();

            verify(notificationRepository).findByReadFalse();
            verify(notificationRepository, never()).findAll();
        }

        @Test
        @DisplayName("returns only unread notifications mapped to DTO")
        void returnsOnlyUnread() {
            when(notificationRepository.findByReadFalse()).thenReturn(List.of(unreadLeaveNotif));

            List<NotificationResponse> result = notificationService.getUnreadNotifications();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).isUnread()).isTrue();
        }

        @Test
        @DisplayName("returns empty list when all notifications are read")
        void returnsEmptyWhenAllRead() {
            when(notificationRepository.findByReadFalse()).thenReturn(List.of());

            assertThat(notificationService.getUnreadNotifications()).isEmpty();
        }
    }

    @Nested
    @DisplayName("markAsRead()")
    class MarkAsRead {

        @Test
        @DisplayName("sets read=true and saves the notification")
        void setsReadTrueAndSaves() {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(unreadLeaveNotif));

            notificationService.markAsRead(1L);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            assertThat(captor.getValue().isRead()).isTrue();
        }

        @Test
        @DisplayName("throws RuntimeException when notification id not found")
        void throwsWhenNotFound() {
            when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notificationService.markAsRead(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Notification not found with id: 99");

            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("still saves successfully if notification was already read")
        void savesEvenIfAlreadyRead() {
            when(notificationRepository.findById(2L)).thenReturn(Optional.of(readPayrollNotif));

            notificationService.markAsRead(2L);

            verify(notificationRepository).save(readPayrollNotif);
            assertThat(readPayrollNotif.isRead()).isTrue();
        }
    }

    @Nested
    @DisplayName("markAllAsRead()")
    class MarkAllAsRead {

        @Test
        @DisplayName("sets read=true on all unread notifications and calls saveAll")
        void setsReadOnAllAndSavesAll() {
            Notification n1 = Notification.builder().id(1L).message("A").type(Notification.Type.LEAVE)
                    .read(false).createdAt(LocalDateTime.now()).build();
            Notification n2 = Notification.builder().id(2L).message("B").type(Notification.Type.SYSTEM)
                    .read(false).createdAt(LocalDateTime.now()).build();
            when(notificationRepository.findByReadFalse()).thenReturn(List.of(n1, n2));

            notificationService.markAllAsRead();

            assertThat(n1.isRead()).isTrue();
            assertThat(n2.isRead()).isTrue();

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
            verify(notificationRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).hasSize(2);
        }

        @Test
        @DisplayName("calls saveAll with empty list when no unread notifications exist")
        void saveAllWithEmptyListWhenNoneUnread() {
            when(notificationRepository.findByReadFalse()).thenReturn(List.of());

            notificationService.markAllAsRead();

            verify(notificationRepository).saveAll(List.of());
        }

        @Test
        @DisplayName("fetches only unread via findByReadFalse, not findAll")
        void fetchesOnlyUnread() {
            when(notificationRepository.findByReadFalse()).thenReturn(List.of());

            notificationService.markAllAsRead();

            verify(notificationRepository).findByReadFalse();
            verify(notificationRepository, never()).findAll();
        }
    }
}