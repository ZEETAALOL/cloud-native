package cl.duoc.orders.repository;

import cl.duoc.orders.model.Order;
import cl.duoc.orders.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {
    
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
            order.setCreatedAt(LocalDateTime.now());
        }
        order.setUpdatedAt(LocalDateTime.now());
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
            .filter(order -> order.getStatus() == status)
            .collect(Collectors.toList());
    }

    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
            .filter(order -> order.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }

    public List<Order> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return orders.values().stream()
            .filter(order -> !order.getCreatedAt().isBefore(from) && !order.getCreatedAt().isAfter(to))
            .collect(Collectors.toList());
    }

    public List<Order> findByStatusAndDateRange(OrderStatus status, LocalDateTime from, LocalDateTime to) {
        return orders.values().stream()
            .filter(order -> order.getStatus() == status)
            .filter(order -> !order.getCreatedAt().isBefore(from) && !order.getCreatedAt().isAfter(to))
            .collect(Collectors.toList());
    }

    public boolean existsById(Long id) {
        return orders.containsKey(id);
    }

    public void deleteById(Long id) {
        orders.remove(id);
    }

    public long count() {
        return orders.size();
    }
}
