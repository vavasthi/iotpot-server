/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.pojos;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.iotpot.server.common.annotations.IoTPotNonNullString;
import com.iotpot.server.serializers.IoTPotDateTimeDeserializer;
import com.iotpot.server.serializers.IoTPotDateTimeSerializer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public class Base implements Serializable {
  public Base(UUID id, DateTime createdAt, DateTime updatedAt, String createdBy, String updatedBy, String name) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    this.name = name;
  }

  public Base() {
  }

  public Base(String name) {
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(DateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Base base = (Base) o;

    if (id != null ? !id.equals(base.id) : base.id != null) return false;
    return name != null ? name.equals(base.name) : base.name == null;

  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Base{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", createdBy='" + createdBy + '\'' +
        ", updatedBy='" + updatedBy + '\'' +
        ", name='" + name + '\'' +
        '}';
  }

  private UUID id;
  @JsonSerialize(using = IoTPotDateTimeSerializer.class)
  @JsonDeserialize(using = IoTPotDateTimeDeserializer.class)
  private DateTime createdAt;
  @JsonSerialize(using = IoTPotDateTimeSerializer.class)
  @JsonDeserialize(using = IoTPotDateTimeDeserializer.class)
  private DateTime updatedAt;
  private String createdBy;
  private String updatedBy;
  private @IoTPotNonNullString(min = 5, max = 255, nullAllowed = false) String name;
}
