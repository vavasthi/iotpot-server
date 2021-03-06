package com.iotpot.server.pojos;

import java.io.Serializable;
import java.util.UUID;

public class DeviceAuthentication implements Serializable {

  public DeviceAuthentication(final UUID accountId, final String deviceRegistrationId) {
    this.accountId = accountId;
    this.deviceRegistrationId = deviceRegistrationId;
  }

  public DeviceAuthentication() {
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(final UUID accountId) {
    this.accountId = accountId;
  }

  public String getDeviceRegistrationId() {
    return deviceRegistrationId;
  }

  public void setDeviceRegistrationId(final String deviceRegistrationId) {
    this.deviceRegistrationId = deviceRegistrationId;
  }

  private UUID accountId;
  private String deviceRegistrationId;
}
