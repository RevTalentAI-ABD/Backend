package com.revtalent.revtalent.repository;
import java.util.Optional;
import com.revtalent.revtalent.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {


    Optional<Department> findByName(String name);
}