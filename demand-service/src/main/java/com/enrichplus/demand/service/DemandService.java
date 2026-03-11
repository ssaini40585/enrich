package com.enrichplus.demand.service;

import com.enrichplus.common.entity.TalentDemand;
import com.enrichplus.demand.repository.TalentDemandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandService {
    private final TalentDemandRepository demandRepository;

    public DemandService(TalentDemandRepository demandRepository) {
        this.demandRepository = demandRepository;
    }

    public TalentDemand createDemand(TalentDemand talentDemand) {
        talentDemand.setFulfilledCount(0);
        return demandRepository.save(talentDemand);
    }

    public List<TalentDemand> getAllDemands() {
        return demandRepository.findAll();
    }

    public TalentDemand incrementFulfilledCount(Long demandId) {
        TalentDemand demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + demandId));

        demand.setFulfilledCount(demand.getFulfilledCount() + 1);
        return demandRepository.save(demand);
    }
}
