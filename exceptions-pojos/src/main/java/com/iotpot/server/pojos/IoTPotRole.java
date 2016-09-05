/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.pojos;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by vinay on 1/28/16.
 */
public class IoTPotRole implements GrantedAuthority {
    private String authority;

    public IoTPotRole(String authority) {
        this.authority = authority;
    }

    public IoTPotRole() {
    }

    @Override
    public String toString() {
        return "IoTPotRole{" +
                "authority='" + authority + '\'' +
                '}';
    }

    @Override
    @JsonGetter("role")
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IoTPotRole ioTPotRole = (IoTPotRole) o;

        return authority != null ? authority.equals(ioTPotRole.authority) : ioTPotRole.authority == null;

    }

    @Override
    public int hashCode() {
        return authority != null ? authority.hashCode() : 0;
    }
}
