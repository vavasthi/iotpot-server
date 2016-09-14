/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.enums;

/**
 * Created by maheshsapre on 04/04/16.
 */
public enum Role {
    USER("user"), // 0
    FACTORY_USER("factory_user"), // 7
    TESTER("tester"), // 8
    FW_UPGRADE_ADMIN("fw_upgrade_admin"), // 10
    POLICY_ADMIN("policy_admin"), // 11
    TENANT_ADMIN("tenant_admin"), // 12
    ADMIN("admin"), // 13
    DEVICE("device"); // 14

    private final String value;
    private final String role;

    Role(String value) {
        this.value = value;
        this.role = "\"hasRole('" + value + "')\")";
    }

    public static Role createFromString(String value) {
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(value)) {
                return r;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid value");
    }

    public final String getValue() {
        return value;
    }

    public final String getRole() {
        return role;
    }

}
