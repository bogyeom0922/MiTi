package MiTi.repository;

import com.MiTi.entity.Record;
import com.MiTi.entity.RecordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, RecordId> {
}
