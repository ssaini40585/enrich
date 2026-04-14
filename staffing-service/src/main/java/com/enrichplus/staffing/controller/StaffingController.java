package com.enrichplus.staffing.controller;

import com.enrichplus.common.entity.Employee;
import com.enrichplus.staffing.dto.CvUploadRequest;
import com.enrichplus.staffing.dto.ResumeBuilderRequest;
import com.enrichplus.staffing.service.EmployeeLifecycleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffing")
public class StaffingController {

    private final EmployeeLifecycleService employeeService;

    public StaffingController(EmployeeLifecycleService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/onboard")
    public Employee onboard(@RequestBody Employee employee) {
        return employeeService.onboard(employee);
    }

    @PatchMapping("/{id}/offboard")
    public Employee offboard(@PathVariable Long id) {
        return employeeService.offboard(id);
    }

    @GetMapping("/available")
    public List<Employee> availableEmployees(@RequestParam String skill) {
        return employeeService.listAvailableBySkill(skill);
    }

    @PatchMapping("/{id}/allocate")
    public Employee allocateEmployee(@PathVariable Long id) {
        return employeeService.markAllocated(id);
    }

    @PutMapping("/{id}/cv")
    public Employee uploadCvProfile(@PathVariable Long id, @RequestBody CvUploadRequest request) {
        return employeeService.updateCvProfile(id, request);
    }

    @PutMapping("/{id}/resume-builder")
    public Employee updateResumeBuilderProfile(@PathVariable Long id, @RequestBody ResumeBuilderRequest request) {
        return employeeService.updateResumeBuilderProfile(id, request);
    }

    @GetMapping("/{id}/profile")
    public Employee getEmployeeProfile(@PathVariable Long id) {
        return employeeService.getEmployeeProfile(id);
    }
}
