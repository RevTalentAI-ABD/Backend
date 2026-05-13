package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Users> findByRole(Users.Role role);
    List<Users> findByRoleIn(List<Users.Role> roles);          // ← add this
    List<Users> findByManager(Users manager);
    List<Users> findByManager_Id(Long id);
    List<Users> findByRoleAndManagerIsNull(Users.Role role);
    List<Users> findByIsActiveTrue();



}