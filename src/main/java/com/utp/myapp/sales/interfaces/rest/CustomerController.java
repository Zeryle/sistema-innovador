package com.utp.myapp.sales.interfaces.rest;

import com.utp.myapp.sales.application.command.CreateCustomerCommand;
import com.utp.myapp.sales.application.command.DeleteCustomerCommand;
import com.utp.myapp.sales.application.command.UpdateCustomerCommand;
import com.utp.myapp.sales.application.dto.CustomerDto;
import com.utp.myapp.sales.application.dto.CustomerSummaryDto;
import com.utp.myapp.sales.application.handler.*;
import com.utp.myapp.sales.application.query.GetCustomerByIdQuery;
import com.utp.myapp.sales.application.query.SearchCustomersQuery;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerCommandHandler createHandler;
    private final UpdateCustomerCommandHandler updateHandler;
    private final DeleteCustomerCommandHandler deleteHandler;
    private final GetCustomerByIdQueryHandler getByIdHandler;
    private final SearchCustomersQueryHandler searchHandler;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> create(@RequestBody CreateCustomerCommand command) {
        CustomerDto result = createHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> update(
            @PathVariable Integer id,
            @RequestBody UpdateCustomerCommand command) {
        UpdateCustomerCommand enriched = UpdateCustomerCommand.builder()
                .id(id)
                .name(command.getName())
                .lastName(command.getLastName())
                .dni(command.getDni())
                .email(command.getEmail())
                .phone(command.getPhone())
                .notes(command.getNotes())
                .address(command.getAddress())
                .build();
        CustomerDto result = updateHandler.handle(enriched);
        return ResponseEntity.ok(ApiResponse.ok(result, "Customer updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        deleteHandler.handle(DeleteCustomerCommand.builder().id(id).build());
        return ResponseEntity.ok(ApiResponse.ok(null, "Customer deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getById(@PathVariable Integer id) {
        CustomerDto result = getByIdHandler.handle(
                GetCustomerByIdQuery.builder().id(id).build());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerSummaryDto>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<CustomerSummaryDto> results = searchHandler.handle(
                SearchCustomersQuery.builder()
                        .query(query)
                        .page(page)
                        .size(size)
                        .build());
        return ResponseEntity.ok(ApiResponse.ok(results));
    }
}
