package com.iotpot.server.pojos;

import org.joda.time.DateTime;

import java.util.Set;
import java.util.UUID;

/**
 * Created by vinay on 3/7/16.
 */
public class Appliance extends Base {

  public Appliance(UUID id,
                   DateTime createdAt,
                   DateTime updatedAt,
                   String createdBy,
                   String updatedBy,
                   String deviceRegistrationId,
                   Set<IoTPotRole> ioTPotRoles) {
    super(id, createdAt,  updatedAt,  createdBy,  updatedBy,  deviceRegistrationId);
    this.deviceRegistrationId = deviceRegistrationId;
    this.ioTPotRoles = ioTPotRoles;
  }
  public Appliance(String deviceRegistrationId,
                   Set<IoTPotRole> ioTPotRoles) {
    super(null, null,  null,  null,  null,  deviceRegistrationId);
    this.deviceRegistrationId = deviceRegistrationId;
    this.ioTPotRoles = ioTPotRoles;
  }

  public Appliance() {

  }
  public String getDeviceRegistrationId() {
    return deviceRegistrationId;
  }

  public void setDeviceRegistrationId(final String deviceRegistrationId) {
    this.deviceRegistrationId = deviceRegistrationId;
  }

  public Set<IoTPotRole> getIoTPotRoles() {
    return ioTPotRoles;
  }

  public void setIoTPotRoles(final Set<IoTPotRole> ioTPotRoles) {
    this.ioTPotRoles = ioTPotRoles;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(final Session session) {
    this.session = session;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(final UUID accountId) {
    this.accountId = accountId;
  }

  @Override
  public boolean equals(final Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) return true;
    if (!(o instanceof Appliance)) return false;
    if (!super.equals(o)) return false;

    final Appliance appliance = (Appliance) o;

    if (deviceRegistrationId != null ? !deviceRegistrationId.equals(appliance.deviceRegistrationId) : appliance.deviceRegistrationId != null)
      return false;
    return accountId != null ? accountId.equals(appliance.accountId) : appliance.accountId == null;

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (deviceRegistrationId != null ? deviceRegistrationId.hashCode() : 0);
    result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
    return result;
  }

  private String deviceRegistrationId;
  private Set<IoTPotRole> ioTPotRoles;
  private Session session;
  private UUID accountId;
}
