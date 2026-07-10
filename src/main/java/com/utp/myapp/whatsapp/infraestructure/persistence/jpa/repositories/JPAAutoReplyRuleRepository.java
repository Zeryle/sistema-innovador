package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.repositories;

import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.entities.AutoReplyRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPAAutoReplyRuleRepository extends JpaRepository<AutoReplyRuleEntity, Long> {
    List<AutoReplyRuleEntity> findByTenantIdAndActiveTrueOrderByCreatedAtDesc(String tenantId);
    List<AutoReplyRuleEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
