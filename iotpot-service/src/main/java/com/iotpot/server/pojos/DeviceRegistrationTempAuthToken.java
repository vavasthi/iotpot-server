package com.iotpot.server.pojos;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by vinay on 3/4/16.
 */
public class DeviceRegistrationTempAuthToken extends Base {

  public DeviceRegistrationTempAuthToken(final String deviceRegistrationId, final String tempAuthToken, final DateTime expiry) {
    super(deviceRegistrationId);
    this.deviceRegistrationId = deviceRegistrationId;
    this.tempAuthToken = tempAuthToken;
    this.expiry = expiry;
  }
  public DeviceRegistrationTempAuthToken(UUID id,
                                         Account account,
                                         Appliance appliance,
                                         DateTime createdAt,
                                         DateTime updatedAt,
                                         String createdBy,
                                         String updatedBy,
                                         final String deviceRegistrationId,
                                         final String tempAuthToken,
                                         final DateTime expiry) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, deviceRegistrationId);
    this.account = account;
    this.appliance = appliance;
    this.deviceRegistrationId = deviceRegistrationId;
    this.tempAuthToken = tempAuthToken;
    this.expiry = expiry;
  }

  public DeviceRegistrationTempAuthToken() {
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(final Account account) {
    this.account = account;
  }

  public Appliance getAppliance() {
    return appliance;
  }

  public void setAppliance(final Appliance appliance) {
    this.appliance = appliance;
  }

  public String getDeviceRegistrationId() {
    return deviceRegistrationId;
  }

  public void setDeviceRegistrationId(final String deviceRegistrationId) {
    this.deviceRegistrationId = deviceRegistrationId;
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

  private Account account;
  private Appliance appliance;
  private String deviceRegistrationId;
  private String tempAuthToken;
  private DateTime expiry;
}
