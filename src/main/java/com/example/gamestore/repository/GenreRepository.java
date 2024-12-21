package com.example.gamestore.repository;

import com.example.gamestore.entity.Genre;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GenreRepository extends BaseRepository<Genre, UUID> {

    @Query("SELECT g FROM Genre g WHERE g.name = :name")
    Genre findByName(@Param("name") String name);
}
