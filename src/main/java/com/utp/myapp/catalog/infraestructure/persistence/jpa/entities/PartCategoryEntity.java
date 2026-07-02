package com.utp.myapp.catalog.infraestructure.persistence.jpa.entities;

import com.utp.myapp.shared.infraestructure.persistence.jpa.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "part_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartCategoryEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "parent_category_id")
    private Long parentCategoryId;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
