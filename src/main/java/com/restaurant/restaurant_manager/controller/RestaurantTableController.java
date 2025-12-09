package com.restaurant.restaurant_manager.controller;

import com.restaurant.restaurant_manager.dto.response.ApiResponse;
import com.restaurant.restaurant_manager.dto.table.CreateTableRequest;
import com.restaurant.restaurant_manager.dto.table.TableResponse;
import com.restaurant.restaurant_manager.dto.table.UpdateTableRequest;
import com.restaurant.restaurant_manager.entity.enums.TableStatus;
import com.restaurant.restaurant_manager.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    /**
     * Lấy danh sách bàn (Admin/Staff xem để xếp bàn)
     */
    @GetMapping("/staff/tables")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAllTables() {
        return ApiResponse.success(tableService.getAllTables(), "Tables retrieved successfully");
    }

    /**
     * Lấy chi tiết bàn
     */
    @GetMapping("/staff/tables/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> getTableById(@PathVariable UUID id) {
        return ApiResponse.success(tableService.getTableById(id), "Table details retrieved");
    }

    /**
     * Tạo bàn mới (Chỉ Admin)
     */
    @PostMapping("/admin/tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> createTable(
            @Valid @RequestBody CreateTableRequest request
    ) {
        return ApiResponse.created(tableService.createTable(request), "Table created successfully");
    }

    /**
     * Cập nhật thông tin bàn (Chỉ Admin)
     */
    @PutMapping("/admin/tables/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> updateTable(
            @PathVariable UUID id,
            @RequestBody UpdateTableRequest request
    ) {
        return ApiResponse.success(tableService.updateTable(id, request), "Table updated successfully");
    }

    /**
     * NEW API: Cập nhật TRẠNG THÁI bàn (Staff cũng làm được)
     * Ví dụ: Chuyển sang MAINTENANCE khi hỏng, hoặc AVAILABLE khi dọn xong.
     * PUT /api/v1/staff/tables/{id}/status?status=MAINTENANCE
     */
    @PutMapping("/staff/tables/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> updateTableStatus(
            @PathVariable UUID id,
            @RequestParam TableStatus status
    ) {
        TableResponse response = tableService.updateTableStatus(id, status);
        return ApiResponse.success(response, "Table status updated successfully");
    }

    /**
     * Xóa bàn (Chỉ Admin)
     */
    @DeleteMapping("/admin/tables/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteTable(@PathVariable UUID id) {
        tableService.deleteTable(id);
        return ApiResponse.success(null, "Table deleted successfully");
    }
}