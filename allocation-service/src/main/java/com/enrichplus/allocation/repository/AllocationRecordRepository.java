package com.enrichplus.allocation.repository;

import com.enrichplus.common.entity.AllocationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllocationRecordRepository extends JpaRepository<AllocationRecord, Long> {
}
