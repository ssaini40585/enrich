package com.enrichplus.staffing.repository;

import com.enrichplus.common.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByPrimarySkillAndStatus(String skill, String status);

    Optional<Employee> findByEmail(String email);
}
