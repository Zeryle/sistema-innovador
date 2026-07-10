package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.adapters;

import com.utp.myapp.whatsapp.domain.model.aggregates.AutoReplyRule;
import com.utp.myapp.whatsapp.domain.model.repository.IAutoReplyRuleRepository;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.mappers.AutoReplyRuleMapper;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.entities.AutoReplyRuleEntity;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.repositories.JPAAutoReplyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AutoReplyRuleRepositoryAdapter implements IAutoReplyRuleRepository {

    private final JPAAutoReplyRuleRepository jpa;
    private final AutoReplyRuleMapper mapper;

    @Override
    public AutoReplyRule save(AutoReplyRule rule) {
        AutoReplyRuleEntity e = mapper.toEntity(rule);
        e.setTenantId(rule.getTenantId());
        e.setCreatedAt(rule.getCreatedAt());
        e.setUpdatedAt(rule.getUpdatedAt());
        AutoReplyRuleEntity saved = jpa.save(e);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AutoReplyRule> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AutoReplyRule> findActiveByTenantId(String tenantId) {
        return jpa.findByTenantIdAndActiveTrueOrderByCreatedAtDesc(tenantId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
