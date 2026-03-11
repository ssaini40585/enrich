package com.enrichplus.allocation.controller;

import com.enrichplus.allocation.service.AllocationService;
import com.enrichplus.common.dto.AllocationDecision;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping("/run")
    public List<AllocationDecision> runAllocationCycle() {
        return allocationService.runAllocationCycle();
    }
}
