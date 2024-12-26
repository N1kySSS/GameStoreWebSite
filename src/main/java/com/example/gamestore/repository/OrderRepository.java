package com.example.gamestore.repository;

import com.example.gamestore.entity.Order;
import com.example.gamestore.entity.enums.Status;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends BaseRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    void updateStatusById(@Param("status") Status status, @Param("id") UUID id);
}
