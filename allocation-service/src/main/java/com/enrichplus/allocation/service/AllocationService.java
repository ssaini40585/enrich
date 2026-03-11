package com.enrichplus.allocation.service;

import com.enrichplus.allocation.client.DemandClient;
import com.enrichplus.allocation.client.StaffingClient;
import com.enrichplus.allocation.repository.AllocationRecordRepository;
import com.enrichplus.common.dto.AllocationDecision;
import com.enrichplus.common.entity.AllocationRecord;
import com.enrichplus.common.entity.Employee;
import com.enrichplus.common.entity.TalentDemand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AllocationService {
    private final DemandClient demandClient;
    private final StaffingClient staffingClient;
    private final AllocationRecordRepository allocationRecordRepository;

    public AllocationService(DemandClient demandClient,
                             StaffingClient staffingClient,
                             AllocationRecordRepository allocationRecordRepository) {
        this.demandClient = demandClient;
        this.staffingClient = staffingClient;
        this.allocationRecordRepository = allocationRecordRepository;
    }

    public List<AllocationDecision> runAllocationCycle() {
        List<TalentDemand> demands = demandClient.getOpenDemands();
        List<AllocationDecision> decisions = new ArrayList<>();

        for (TalentDemand demand : demands) {
            int openSlots = demand.getRequestedCount() - demand.getFulfilledCount();
            if (openSlots <= 0) {
                continue;
            }

            List<Employee> candidates = staffingClient.findAvailableBySkill(demand.getRequiredSkill());
            for (int i = 0; i < openSlots && i < candidates.size(); i++) {
                Employee employee = candidates.get(i);
                staffingClient.markAllocated(employee.getId());
                demandClient.incrementFulfilledCount(demand.getId());

                AllocationRecord record = new AllocationRecord();
                record.setDemandId(demand.getId());
                record.setEmployeeId(employee.getId());
                record.setAllocationStatus("ALLOCATED");
                allocationRecordRepository.save(record);

                decisions.add(new AllocationDecision(demand.getId(), employee.getId(), "Allocated successfully"));
            }
        }
        return decisions;
    }
}
