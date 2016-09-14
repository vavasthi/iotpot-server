package com.iotpot.server.entity;

import javax.persistence.*;

@Entity
@Table(name = "admins",
        indexes = {
                @Index(name = "admins_email_index",  columnList="adminEmail", unique = true),
                @Index(name = "admins_name_index",  columnList="name", unique = true)
        }
)
public class AdminEntity extends BaseEntity {
    public AdminEntity(String name, String adminEmail, String password) {
        super(name);
        this.adminEmail = adminEmail;
        this.password = password;
    }
    public AdminEntity() {

    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String email) {
        this.adminEmail = email;
    }

    private String adminEmail;
    private String password;
}
