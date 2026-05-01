package com.revtalent.revtalent.service;

import com.revtalent.revtalent.dto.employee.EmployeeResponseManager;
import com.revtalent.revtalent.dto.employee.ManagerProfileResponse;
import com.revtalent.revtalent.model.Department;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.User;
import com.revtalent.revtalent.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private User testUser;
    private Department testDept;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .username("johndoe")
                .email("john@revtalent.com")
                .role(User.Role.EMPLOYEE)
                .build();

        testDept = Department.builder()
                .id(10L)
                .name("Engineering")
                .build();

        testEmployee = Employee.builder()
                .id(1L)
                .user(testUser)
                .department(testDept)
                .designation("Software Engineer")
                .employeeCode("EMP001")
                .phone("9876543210")
                .status(Employee.Status.ACTIVE)
                .joiningDate(LocalDate.of(2022, 1, 15))
                .dateOfBirth(LocalDate.of(1995, 6, 20))
                .gender("Male")
                .address("123 Main St, Chennai")
                .profilePictureUrl("https://cdn.example.com/pic.jpg")
                .build();
    }

    @Nested
    @DisplayName("getTeam()")
    class GetTeam {

        @Test
        @DisplayName("returns mapped EmployeeResponse list when employees exist")
        void returnsListWhenEmployeesExist() {
            when(employeeRepository.findAll()).thenReturn(List.of(testEmployee));

            List<EmployeeResponseManager> result = employeeService.getTeam();

            assertThat(result).hasSize(1);
            EmployeeResponseManager r = result.get(0);
            assertThat(r.getId()).isEqualTo(1L);
            assertThat(r.getName()).isEqualTo("johndoe");
            assertThat(r.getDesignation()).isEqualTo("Software Engineer");
            assertThat(r.getDepartment()).isEqualTo("Engineering");
            assertThat(r.getStatus()).isEqualTo("ACTIVE");
            assertThat(r.getEmployeeCode()).isEqualTo("EMP001");
            assertThat(r.getPhone()).isEqualTo("9876543210");
        }

        @Test
        @DisplayName("returns empty list when no employees exist")
        void returnsEmptyListWhenNoEmployees() {
            when(employeeRepository.findAll()).thenReturn(List.of());

            List<EmployeeResponseManager> result = employeeService.getTeam();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("maps 'N/A' for employee with null user")
        void mapsNullUserToNA() {
            Employee noUser = Employee.builder()
                    .id(2L)
                    .user(null)
                    .department(testDept)
                    .designation("QA")
                    .employeeCode("EMP002")
                    .status(Employee.Status.ACTIVE)
                    .build();
            when(employeeRepository.findAll()).thenReturn(List.of(noUser));

            List<EmployeeResponseManager> result = employeeService.getTeam();

            assertThat(result.get(0).getName()).isEqualTo("N/A");
        }

        @Test
        @DisplayName("maps 'N/A' for employee with null department")
        void mapsNullDepartmentToNA() {
            testEmployee.setDepartment(null);
            when(employeeRepository.findAll()).thenReturn(List.of(testEmployee));

            List<EmployeeResponseManager> result = employeeService.getTeam();

            assertThat(result.get(0).getDepartment()).isEqualTo("N/A");
        }
    }

    @Nested
    @DisplayName("getEmployeeById()")
    class GetEmployeeById {

        @Test
        @DisplayName("returns EmployeeResponse for valid id")
        void returnsResponseForValidId() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

            EmployeeResponseManager result = employeeService.getEmployeeById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("johndoe");
            verify(employeeRepository).findById(1L);
        }

        @Test
        @DisplayName("throws RuntimeException for non-existent id")
        void throwsForNonExistentId() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Employee not found with id: 99");
        }
    }

    @Nested
    @DisplayName("searchTeam()")
    class SearchTeam {

        @Test
        @DisplayName("returns matching employees when query matches username (case-insensitive)")
        void returnsMatchesForQuery() {
            when(employeeRepository.findAll()).thenReturn(List.of(testEmployee));

            List<EmployeeResponseManager> result = employeeService.searchTeam("JOHN");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("johndoe");
        }

        @Test
        @DisplayName("returns empty list when no username matches query")
        void returnsEmptyWhenNoMatch() {
            when(employeeRepository.findAll()).thenReturn(List.of(testEmployee));

            List<EmployeeResponseManager> result = employeeService.searchTeam("xyz");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("excludes employees with null user from search results")
        void excludesNullUserEmployees() {
            Employee noUser = Employee.builder().id(2L).user(null).status(Employee.Status.ACTIVE).build();
            when(employeeRepository.findAll()).thenReturn(List.of(testEmployee, noUser));

            // "john" should match testEmployee but noUser should be safely skipped
            List<EmployeeResponseManager> result = employeeService.searchTeam("john");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getManagerProfile()")
    class GetManagerProfile {

        @BeforeEach
        void setUpManager() {
            testUser = User.builder()
                    .id(1L)
                    .name("Alice Manager")
                    .username("alice")
                    .email("alice@revtalent.com")
                    .role(User.Role.MANAGER)
                    .build();
            testEmployee.setUser(testUser);
        }

        @Test
        @DisplayName("returns full ManagerProfileResponse for valid manager id")
        void returnsProfileForValidManager() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.countByManagerId(1L)).thenReturn(5L);

            ManagerProfileResponse profile = employeeService.getManagerProfile(1L);

            assertThat(profile.getId()).isEqualTo(1L);
            assertThat(profile.getName()).isEqualTo("Alice Manager");
            assertThat(profile.getUsername()).isEqualTo("alice");
            assertThat(profile.getEmail()).isEqualTo("alice@revtalent.com");
            assertThat(profile.getDesignation()).isEqualTo("Software Engineer");
            assertThat(profile.getDepartment()).isEqualTo("Engineering");
            assertThat(profile.getEmployeeCode()).isEqualTo("EMP001");
            assertThat(profile.getPhone()).isEqualTo("9876543210");
            assertThat(profile.getGender()).isEqualTo("Male");
            assertThat(profile.getJoiningDate()).isEqualTo(LocalDate.of(2022, 1, 15));
            assertThat(profile.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 6, 20));
            assertThat(profile.getAddress()).isEqualTo("123 Main St, Chennai");
            assertThat(profile.getProfilePictureUrl()).isEqualTo("https://cdn.example.com/pic.jpg");
            assertThat(profile.getStatus()).isEqualTo("ACTIVE");
            assertThat(profile.getTeamSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("teamSize is correctly fetched from countByManagerId")
        void teamSizeCorrect() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.countByManagerId(1L)).thenReturn(12L);

            ManagerProfileResponse profile = employeeService.getManagerProfile(1L);

            assertThat(profile.getTeamSize()).isEqualTo(12);
            verify(employeeRepository).countByManagerId(1L);
        }

        @Test
        @DisplayName("maps 'N/A' fields when manager has null user")
        void mapsNAWhenUserIsNull() {
            testEmployee.setUser(null);
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.countByManagerId(1L)).thenReturn(0L);

            ManagerProfileResponse profile = employeeService.getManagerProfile(1L);

            assertThat(profile.getName()).isEqualTo("N/A");
            assertThat(profile.getUsername()).isEqualTo("N/A");
            assertThat(profile.getEmail()).isEqualTo("N/A");
        }

        @Test
        @DisplayName("maps 'N/A' department when manager has null department")
        void mapsNAWhenDeptIsNull() {
            testEmployee.setDepartment(null);
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.countByManagerId(1L)).thenReturn(0L);

            ManagerProfileResponse profile = employeeService.getManagerProfile(1L);

            assertThat(profile.getDepartment()).isEqualTo("N/A");
        }

        @Test
        @DisplayName("throws RuntimeException when manager id not found")
        void throwsWhenManagerNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.getManagerProfile(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Manager not found with id: 99");

            verify(employeeRepository, never()).countByManagerId(anyLong());
        }
    }
}
