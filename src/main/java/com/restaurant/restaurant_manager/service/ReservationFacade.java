package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.reservation.CreateReservationRequest;
import com.restaurant.restaurant_manager.dto.reservation.ReservationResponse;
import com.restaurant.restaurant_manager.entity.Customer;
import com.restaurant.restaurant_manager.entity.Reservation;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    @Transactional
    public ReservationResponse createReservationForUser(User user, CreateReservationRequest request) {
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Customer profile not found"));

        Reservation reservation = reservationService.createReservation(customer, request);
        sendNotificationEmail(customer, reservation);
        return ReservationResponse.fromEntity(reservation);
    }

    @Transactional
    public ReservationResponse createReservationForGuest(CreateReservationRequest request) {
        if (request.getCustomerInfo() == null) throw new BadRequestException("Customer info required");

        Customer customer = customerService.findOrCreateCustomer(
                request.getCustomerInfo().getPhone(),
                request.getCustomerInfo().getName(),
                request.getCustomerInfo().getEmail(),
                request.getCustomerInfo().getAddress()
        );

        Reservation reservation = reservationService.createReservation(customer, request);
        if (customer.getEmail() != null) sendNotificationEmail(customer, reservation);
        return ReservationResponse.fromEntity(reservation);
    }

    private void sendNotificationEmail(Customer customer, Reservation r) {
        try {
            String subject = "Reservation Received";
            String text = "Hi " + customer.getName() + ",\n" +
                    "We received your booking for " + r.getNumberOfGuests() + " people at " + r.getReservationTime() + ".\n" +
                    "We will confirm shortly.";
            emailService.sendEmail(customer.getEmail(), subject, text);
        } catch (Exception e) {}
    }
}