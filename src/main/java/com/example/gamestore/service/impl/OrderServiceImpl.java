package com.example.gamestore.service.impl;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.entity.Order;
import com.example.gamestore.entity.enums.Status;
import com.example.gamestore.repository.OrderRepository;
import com.example.gamestore.service.OrderService;
import com.example.gamestore.utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableCaching
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(cacheNames = "orders", allEntries = true)
    public OrderDTO addOrder(OrderDTO orderDTO) {
        if (!this.validationUtil.isValid(orderDTO)) {
            this.validationUtil
                    .violations(orderDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные заказа не валидны");
        }

        if (orderDTO == null) {
            throw new RuntimeException("Данные заказа отсутствуют");
        }
        Order order = modelMapper.map(orderDTO, Order.class);
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    @Cacheable("orders")
    public List<OrderDTO> getAll() {
        List<Order> orders = (List<Order>) orderRepository.findAll();
        List<OrderDTO> dtoOrders = new ArrayList<>();
        orders.forEach(it -> dtoOrders.add(modelMapper.map(it, OrderDTO.class)));

        if (dtoOrders.isEmpty()) {
            throw new RuntimeException("Заказы не найдены");
        } else {
            return dtoOrders;
        }
    }

    @Override
    public OrderDTO getById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            return modelMapper.map(order, OrderDTO.class);
        } else {
            throw new RuntimeException("Заказ с таким id: " + id + " не найден");
        }
    }

    @Override
    public List<OrderDTO> getAllByUserId(UUID userId) {
        if (userId == null) {
            throw new RuntimeException("Неверный id");
        }

        List<Order> userOrders = orderRepository.findAllByUserId(userId);
        List<OrderDTO> userDtoOrders = new ArrayList<>();
        userOrders.forEach(it -> userDtoOrders.add(modelMapper.map(it, OrderDTO.class)));

        return userDtoOrders;
    }

    @Override
    public List<OrderDTO> getByDate(LocalDate dateOfOrder, UUID userId) {
        if (userId == null) {
            throw new RuntimeException("Неверный id");
        }

        if (dateOfOrder == null) {
            throw new RuntimeException("Неверная дата рождения");
        }

        List<Order> userOrders = orderRepository.findByDate(dateOfOrder, userId);
        List<OrderDTO> userDtoOrders = new ArrayList<>();
        userOrders.forEach(it -> userDtoOrders.add(modelMapper.map(it, OrderDTO.class)));

        if (userDtoOrders.isEmpty()) {
            throw new RuntimeException("У этого пользователя нет заказов за эту дату: " + dateOfOrder);
        } else {
            return userDtoOrders;
        }
    }

    @Override
    @CacheEvict(cacheNames = "orders", allEntries = true)
    public void deleteOrder(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (orderRepository.findById(id).isPresent()) {
            orderRepository.deleteById(id);
        } else {
            throw new RuntimeException("Заказа с таким id: " + id + " не существует");
        }
    }

    @Override
    @CacheEvict(cacheNames = "orders", allEntries = true)
    public void update(Status status, UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (orderRepository.findById(id).isPresent()) {
            orderRepository.updateStatusById(status, id);
        } else {
            throw new RuntimeException("Заказа с таким id: " + id + " не существует");
        }
    }
}
