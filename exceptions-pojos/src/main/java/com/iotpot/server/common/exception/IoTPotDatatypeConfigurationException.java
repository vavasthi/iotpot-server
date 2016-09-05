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
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class IoTPotDatatypeConfigurationException extends IoTPotBaseException {

    /**
     * Instantiates a new H 2 o datatype configuration exception.
     *
     * @param throwable the throwable
     */
    public IoTPotDatatypeConfigurationException(Throwable throwable) {
    super(throwable);
  }

}
