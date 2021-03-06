package com.iotpot.server.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.iotpot.server.security.token.IoTPotTokenPrincipal;
import com.iotpot.server.serializers.IoTPotDateTimeDeserializer;
import com.iotpot.server.serializers.IoTPotDateTimeSerializer;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.Serializable;
import java.util.Collection;

public class IoTPotTokenResponse implements Serializable {

  @JsonProperty
  private String authToken;
  private IoTPotTokenPrincipal.TOKEN_TYPE tokenType;
  private DataCenter dataCenter;
  @JsonSerialize(using = IoTPotDateTimeSerializer.class)
  @JsonDeserialize(using = IoTPotDateTimeDeserializer.class)
  private DateTime expiry;
  private Collection<IoTPotRole> ioTPotRoles;

  public IoTPotTokenResponse() {
  }

  public IoTPotTokenResponse(String authToken,
                             IoTPotTokenPrincipal.TOKEN_TYPE tokenType,
                             DataCenter dataCenter,
                             DateTime expiry,
                             Collection<IoTPotRole> ioTPotRoles) throws DatatypeConfigurationException {
    this.authToken = authToken;
    this.tokenType = tokenType;
    this.dataCenter = dataCenter;
    this.ioTPotRoles = ioTPotRoles;
    this.expiry = expiry;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public IoTPotTokenPrincipal.TOKEN_TYPE getTokenType() {
    return tokenType;
  }

  public void setTokenType(final IoTPotTokenPrincipal.TOKEN_TYPE tokenType) {
    this.tokenType = tokenType;
  }

  public DateTime getExpiry() {
    return expiry;
  }

  public void setExpiry(DateTime expiry) {
    this.expiry = expiry;
  }

  public Collection<IoTPotRole> getIoTPotRoles() {
    return ioTPotRoles;
  }

  public void setIoTPotRoles(Collection<IoTPotRole> ioTPotRoles) {
    this.ioTPotRoles = ioTPotRoles;
  }

  public DataCenter getDataCenter() {
    return dataCenter;
  }

  public void setDataCenter(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
  }
}