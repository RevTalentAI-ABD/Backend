package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
<<<<<<< HEAD
    Optional<User> findByEmail(String email);
=======
    boolean existsByUsername(String username);
>>>>>>> df44851a375836bc5ba56b6a53604b165e706241
}