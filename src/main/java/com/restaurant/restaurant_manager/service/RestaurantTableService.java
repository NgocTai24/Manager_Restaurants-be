package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.dto.table.CreateTableRequest;
import com.restaurant.restaurant_manager.dto.table.TableResponse;
import com.restaurant.restaurant_manager.dto.table.UpdateTableRequest;
import com.restaurant.restaurant_manager.entity.RestaurantTable;
import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import com.restaurant.restaurant_manager.exception.BadRequestException;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    @Transactional
    public TableResponse createTable(CreateTableRequest request) {

        RestaurantTable table = new RestaurantTable();
        table.setName(request.getName());
        table.setCapacity(request.getCapacity());
        table.setDescription(request.getDescription());
        table.setStatus(TableStatus.AVAILABLE);

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    public PageResponse<TableResponse> getAllTables(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<RestaurantTable> tablePage = tableRepository.findAll(pageable);

        List<TableResponse> content = tablePage.getContent().stream()
                .map(TableResponse::fromEntity)
                .collect(Collectors.toList());
        return PageResponse.<TableResponse>builder()
                .content(content)
                .pageNo(tablePage.getNumber())
                .pageSize(tablePage.getSize())
                .totalElements(tablePage.getTotalElements())
                .totalPages(tablePage.getTotalPages())
                .last(tablePage.isLast())
                .build();
    }


    public TableResponse getTableById(UUID id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        return TableResponse.fromEntity(table);
    }


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

    @Transactional
    public TableResponse updateTableStatus(UUID id, TableStatus status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        table.setStatus(status);

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    @Transactional
    public void deleteTable(UUID id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        if (table.getStatus() == TableStatus.OCCUPIED || table.getStatus() == TableStatus.RESERVED) {
            throw new BadRequestException("Cannot delete table while it is OCCUPIED or RESERVED");
        }

        tableRepository.delete(table);
    }
}