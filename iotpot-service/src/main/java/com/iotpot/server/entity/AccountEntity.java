package com.iotpot.server.entity;

import com.iotpot.server.common.annotations.SkipPatching;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "accounts",
    indexes = {
        @Index(name = "accounts_email_index", columnList = "email", unique = true),
        @Index(name = "accounts_name_index", columnList = "name", unique = true)
    }
)
public class AccountEntity extends ActorEntity {

  public AccountEntity(TenantEntity tenant,
                       String name,
                       String email,
                       String password,
                       Set<String> remoteAddresses,
                       Set<RoleEntity> roles,
                       DataCenterEntity dataCenterEntity) {

    super(tenant, name, remoteAddresses, roles);
    this.email = email;
    this.password = password;
    this.dataCenterEntity = dataCenterEntity;
  }

  public AccountEntity() {

  }

  @SkipPatching
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Map<String, SessionEntity> getSessionMap() {
    return sessionMap;
  }

  public void setSessionMap(Map<String, SessionEntity> sessionMap) {
    this.sessionMap = sessionMap;
  }

  public Set<ApplianceEntity> getDevices() {
    return devices;
  }

  public void setDevices(final Set<ApplianceEntity> devices) {
    this.devices = devices;
  }

  public DataCenterEntity getDataCenterEntity() {
    return dataCenterEntity;
  }

  public void setDataCenterEntity(DataCenterEntity dataCenterEntity) {
    this.dataCenterEntity = dataCenterEntity;
  }


  private String email;
  private String password;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @MapKey(name = "applicationId")
  private Map<String, SessionEntity> sessionMap;

  @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<ApplianceEntity> devices;

  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH,
          CascadeType.PERSIST,
          CascadeType.MERGE,
          CascadeType.REFRESH})
  private DataCenterEntity dataCenterEntity;

}
