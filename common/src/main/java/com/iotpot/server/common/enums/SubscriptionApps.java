/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.enums;

import com.iotpot.server.pojos.constants.IoTPotConstants;

import java.util.List;


/**
 * Created by praveen on 12/4/16.
 */

public enum SubscriptionApps {

    GCM(IoTPotConstants.STR_NOTIFICATION_TYPE_GCM, IoTPotConstants.SUBSCRIPTION_GCM_APPS),
    APNS(IoTPotConstants.STR_NOTIFICATION_TYPE_APNS, IoTPotConstants.SUBSCRIPTION_APNS_APPS);

    private final String appType;
    private final List<String> appIdList;


    SubscriptionApps(String appType, List<String> appIdList) {
        this.appType = appType;
        this.appIdList = appIdList;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public List<String> getValue() {
        return appIdList;
    }
}
