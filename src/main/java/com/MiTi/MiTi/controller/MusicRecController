package com.MiTi.MiTi.controller;

import com.MiTi.MiTi.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MusicRecController {

    @Autowired
    private PlaylistService playlistService;  // 기존 PlaylistService 사용

    @GetMapping("/recommend/{albumId}")
    public ResponseEntity<Map<String, Object>> getRecommendedAlbums(@PathVariable Long albumId) {
        // 1. albumId로부터 추천곡 20개를 Python 스크립트를 통해 가져옴
        List<Long> albumIds = Collections.singletonList(albumId);
        List<Long> recommendedAlbums = runPythonScript(albumIds);

        // 2. 가져온 추천 앨범 정보들을 JSON으로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("recommendedAlbums", recommendedAlbums);

        // MusicRecController의 getRecommendedAlbums 메서드에서
        System.out.println("Recommended albums: " + recommendedAlbums);  // 추천 앨범 로그 찍기


        return ResponseEntity.ok(response);
    }

    // 기존 runPythonScript 메서드 사용
    public List<Long> runPythonScript(List<Long> albumIds) {
        List<Long> results = new ArrayList<>();
        try {
            // Python 스크립트 경로 확인
            ProcessBuilder processBuilder = new ProcessBuilder("python", "C:\\mititest\\src\\main\\scripts\\recommendation_algorithm.py");
            processBuilder.redirectErrorStream(true);  // 에러 스트림도 표준 출력에 포함

            Process process = processBuilder.start();

            // Python 스크립트에 albumIds 전달
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            for (Long albumId : albumIds) {
                writer.write(albumId.toString());
                writer.newLine();  // 각 ID는 개행으로 구분
            }
            writer.close();

            // Python 스크립트의 출력 결과 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    results.add(Long.valueOf(line));  // 각 줄을 Long으로 변환해서 추가
                } catch (NumberFormatException e) {
                    System.err.println("Invalid output from Python script: " + line);
                }
            }
            reader.close();

            int exitCode = process.waitFor();  // 프로세스가 종료될 때까지 대기
            if (exitCode != 0) {
                System.err.println("Python script exited with error code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }

}
