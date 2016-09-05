/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */


package com.iotpot.server.common.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * Created by vinay on 6/17/16.
 */
public class BlackListedDueToRepeatedAuthFailureException extends IoTPotBaseException {
    private String token;
    private String ipAddress;

    public BlackListedDueToRepeatedAuthFailureException(String token, String ipAddress) {
        super(HttpStatus.FORBIDDEN_403, String.format("%s is invalid authtoken from ipaddress %s. Blocked for next half an hour.",
                token,
                ipAddress));
        this.token = token;
        this.ipAddress = ipAddress;
        shouldLog(false);
    }
}
