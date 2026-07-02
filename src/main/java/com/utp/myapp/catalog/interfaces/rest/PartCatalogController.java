package com.utp.myapp.catalog.interfaces.rest;

import com.utp.myapp.catalog.domain.model.aggregates.PartCategory;
import com.utp.myapp.catalog.domain.model.repository.IPartCategoryRepository;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import com.utp.myapp.shared.infraestructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/catalog/parts")
@RequiredArgsConstructor
public class PartCatalogController {

    private final IPartCategoryRepository repository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<Map<String, Object>> result = repository.findAll().stream()
                .map(this::toMap)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(p -> ResponseEntity.ok(ApiResponse.ok(toMap(p))))
                .orElseThrow(() -> new EntityNotFoundException("PartCategory", id));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> search(@RequestParam String query) {
        List<Map<String, Object>> result = repository.searchByName(query).stream()
                .map(this::toMap)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        PartCategory pc = new PartCategory.Builder()
                .name((String) body.get("name"))
                .description((String) body.getOrDefault("description", ""))
                .parentCategoryId(body.get("parentCategoryId") != null ? ((Number) body.get("parentCategoryId")).longValue() : null)
                .build();
        PartCategory saved = repository.save(pc);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(toMap(saved)));
    }

    private Map<String, Object> toMap(PartCategory pc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", pc.getId());
        m.put("name", pc.getName());
        m.put("description", pc.getDescription());
        m.put("parentCategoryId", pc.getParentCategoryId());
        m.put("imageUrl", pc.getImageUrl());
        return m;
    }
}
