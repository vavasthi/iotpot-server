/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.pojos;

import com.iotpot.server.common.annotations.IoTPotUrlString;
import org.joda.time.DateTime;

import java.util.UUID;

public class DataCenter extends Base {
    public DataCenter() {
    }

    public DataCenter(UUID id,
                      DateTime createdAt,
                      DateTime updatedAt,
                      String createdBy,
                      String updatedBy,
                      String name,
                      String identityURL,
                      String apiEndpointURL,
                      String csEndpointURL,
                      String stunURL,
                      String mqttURL,
                      String ntpURL,
                      Long userCount) {

        super(id, createdAt, updatedAt, createdBy, updatedBy, name);
        this.identityURL = identityURL;
        this.apiEndpointURL = apiEndpointURL;
        this.csEndpointURL = csEndpointURL;
        this.stunURL = stunURL;
        this.mqttURL = mqttURL;
        this.ntpURL = ntpURL;
        this.userCount = userCount;
    }

    public DataCenter(String name,
                      String identityURL,
                      String apiEndpointURL,
                      String csEndpointURL,
                      String stunURL,
                      String mqttURL,
                      String ntpURL,
                      Long userCount) {

        super(name);
        this.identityURL = identityURL;
        this.apiEndpointURL = apiEndpointURL;
        this.csEndpointURL = csEndpointURL;
        this.stunURL = stunURL;
        this.mqttURL = mqttURL;
        this.ntpURL = ntpURL;
        this.userCount = userCount;
    }

    public String getIdentityURL() {
        return identityURL;
    }

    public void setIdentityURL(String identityURL) {
        this.identityURL = identityURL;
    }

    public String getApiEndpointURL() {
        return apiEndpointURL;
    }

    public void setApiEndpointURL(String apiEndpointURL) {
        this.apiEndpointURL = apiEndpointURL;
    }

    public String getCsEndpointURL() {
        return csEndpointURL;
    }

    public void setCsEndpointURL(String csEndpointURL) {
        this.csEndpointURL = csEndpointURL;
    }

    public String getStunURL() {
        return stunURL;
    }

    public void setStunURL(String stunURL) {
        this.stunURL = stunURL;
    }

    public String getMqttURL() {
        return mqttURL;
    }

    public void setMqttURL(String mqttURL) {
        this.mqttURL = mqttURL;
    }

    public String getNtpURL() {
        return ntpURL;
    }

    public void setNtpURL(String ntpURL) {
        this.ntpURL = ntpURL;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    private @IoTPotUrlString
    String identityURL;
    private @IoTPotUrlString
    String apiEndpointURL;
    private @IoTPotUrlString
    String csEndpointURL;
    private @IoTPotUrlString
    String stunURL;
    private @IoTPotUrlString
    String mqttURL;
    private @IoTPotUrlString
    String ntpURL;
    private Long userCount;
}
