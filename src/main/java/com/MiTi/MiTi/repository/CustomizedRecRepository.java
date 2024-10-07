package com.MiTi.MiTi.repository;

import com.MiTi.MiTi.entity.Album;
import com.MiTi.MiTi.entity.CustomizedRec;
import com.MiTi.MiTi.entity.CustomizedRecId;
import com.nimbusds.jose.crypto.impl.CompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomizedRecRepository extends JpaRepository<CustomizedRec, CustomizedRecId> {

    // userId를 기준으로 CustomizedRec 목록을 조회하는 메서드 추가
    List<CustomizedRec> findByUserId(String userId);

    @Query("SELECT a FROM album a JOIN CustomizedRec cr ON a.id = cr.albumId WHERE cr.userId = :userId")
    List<Album> findCustomizedAlbumsByUserId(@Param("userId") String userId);
}
