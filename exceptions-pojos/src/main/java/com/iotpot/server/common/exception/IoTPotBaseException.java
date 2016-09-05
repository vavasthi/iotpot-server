/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.exception;

/**
 * Created by vinay on 1/11/16.
 */
public class IoTPotBaseException extends RuntimeException {

  private int errorCode;
  private boolean shouldLog = true;

  /**
   * Instantiates a new Hubble base exception.
   */
  public IoTPotBaseException() {
    super("");
  }

  /**
   * Instantiates a new Hubble base exception.
   *
   * @param errorCode the error code
   * @param message   the message
   */
  public IoTPotBaseException(int errorCode, String message) {
    super(message);
    this.errorCode=errorCode;
  }

  /**
   * Instantiates a new Hubble base exception.
   *
   * @param message the message
   */
  public IoTPotBaseException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Hubble base exception.
   *
   * @param cause the cause
   */
  public IoTPotBaseException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new Hubble base exception.
   *
   * @param message the message
   * @param cause   the cause
   */
  public IoTPotBaseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Hubble base exception.
   *
   * @param message            the message
   * @param cause              the cause
   * @param enableSuppression  the enable suppression
   * @param writableStackTrace the writable stack trace
   */
  public IoTPotBaseException(String message, Throwable cause,
                             boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * Gets error code.
   *
   * @return the error code
   */
  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public boolean shouldLog() {
    return shouldLog;
  }
  public void shouldLog(boolean shouldLog) {
    this.shouldLog = shouldLog;
  }
}
