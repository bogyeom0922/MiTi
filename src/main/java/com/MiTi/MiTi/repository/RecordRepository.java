package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Record;
import com.MiTi.MiTi.entity.RecordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, RecordId> {
}
