/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.exception;

/**
 * Created by maheshsapre on 03/02/16.
 */
public class CameraServiceException extends IoTPotBaseException {

  /**
   * Instantiates a new Camera service exception.
   *
   * @param message the message
   */
  public CameraServiceException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Camera service exception.
   *
   * @param message the message
   * @param ex      the ex
   */
  public CameraServiceException(String message, Throwable ex) {
    super(message, ex);

  }
}

