/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by vavasthi on 27/1/16.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class TenantMismatchException extends IoTPotBaseException {

  /**
   * The Code.
   */
  Integer code;

  /**
   * Instantiates a new Tenant mismatch exception.
   */
  public TenantMismatchException(){
    super();
  }

  /**
   * Instantiates a new Tenant mismatch exception.
   *
   * @param message the message
   */
  public TenantMismatchException(String message){
    super(message);
  }

  /**
   * Instantiates a new Tenant mismatch exception.
   *
   * @param message the message
   * @param code    the code
   */
  public TenantMismatchException(String message, Integer code){
    super(message);
    this.code=code;
  }

  /**
   * Gets code.
   *
   * @return the code
   */
  public Integer getCode() {
    return code;
  }
}
