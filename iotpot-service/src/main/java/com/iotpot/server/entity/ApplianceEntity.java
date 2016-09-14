package com.iotpot.server.entity;

import javax.persistence.*;
import java.util.HashSet;

@Entity
@Table(name = "devices",
    indexes = {
    }
)
public class ApplianceEntity extends ActorEntity {

  public ApplianceEntity(String deviceId,
                         AccountEntity accountEntity) {

    super(accountEntity.getTenant(),
            deviceId,
            new HashSet<>(),
            new HashSet<>());

    this.deviceId = deviceId;
    this.accountEntity = accountEntity;
  }

  public ApplianceEntity() {

  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(final String deviceId) {
    this.deviceId = deviceId;
  }

  public SessionEntity getSessionEntity() {
    return sessionEntity;
  }

  public void setSessionEntity(final SessionEntity sessionEntity) {
    this.sessionEntity = sessionEntity;
  }

  public AccountEntity getAccountEntity() {
    return accountEntity;
  }

  public void setAccountEntity(final AccountEntity accountEntity) {
    this.accountEntity = accountEntity;
  }

  private String deviceId;

  @OneToOne(orphanRemoval = true, fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "actorEntity")
  private SessionEntity sessionEntity;

  @OneToOne(fetch=FetchType.EAGER)
  private AccountEntity accountEntity;
}
