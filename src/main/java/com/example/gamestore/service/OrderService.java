package com.example.gamestore.service;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.entity.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTO addOrder(OrderDTO orderDTO);

    List<OrderDTO> getAll();

    OrderDTO getById(UUID id);

    List<OrderDTO> getAllByUserId(UUID userId);

    List<OrderDTO> getByDate(LocalDate dateOfOrder, UUID userId);

    void deleteOrder(UUID id);

    void update(Status status, UUID id);
}
