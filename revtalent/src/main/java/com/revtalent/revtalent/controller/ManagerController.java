package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.AssignManagerRequest;

import com.revtalent.revtalent.model.Users;
import com.revtalent.revtalent.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@CrossOrigin("*")
public class ManagerController {

    @Autowired
    private UserRepository userRepository;

    // GET ALL MANAGERS

    @GetMapping
    public List<Users> getManagers() {

        return userRepository.findByRole(
                Users.Role.MANAGER
        );
    }

    // GET EMPLOYEES UNDER MANAGER

    @GetMapping("/{managerId}/employees")
    public List<Users> getEmployeesUnderManager(
            @PathVariable Long managerId
    ) {

        return userRepository.findByManager_Id(
                managerId
        );
    }

    // ASSIGN EMPLOYEE TO MANAGER

    @PutMapping("/assign")
    public ResponseEntity<?> assignManager(

            @RequestBody
            AssignManagerRequest request
    ) {

        Users employee =
                userRepository.findById(
                        request.getEmployeeId()
                ).orElseThrow();

        Users manager =
                userRepository.findById(
                        request.getManagerId()
                ).orElseThrow();

        employee.setManager(manager);

        userRepository.save(employee);

        return ResponseEntity.ok(
                "Employee assigned to manager"
        );
    }

    // REMOVE EMPLOYEE FROM MANAGER

    @PutMapping("/remove/{employeeId}")
    public ResponseEntity<?> removeManager(
            @PathVariable Long employeeId
    ) {

        Users employee =
                userRepository.findById(
                        employeeId
                ).orElseThrow();

        employee.setManager(null);

        userRepository.save(employee);

        return ResponseEntity.ok(
                "Employee removed from manager"
        );
    }

    // PROMOTE EMPLOYEE TO MANAGER

    @PutMapping("/promote/{id}")
    public ResponseEntity<?> promoteEmployee(
            @PathVariable Long id
    ) {

        Users user =
                userRepository.findById(id)
                        .orElseThrow();

        user.setRole(
                Users.Role.MANAGER
        );

        userRepository.save(user);

        return ResponseEntity.ok(
                "Employee promoted to manager"
        );
    }

    // DEMOTE MANAGER TO EMPLOYEE

    @PutMapping("/demote/{id}")
    public ResponseEntity<?> demoteManager(
            @PathVariable Long id
    ) {

        Users user =
                userRepository.findById(id)
                        .orElseThrow();

        user.setRole(
                Users.Role.EMPLOYEE
        );

        user.setManager(null);

        userRepository.save(user);

        return ResponseEntity.ok(
                "Manager demoted to employee"
        );
    }
}