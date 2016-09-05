/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.exception;

/**
 * The type Failed dependency exception.
 *
 * @author nikhilvs
 */
public class FailedDependencyException extends IoTPotBaseException {

    /**
     * Instantiates a new Failed dependency exception.
     *
     * @param message the message
     */
    public FailedDependencyException(String message) {
    super(message);
  }

    /**
     * Instantiates a new Failed dependency exception.
     *
     * @param message the message
     * @param t       the t
     */
    public FailedDependencyException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Instantiates a new Failed dependency exception.
     *
     * @param message the message
     * @param code    the code
     */
    public FailedDependencyException(String message, Integer code) {
    super(code,message);
 }

}
