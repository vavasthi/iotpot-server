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
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.Date;

@Entity
@Table(name = "sessions")
public class SessionEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  private ActorEntity actorEntity;

  private String authToken;
  private String applicationId;
  private String remoteAddress;
  @Column(columnDefinition = "DATETIME")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime expiry;
  private int sessionType;

  public SessionEntity(String authToken,
                       String remoteAddress,
                       String applicationId,
                       ActorEntity actorEntity,
                       int sessionType) throws DatatypeConfigurationException {
    super(authToken);
    this.authToken = authToken;
    this.remoteAddress = remoteAddress;
    this.applicationId = applicationId;
    this.actorEntity = actorEntity;
    expiry = new DateTime(new Date().getTime() + (7L * 24 * 60 * 60 * 1000));
    this.sessionType = sessionType;
  }

  public SessionEntity() {

  }

  public ActorEntity getActorEntity() {
    return actorEntity;
  }

  public void setActorEntity(ActorEntity actorEntity) {
    this.actorEntity = actorEntity;
  }

  public String getAuthToken() {
    return authToken;
  }

  /**
   * This method allows setting of a new authtoken into the session. The expiry is reset every time the auth token it set.
   * @param authToken
   */
  public void setAuthToken(String authToken) {
    this.authToken = authToken;
    expiry = new DateTime(new Date().getTime() + (7L * 24 * 60 * 60 * 1000));
  }

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public DateTime getExpiry() {
    return expiry;
  }

  public void setExpiry(final DateTime expiry) {
    this.expiry = expiry;
  }

  public int getSessionType() {
    return sessionType;
  }

  public void setSessionType(final int sessionType) {
    this.sessionType = sessionType;
  }
}
