package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.table.CreateTableRequest;
import com.restaurant.restaurant_manager.dto.table.TableResponse;
import com.restaurant.restaurant_manager.dto.table.UpdateTableRequest;
import com.restaurant.restaurant_manager.entity.RestaurantTable;
import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    // --- CREATE ---
    @Transactional
    public TableResponse createTable(CreateTableRequest request) {
        // Kiểm tra trùng tên bàn (Optional)
        // if (tableRepository.existsByName(request.getName())) {
        //     throw new BadRequestException("Table name already exists");
        // }

        RestaurantTable table = new RestaurantTable();
        table.setName(request.getName());
        table.setCapacity(request.getCapacity());
        table.setDescription(request.getDescription());
        table.setStatus(TableStatus.AVAILABLE); // Mặc định là trống

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    // --- READ ALL ---
    public List<TableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(TableResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // --- READ ONE ---
    public TableResponse getTableById(UUID id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        return TableResponse.fromEntity(table);
    }

    // --- UPDATE INFO (ADMIN) ---
    @Transactional
    public TableResponse updateTable(UUID id, UpdateTableRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        if (request.getName() != null) table.setName(request.getName());
        if (request.getCapacity() != null) table.setCapacity(request.getCapacity());
        if (request.getDescription() != null) table.setDescription(request.getDescription());
        if (request.getStatus() != null) table.setStatus(request.getStatus());

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    // --- NEW: UPDATE STATUS ONLY (STAFF/ADMIN) ---
    @Transactional
    public TableResponse updateTableStatus(UUID id, TableStatus status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        table.setStatus(status);

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    // --- DELETE ---
    @Transactional
    public void deleteTable(UUID id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        // Không xóa bàn đang có khách ngồi hoặc đã được đặt
        if (table.getStatus() == TableStatus.OCCUPIED || table.getStatus() == TableStatus.RESERVED) {
            throw new BadRequestException("Cannot delete table while it is OCCUPIED or RESERVED");
        }

        tableRepository.delete(table);
    }
}