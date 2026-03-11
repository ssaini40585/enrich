package com.enrichplus.staffing.service;

import com.enrichplus.common.entity.Employee;
import com.enrichplus.staffing.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeLifecycleService {
    private final EmployeeRepository employeeRepository;

    public EmployeeLifecycleService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee onboard(Employee employee) {
        employee.setStatus("AVAILABLE");
        return employeeRepository.save(employee);
    }

    public Employee offboard(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        employee.setStatus("OFFBOARDED");
        return employeeRepository.save(employee);
    }

    public List<Employee> listAvailableBySkill(String skill) {
        return employeeRepository.findByPrimarySkillAndStatus(skill, "AVAILABLE");
    }

    public Employee markAllocated(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        employee.setStatus("ALLOCATED");
        return employeeRepository.save(employee);
    }
}
