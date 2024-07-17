package MiTi.service;

import com.MiTi.dto.RecordDto;
import com.MiTi.entity.Record;
import com.MiTi.repository.RecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecordService {
    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Transactional
    public List<RecordDto> getRecordList() {
        List<Record> recordList = recordRepository.findAll();
        List<RecordDto> recordDtoList = new ArrayList<>();

        for (Record record : recordList) {
            RecordDto recordDto = RecordDto.builder()
                    .user_id(record.getUser_id())
                    .album_id(record.getAlbum_id())
                    .build();
            recordDtoList.add(recordDto);
        }
        return recordDtoList;
    }
}
