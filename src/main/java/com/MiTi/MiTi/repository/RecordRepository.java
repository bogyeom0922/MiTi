package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByUserId(String userId);
}