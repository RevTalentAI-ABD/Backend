package com.revtalent.revtalent.repository;

import com.revtalent.revtalent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}