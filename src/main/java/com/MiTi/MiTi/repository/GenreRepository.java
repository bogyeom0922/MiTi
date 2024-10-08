package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findByProviderId(String providerId);
    void deleteById(Long id);

}