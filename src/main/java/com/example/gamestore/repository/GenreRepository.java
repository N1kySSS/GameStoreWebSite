package com.example.gamestore.repository;

import com.example.gamestore.entity.Genre;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GenreRepository extends BaseRepository<Genre, UUID> {

}
