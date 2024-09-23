package com.MiTi.MiTi.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChartService {

    // 인기 앨범 목록을 반환하는 메서드 (예시 데이터)
    public List<String> getPopularAlbums() {
        // 예시로 하드코딩된 인기 앨범 목록
        return Arrays.asList(
                "Album 1 - Artist A",
                "Album 2 - Artist B",
                "Album 3 - Artist C",
                "Album 4 - Artist D",
                "Album 5 - Artist E"
        );
    }
}
