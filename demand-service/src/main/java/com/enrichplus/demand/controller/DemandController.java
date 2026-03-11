package com.enrichplus.demand.controller;

import com.enrichplus.common.entity.TalentDemand;
import com.enrichplus.demand.service.DemandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demands")
public class DemandController {
    private final DemandService demandService;

    public DemandController(DemandService demandService) {
        this.demandService = demandService;
    }

    @PostMapping
    public TalentDemand createDemand(@RequestBody TalentDemand demand) {
        return demandService.createDemand(demand);
    }

    @GetMapping
    public List<TalentDemand> getAllDemands() {
        return demandService.getAllDemands();
    }

    @PatchMapping("/{id}/fulfill")
    public TalentDemand fulfillOneSlot(@PathVariable Long id) {
        return demandService.incrementFulfilledCount(id);
    }
}
