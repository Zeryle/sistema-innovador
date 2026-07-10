package com.utp.myapp.whatsapp.infraestructure.persistence.jpa.mappers;

import com.utp.myapp.whatsapp.domain.model.aggregates.AutoReplyRule;
import com.utp.myapp.whatsapp.infraestructure.persistence.jpa.entities.AutoReplyRuleEntity;
import org.springframework.stereotype.Component;

@Component
public class AutoReplyRuleMapper {

    public AutoReplyRule toDomain(AutoReplyRuleEntity e) {
        if (e == null) return null;
        AutoReplyRule r = AutoReplyRule.create(e.getTenantId(), e.getKeyword(), e.getReplyText());
        r.setId(e.getId());
        r.setActive(e.isActive());
        return r;
    }

    public AutoReplyRuleEntity toEntity(AutoReplyRule r) {
        if (r == null) return null;
        return AutoReplyRuleEntity.builder()
                .keyword(r.getKeyword())
                .replyText(r.getReplyText())
                .active(r.isActive())
                .build();
    }
}
