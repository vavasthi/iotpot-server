package com.iotpot.server.pojos;

import org.joda.time.DateTime;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vinay on 1/28/16.
 */
public class Account extends Base {
  public Account() {
  }

  public Account(UUID id,
                 Tenant tenant,
                 DateTime createdAt,
                 DateTime updatedAt,
                 String createdBy,
                 String updatedBy,
                 String name,
                 String email,
                 String password,
                 Map<String, Session> sessionMap,
                 Set<String> remoteAddresses,
                 Set<IoTPotRole> ioTPotRoles,
                 Set<Appliance> appliances,
                 DataCenter dataCenter) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, name);
    this.tenant = tenant;
    this.email = email;
    this.password = password;
    this.sessionMap = sessionMap;
    this.remoteAddresses = remoteAddresses;
    this.ioTPotRoles = ioTPotRoles;
    this.appliances = appliances;
    this.dataCenter = dataCenter;
  }

  public Account(Tenant tenant,
                 String name,
                 String email,
                 String password,
                 Set<IoTPotRole> ioTPotRoles,
                 DataCenter dataCenter) {
    super(name);
    this.tenant = tenant;
    this.email = email;
    this.password = password;
    this.ioTPotRoles = ioTPotRoles;
    this.dataCenter = dataCenter;
  }

  public Account(Tenant tenant,
                 String name,
                 String email,
                 String password,
                 Set<IoTPotRole> ioTPotRoles) {
    super(name);
    this.tenant = tenant;
    this.email = email;
    this.password = password;
    this.ioTPotRoles = ioTPotRoles;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<IoTPotRole> getIoTPotRoles() {
    return ioTPotRoles;
  }

  public void setIoTPotRoles(Set<IoTPotRole> ioTPotRoles) {
    this.ioTPotRoles = ioTPotRoles;
  }

  public Set<String> getRemoteAddresses() {
    return remoteAddresses;
  }

  public void setRemoteAddresses(Set<String> remoteAddresses) {
    this.remoteAddresses = remoteAddresses;
  }

  public Map<String, Session> getSessionMap() {
    return sessionMap;
  }

  public void setSessionMap(Map<String, Session> sessionMap) {
    this.sessionMap = sessionMap;
  }

  public Set<Appliance> getAppliances() {
    return appliances;
  }

  public void setAppliances(final Set<Appliance> appliances) {
    this.appliances = appliances;
  }

  public DataCenter getDataCenter() {
    return dataCenter;
  }

  public void setDataCenter(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
  }

  @Override
  public boolean equals(final Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Account account = (Account) o;

    if (tenant != null ? !tenant.equals(account.tenant) : account.tenant != null) return false;
    return email != null ? email.equals(account.email) : account.email == null;

  }

  @Override
  public int hashCode() {
    int result = tenant != null ? tenant.hashCode() : 0;
    result = 31 * result + (email != null ? email.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Account{" +
        "tenant=" + tenant +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        ", ioTPotRoles=" + ioTPotRoles +
        ", remoteAddresses=" + remoteAddresses +
        ", sessionMap=" + sessionMap +
        ", appliances=" + appliances +
        ", dataCenter=" + dataCenter +
        "} " + super.toString();
  }

  private Tenant tenant;
  private String email;
  private String password;
  private Set<IoTPotRole> ioTPotRoles;
  private Set<String> remoteAddresses;
  private Map<String, Session> sessionMap;
  private Set<Appliance> appliances;
  private DataCenter dataCenter;
}
