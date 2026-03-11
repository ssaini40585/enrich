package com.enrichplus.allocation.client;

import com.enrichplus.common.entity.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StaffingClient {
    private final RestTemplate restTemplate;
    private final String staffingServiceUrl;

    public StaffingClient(RestTemplate restTemplate,
                          @Value("${clients.staffing-service-url}") String staffingServiceUrl) {
        this.restTemplate = restTemplate;
        this.staffingServiceUrl = staffingServiceUrl;
    }

    public List<Employee> findAvailableBySkill(String skill) {
        ResponseEntity<List<Employee>> response = restTemplate.exchange(
                staffingServiceUrl + "/api/staffing/available?skill=" + skill,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    public void markAllocated(Long employeeId) {
        restTemplate.patchForObject(staffingServiceUrl + "/api/staffing/" + employeeId + "/allocate", null, Employee.class);
    }
}
