package com.example.gamestore.repository;

import com.example.gamestore.entity.Information;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InformationRepository extends BaseRepository<Information, UUID> {

    @Query("SELECT i FROM Information i WHERE i.isChecked = :isChecked AND i.user.id = :userId")
    List<Information> findByStatus(@Param("isChecked") boolean isChecked, @Param("userId") UUID userId);
}
