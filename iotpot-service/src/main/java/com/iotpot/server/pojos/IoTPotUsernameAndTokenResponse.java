/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.pojos;

import java.io.Serializable;

public class IoTPotUsernameAndTokenResponse implements Serializable {
  public IoTPotUsernameAndTokenResponse(String tenant, String username, IoTPotTokenResponse response) {
    this.username = username;
    this.tenant = tenant;
    this.response = response;
  }

  public IoTPotUsernameAndTokenResponse() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTenant() {
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  public IoTPotTokenResponse getResponse() {
    return response;
  }

  public void setResponse(IoTPotTokenResponse response) {
    this.response = response;
  }

  private String username;
  private String tenant;
  private IoTPotTokenResponse response;

}
