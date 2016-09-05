/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.entity;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by vinay on 1/28/16.
 */
@Entity
@Table(name = "appliance_reg_temp_auth_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_applianceEntity", columnNames = "appliance_entity"),
                @UniqueConstraint(name = "UK_tempAuthToken", columnNames = "tempAuthToken")

        }
)
public class ApplianceRegistrationTempAuthTokenEntity extends BaseEntity {

  private final int TEMP_AUTH_TOKEN_EXPIRY_MILLISECONDS = 30 * 60 * 1000;

  public ApplianceRegistrationTempAuthTokenEntity(AccountEntity accountEntity,
                                                  ApplianceEntity applianceEntity,
                                                  String tempAuthToken) {

    super(applianceEntity.getDeviceId());
    this.accountEntity = accountEntity;
    this.applianceEntity = applianceEntity;
    this.tempAuthToken = tempAuthToken;
    expiry = new DateTime(new Date().getTime() + (TEMP_AUTH_TOKEN_EXPIRY_MILLISECONDS));
  }

  public ApplianceRegistrationTempAuthTokenEntity() {
  }

  public String getTempAuthToken() {
    return tempAuthToken;
  }

  public void setTempAuthToken(final String tempAuthToken) {
    this.tempAuthToken = tempAuthToken;
  }

  public DateTime getExpiry() {
    return expiry;
  }

  public void setExpiry(final DateTime expiry) {
    this.expiry = expiry;
  }

  public AccountEntity getAccountEntity() {
    return accountEntity;
  }

  public void setAccountEntity(final AccountEntity accountEntity) {
    this.accountEntity = accountEntity;
  }

  public ApplianceEntity getApplianceEntity() {
    return applianceEntity;
  }

  public void setApplianceEntity(final ApplianceEntity applianceEntity) {
    this.applianceEntity = applianceEntity;
  }

  @OneToOne(fetch=FetchType.LAZY)
  private AccountEntity accountEntity;
  @OneToOne(fetch=FetchType.LAZY)
  private ApplianceEntity applianceEntity;
  private String tempAuthToken;
  @Column(columnDefinition = "DATETIME")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime expiry;
}
