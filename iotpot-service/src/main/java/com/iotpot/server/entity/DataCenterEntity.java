/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "data_center")
public class DataCenterEntity extends BaseEntity {

  public DataCenterEntity(String name,
                          String identityURL,
                          String apiEndpointURL,
                          String csEndpointURL,
                          String stunURL,
                          String mqttURL,
                          String ntpURL) {
    super(name);
    this.identityURL = identityURL;
    this.apiEndpointURL = apiEndpointURL;
    this.csEndpointURL = csEndpointURL;
    this.stunURL = stunURL;
    this.mqttURL = mqttURL;
    this.ntpURL = ntpURL;
    this.userCount = 0L;
  }

  public DataCenterEntity() {
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

  public Set<TenantEntity> getTenants() {
    return tenants;
  }

  public void setTenants(Set<TenantEntity> tenants) {
    this.tenants = tenants;
  }

  public synchronized void incrementUserCount() {
    ++(this.userCount);
  }

  public synchronized void decrementUserCount() {
    --(this.userCount);
  }


  private String identityURL;
  private String apiEndpointURL;
  private String csEndpointURL;
  private String stunURL;
  private String mqttURL;
  private String ntpURL;
  private Long userCount;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "datacenters")
  private Set<TenantEntity> tenants;
}
