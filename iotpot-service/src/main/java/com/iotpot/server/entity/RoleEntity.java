package com.iotpot.server.entity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


@Entity
@Table(name = "roles",
        indexes = {
                @Index(name = "role_name_index",  columnList="name", unique = true)
        }
)
public class RoleEntity extends BaseEntity {
    public RoleEntity(String name) {
        super(name);
    }
    public RoleEntity() {

    }
}
