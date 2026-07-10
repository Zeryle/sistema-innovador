package com.utp.myapp.whatsapp.domain.model.repository;

import com.utp.myapp.whatsapp.domain.model.aggregates.AutoReplyRule;

import java.util.List;
import java.util.Optional;

public interface IAutoReplyRuleRepository {
    AutoReplyRule save(AutoReplyRule rule);
    Optional<AutoReplyRule> findById(Long id);
    List<AutoReplyRule> findActiveByTenantId(String tenantId);
    void delete(Long id);
}
