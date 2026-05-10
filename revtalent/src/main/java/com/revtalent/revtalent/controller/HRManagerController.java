package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.model.Users;
import com.revtalent.revtalent.model.Employee;
import com.revtalent.revtalent.model.Department;
import com.revtalent.revtalent.repository.UserRepository;
import com.revtalent.revtalent.repository.EmployeeRepository;
import com.revtalent.revtalent.repository.DepartmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin("*")
public class HRManagerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Get all managers
    @GetMapping("/managers")
    public List<Users> getManagers() {
        return userRepository.findByRole(Users.Role.MANAGER);
    }

    // Assign manager
    @PutMapping("/assign-manager")
    public Users assignManager(
            @RequestBody Map<String, Object> body
    ) {
        Long employeeId = Long.parseLong(body.get("employeeId").toString());
        Long managerId  = Long.parseLong(body.get("managerId").toString());

        Users employeeUser = userRepository.findById(employeeId).orElseThrow();
        Users managerUser  = userRepository.findById(managerId).orElseThrow();
        employeeUser.setManager(managerUser);
        employeeUser.setDepartment(
                managerUser.getDepartment()
        );
        userRepository.save(employeeUser);

        employeeRepository.findByUser_Id(employeeId).ifPresent(emp -> {
            employeeRepository.findByUser_Id(managerId).ifPresent(mgr -> {
                emp.setManager(mgr);
                emp.setDepartment(
                        mgr.getDepartment()
                );

                employeeRepository.save(emp);
            });
        });

        return employeeUser;
    }

    // ✅ Change department — saves to BOTH Users and Employee entity
    @PutMapping("/change-department")
    public Users changeDepartment(
            @RequestBody Map<String, String> body
    ) {

        Long employeeId =
                Long.parseLong(
                        body.get("employeeId")
                );

        String deptName =
                body.get("department");

        // UPDATE USER

        Users employeeUser =
                userRepository
                        .findById(employeeId)
                        .orElseThrow();

        employeeUser.setDepartment(
                deptName
        );

        userRepository.save(employeeUser);

        // UPDATE EMPLOYEE ENTITY

        employeeRepository
                .findByUser_Id(employeeId)
                .ifPresent(emp -> {

                    Department dept =
                            departmentRepository
                                    .findByName(deptName)
                                    .orElseGet(() -> {

                                        Department newDept =
                                                new Department();

                                        newDept.setName(
                                                deptName
                                        );

                                        return departmentRepository
                                                .save(newDept);
                                    });

                    emp.setDepartment(dept);

                    employeeRepository.save(emp);
                });

        // IF MANAGER → UPDATE TEAM ALSO

        if (
                employeeUser.getRole() ==
                        Users.Role.MANAGER
        ) {

            List<Users> teamMembers =
                    userRepository
                            .findByManager_Id(employeeId);

            for (Users member : teamMembers) {

                member.setDepartment(
                        deptName
                );

                userRepository.save(member);

                employeeRepository
                        .findByUser_Id(member.getId())
                        .ifPresent(emp -> {

                            Department dept =
                                    departmentRepository
                                            .findByName(deptName)
                                            .orElse(null);

                            emp.setDepartment(dept);

                            employeeRepository.save(emp);
                        });
            }
        }

        return employeeUser;
    }
    // Change role
    @PutMapping("/change-role")
    public Users changeRole(
            @RequestBody Map<String, String> body
    ) {
        Long employeeId = Long.parseLong(body.get("employeeId"));
        String role     = body.get("role");

        Users employeeUser = userRepository.findById(employeeId).orElseThrow();
        employeeUser.setRole(Users.Role.valueOf(role));
        userRepository.save(employeeUser);

        return employeeUser;
    }

    @GetMapping("/managers/{id}/employees")
    public List<Users> getManagerEmployees(
            @PathVariable Long id
    ) {
        return userRepository.findByManager_Id(id);
    }
    @PutMapping("/remove-manager")
    public Users removeManager(
            @RequestBody Map<String, Long> body
    ) {

        Long employeeId =
                body.get("employeeId");

        // FIND USER

        Users employeeUser =
                userRepository
                        .findById(employeeId)
                        .orElseThrow();

        // REMOVE MANAGER FROM USERS TABLE

        employeeUser.setManager(null);

        userRepository.save(employeeUser);

        // REMOVE MANAGER FROM EMPLOYEE TABLE

        employeeRepository
                .findByUser_Id(employeeId)
                .ifPresent(emp -> {

                    emp.setManager(null);

                    employeeRepository.save(emp);
                });

        return employeeUser;
    }
}