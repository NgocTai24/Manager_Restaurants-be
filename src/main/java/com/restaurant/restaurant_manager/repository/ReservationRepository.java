// src/main/java/com/restaurant/restaurant_manager/repository/ReservationRepository.java
package com.restaurant.restaurant_manager.repository;

import com.restaurant.restaurant_manager.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> { }