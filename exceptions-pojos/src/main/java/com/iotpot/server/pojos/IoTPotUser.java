package com.iotpot.server.pojos;

/**
 * Created by vinay on 2/3/16.
 */
public class IoTPotUser {
    public IoTPotUser(String tenant, String username) {
        this.tenant = tenant;
        this.username = username;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    private String tenant;
    private String username;
}